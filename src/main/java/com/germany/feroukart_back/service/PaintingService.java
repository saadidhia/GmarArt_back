package com.germany.feroukart_back.service;


import com.germany.feroukart_back.entity.Painting;
import com.germany.feroukart_back.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final S3Service s3Service;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final int MAX_IMAGES = 7;

    /**
     * Get all paintings
     */
    public List<Painting> getAllPaintings() {
        return paintingRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get painting by ID
     */
    public Optional<Painting> getPaintingById(UUID id) {
        return paintingRepository.findById(id);
    }

    /**
     * Get painting by name
     */
    public Optional<Painting> getPaintingByName(String name) {
        return paintingRepository.findByName(name);
    }

    /**
     * Create new painting and upload its images into an S3 folder named after
     * the painting's generated id and name.
     */
    @Transactional
    public Painting createPainting(Painting painting, MultipartFile[] images) {
        if (paintingRepository.existsByName(painting.getName())) {
            throw new RuntimeException("Painting with name '" + painting.getName() + "' already exists");
        }

        // Save first so the id is generated before we build the S3 folder name.
        Painting saved = paintingRepository.save(painting);

        if (images != null && images.length > 0) {
            saved.setAllImageUrls(uploadImages(saved, images));
            saved = paintingRepository.save(saved);
        }

        return saved;
    }

    /**
     * Update painting. If new images are provided, the painting's existing
     * images are removed from S3 and replaced with the new ones.
     */
    @Transactional
    public Painting updatePainting(UUID id, Painting updatedPainting, MultipartFile[] images) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));

        if (!painting.getName().equals(updatedPainting.getName()) &&
                paintingRepository.existsByName(updatedPainting.getName())) {
            throw new RuntimeException("Painting with name '" + updatedPainting.getName() + "' already exists");
        }

        painting.setName(updatedPainting.getName());
        painting.setTechnique(updatedPainting.getTechnique());
        painting.setYear(updatedPainting.getYear());
        painting.setStyle(updatedPainting.getStyle());
        painting.setArtist(updatedPainting.getArtist());
        painting.setWidth(updatedPainting.getWidth());
        painting.setHeight(updatedPainting.getHeight());
        painting.setDepth(updatedPainting.getDepth());
        painting.setPrice(updatedPainting.getPrice());
        painting.setDescription(updatedPainting.getDescription());

        if (images != null && images.length > 0) {
            painting.getAllImageUrls().forEach(s3Service::deleteByUrl);
            painting.setAllImageUrls(uploadImages(painting, images));
        }

        return paintingRepository.save(painting);
    }

    /**
     * Delete painting and its images from S3
     */
    @Transactional
    public void deletePainting(UUID id) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));
        painting.getAllImageUrls().forEach(s3Service::deleteByUrl);
        paintingRepository.deleteById(id);
    }

    /**
     * Validate and upload each image to the painting's S3 folder, returning the resulting URLs.
     */
    private List<String> uploadImages(Painting painting, MultipartFile[] images) {
        if (images.length > MAX_IMAGES) {
            throw new RuntimeException("A maximum of " + MAX_IMAGES + " images are allowed");
        }

        String folder = buildFolderName(painting.getId(), painting.getName());
        List<String> urls = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;
            validateImage(image);
            urls.add(s3Service.upload(folder, image));
        }
        return urls;
    }

    /**
     * Build the per-painting S3 folder name: first 5 chars of the id + sanitized name.
     */
    private String buildFolderName(UUID id, String name) {
        String idPrefix = id.toString().substring(0, 5);
        String sanitizedName = name.replaceAll("[^a-zA-Z0-9]+", "_");
        return idPrefix + "-" + sanitizedName;
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
     * Get paintings by technique
     */
    public List<Painting> getPaintingsByTechnique(String technique) {
        return paintingRepository.findByTechnique(technique);
    }

    /**
     * Search paintings
     */
    public List<Painting> searchPaintings(String searchTerm) {
        return paintingRepository.searchPaintings(searchTerm);
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
