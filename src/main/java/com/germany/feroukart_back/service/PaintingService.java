package com.germany.feroukart_back.service;


import com.germany.feroukart_back.entity.Painting;
import com.germany.feroukart_back.repository.PaintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private static final String UPLOAD_DIR = "uploads/paintings/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

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
     * Create new painting
     */
    @Transactional
    public Painting createPainting(Painting painting) {
        if (paintingRepository.existsByName(painting.getName())) {
            throw new RuntimeException("Painting with name '" + painting.getName() + "' already exists");
        }
        return paintingRepository.save(painting);
    }

    /**
     * Update painting
     */
    @Transactional
    public Painting updatePainting(UUID id, Painting updatedPainting) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));

        if (!painting.getName().equals(updatedPainting.getName()) &&
                paintingRepository.existsByName(updatedPainting.getName())) {
            throw new RuntimeException("Painting with name '" + updatedPainting.getName() + "' already exists");
        }

        painting.setName(updatedPainting.getName());
        painting.setTechnique(updatedPainting.getTechnique());
        painting.setYear(updatedPainting.getYear());
        painting.setPrintSize(updatedPainting.getPrintSize());
        painting.setPrintPrice(updatedPainting.getPrintPrice());
        painting.setOriginalAvailable(updatedPainting.isOriginalAvailable());
        painting.setOriginalPrice(updatedPainting.getOriginalPrice());

        return paintingRepository.save(painting);
    }

    /**
     * Delete painting
     */
    @Transactional
    public void deletePainting(UUID id) {
        if (!paintingRepository.existsById(id)) {
            throw new RuntimeException("Painting not found with id: " + id);
        }
        paintingRepository.deleteById(id);
    }

    /**
     * Save painting image and return URL
     */
    public String savePaintingImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed size of 5MB");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!isValidImageType(contentType)) {
            throw new RuntimeException("Invalid file type. Only JPG, PNG, WEBP, GIF are allowed");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String fileName = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(fileName);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/paintings/" + fileName;
    }

    /**
     * Delete painting image
     */
    public void deletePaintingImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get paintings by technique
     */
    public List<Painting> getPaintingsByTechnique(String technique) {
        return paintingRepository.findByTechnique(technique);
    }

    /**
     * Get paintings with original available
     */
    public List<Painting> getPaintingsWithOriginalAvailable() {
        return paintingRepository.findByOriginalAvailableTrue();
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

    /**
     * Get file extension
     */
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") > 0) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return ".jpg";
    }
}