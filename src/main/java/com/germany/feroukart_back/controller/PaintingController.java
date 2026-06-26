package com.germany.feroukart_back.controller;


import com.germany.feroukart_back.entity.Painting;
import com.germany.feroukart_back.service.PaintingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/paintings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PaintingController {

    private final PaintingService paintingService;

    /**
     * Get all paintings
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllPaintings() {
        try {
            List<Painting> paintings = paintingService.getAllPaintings();
            return ResponseEntity.ok(paintings);
        } catch (Exception e) {
            log.error("Error fetching paintings: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching paintings: " + e.getMessage()));
        }
    }

    /**
     * Get painting by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaintingById(@PathVariable UUID id) {
        try {
            return paintingService.getPaintingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching painting: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching painting: " + e.getMessage()));
        }
    }

    /**
     * Get painting by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getPaintingByName(@PathVariable String name) {
        try {
            return paintingService.getPaintingByName(name)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching painting: " + e.getMessage()));
        }
    }

    /**
     * Upload new painting with images
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPainting(
            @RequestParam String name,
            @RequestParam String technique,
            @RequestParam(required = false) Integer year,
            @RequestParam String printSize,
            @RequestParam Double printPrice,
            @RequestParam(required = false) boolean originalAvailable,
            @RequestParam(required = false) Double originalPrice,
            @RequestParam(required = false) MultipartFile[] images) {
        try {
            log.info("Uploading painting: {}", name);

            // Create painting entity
            Painting painting = new Painting();
            painting.setName(name);
            painting.setTechnique(technique);
            painting.setYear(year);
            painting.setPrintSize(printSize);
            painting.setPrintPrice(printPrice);
            painting.setOriginalAvailable(originalAvailable);
            painting.setOriginalPrice(originalPrice);

            // Handle image uploads
            if (images != null && images.length > 0) {
                java.util.List<String> imageUrls = new java.util.ArrayList<>();
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String imageUrl = paintingService.savePaintingImage(image);
                        imageUrls.add(imageUrl);
                    }
                }
                painting.setAllImageUrls(imageUrls);
            }

            // Save painting
            Painting savedPainting = paintingService.createPainting(painting);

            log.info("Painting uploaded successfully: {}", savedPainting.getId());

            return ResponseEntity.ok(savedPainting);

        } catch (Exception e) {
            log.error("Error uploading painting: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error uploading painting: " + e.getMessage()));
        }
    }

    /**
     * Update painting
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePainting(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String technique,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String printSize,
            @RequestParam(required = false) Double printPrice,
            @RequestParam(required = false) Boolean originalAvailable,
            @RequestParam(required = false) Double originalPrice,
            @RequestParam(required = false) MultipartFile[] images) {
        try {
            Painting painting = paintingService.getPaintingById(id)
                    .orElseThrow(() -> new RuntimeException("Painting not found"));

            if (name != null) painting.setName(name);
            if (technique != null) painting.setTechnique(technique);
            if (year != null) painting.setYear(year);
            if (printSize != null) painting.setPrintSize(printSize);
            if (printPrice != null) painting.setPrintPrice(printPrice);
            if (originalAvailable != null) painting.setOriginalAvailable(originalAvailable);
            if (originalPrice != null) painting.setOriginalPrice(originalPrice);

            // Handle new images
            if (images != null && images.length > 0) {
                java.util.List<String> imageUrls = new java.util.ArrayList<>();
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String imageUrl = paintingService.savePaintingImage(image);
                        imageUrls.add(imageUrl);
                    }
                }
                painting.setAllImageUrls(imageUrls);
            }

            Painting updatedPainting = paintingService.updatePainting(id, painting);
            return ResponseEntity.ok(updatedPainting);

        } catch (Exception e) {
            log.error("Error updating painting: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error updating painting: " + e.getMessage()));
        }
    }

    /**
     * Delete painting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePainting(@PathVariable UUID id) {
        try {
            paintingService.deletePainting(id);
            return ResponseEntity.ok(Map.of("message", "Painting deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error deleting painting: " + e.getMessage()));
        }
    }

    /**
     * Get paintings by technique
     */
    @GetMapping("/filter/technique/{technique}")
    public ResponseEntity<?> getPaintingsByTechnique(@PathVariable String technique) {
        try {
            List<Painting> paintings = paintingService.getPaintingsByTechnique(technique);
            return ResponseEntity.ok(paintings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching paintings: " + e.getMessage()));
        }
    }

    /**
     * Get paintings with original available
     */
    @GetMapping("/filter/original-available")
    public ResponseEntity<?> getPaintingsWithOriginalAvailable() {
        try {
            List<Painting> paintings = paintingService.getPaintingsWithOriginalAvailable();
            return ResponseEntity.ok(paintings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching paintings: " + e.getMessage()));
        }
    }

    /**
     * Search paintings
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPaintings(@RequestParam String q) {
        try {
            List<Painting> paintings = paintingService.searchPaintings(q);
            return ResponseEntity.ok(paintings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error searching paintings: " + e.getMessage()));
        }
    }
}