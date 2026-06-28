package com.germany.feroukart_back.service;


import com.germany.feroukart_back.entity.Print;
import com.germany.feroukart_back.repository.PrintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrintService {

    private final PrintRepository printRepository;
    private final S3Service s3Service;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final int MAX_IMAGES = 5;

    /**
     * Get all prints
     */
    public List<Print> getAllPrints() {
        return printRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get print by ID
     */
    public Optional<Print> getPrintById(UUID id) {
        return printRepository.findById(id);
    }

    /**
     * Get print by name
     */
    public Optional<Print> getPrintByName(String name) {
        return printRepository.findByName(name);
    }

    /**
     * Create new print and upload its images into an S3 folder named after
     * the print's generated id and name, nested under the "prints" folder.
     */
    @Transactional
    public Print createPrint(Print print, MultipartFile[] images) {
        if (printRepository.existsByName(print.getName())) {
            throw new RuntimeException("Print with name '" + print.getName() + "' already exists");
        }

        // Save first so the id is generated before we build the S3 folder name.
        Print saved = printRepository.save(print);

        if (images != null && images.length > 0) {
            saved.setAllImageUrls(uploadImages(saved, images));
            saved = printRepository.save(saved);
        }

        return saved;
    }

    /**
     * Update print. If new images are provided, the print's existing
     * images are removed from S3 and replaced with the new ones.
     */
    @Transactional
    public Print updatePrint(UUID id, Print updatedPrint, MultipartFile[] images) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found with id: " + id));

        if (!print.getName().equals(updatedPrint.getName()) &&
                printRepository.existsByName(updatedPrint.getName())) {
            throw new RuntimeException("Print with name '" + updatedPrint.getName() + "' already exists");
        }

        print.setName(updatedPrint.getName());
        print.setHeight(updatedPrint.getHeight());
        print.setWidth(updatedPrint.getWidth());
        print.setPrice(updatedPrint.getPrice());
        if (updatedPrint.getStock() != null) print.setStock(updatedPrint.getStock());

        if (images != null && images.length > 0) {
            print.getAllImageUrls().forEach(s3Service::deleteByUrl);
            print.setAllImageUrls(uploadImages(print, images));
        }

        return printRepository.save(print);
    }

    /**
     * Delete print and its images from S3
     */
    @Transactional
    public void deletePrint(UUID id) {
        Print print = printRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Print not found with id: " + id));
        // Best-effort: if S3 deletion fails (e.g. credentials temporarily locked down),
        // still remove the print from the database rather than blocking the whole operation.
        print.getAllImageUrls().forEach(url -> {
            try {
                s3Service.deleteByUrl(url);
            } catch (Exception e) {
                log.warn("Failed to delete S3 image {} for print {}: {}", url, id, e.getMessage());
            }
        });
        printRepository.deleteById(id);
    }

    /**
     * Validate and upload each image to the print's S3 folder, returning the resulting URLs.
     */
    private List<String> uploadImages(Print print, MultipartFile[] images) {
        if (images.length > MAX_IMAGES) {
            throw new RuntimeException("A maximum of " + MAX_IMAGES + " images are allowed");
        }

        String folder = buildFolderName(print.getId(), print.getName());
        List<String> urls = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;
            validateImage(image);
            urls.add(s3Service.upload(folder, image));
        }
        return urls;
    }

    /**
     * Build the per-print S3 folder name under the "prints" parent folder:
     * prints/{first 5 chars of id}-{sanitized name}.
     */
    private String buildFolderName(UUID id, String name) {
        String idPrefix = id.toString().substring(0, 5);
        String sanitizedName = name.replaceAll("[^a-zA-Z0-9]+", "_");
        return "prints/" + idPrefix + "-" + sanitizedName;
    }

    private void validateImage(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
        if (!isValidImageType(file.getContentType())) {
            throw new RuntimeException("Invalid file type. Only JPG, PNG, WEBP, GIF are allowed");
        }
    }

    /**
     * Search prints
     */
    public List<Print> searchPrints(String searchTerm) {
        return printRepository.searchPrints(searchTerm);
    }

    /**
     * Check if file type is valid
     */
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/webp") ||
                        contentType.equals("image/gif")
        );
    }
}
