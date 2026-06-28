package com.germany.feroukart_back.controller;


import com.germany.feroukart_back.entity.Print;
import com.germany.feroukart_back.service.PrintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/prints")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PrintController {

    private final PrintService printService;

    /**
     * Get all prints
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllPrints() {
        try {
            List<Print> prints = printService.getAllPrints();
            return ResponseEntity.ok(prints);
        } catch (Exception e) {
            log.error("Error fetching prints: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching prints: " + e.getMessage()));
        }
    }

    /**
     * Get print by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPrintById(@PathVariable UUID id) {
        try {
            return printService.getPrintById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching print: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching print: " + e.getMessage()));
        }
    }

    /**
     * Get print by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getPrintByName(@PathVariable String name) {
        try {
            return printService.getPrintByName(name)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error fetching print: " + e.getMessage()));
        }
    }

    /**
     * Upload new print with images
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPrint(
            @RequestParam String name,
            @RequestParam Double width,
            @RequestParam Double height,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam(required = false) MultipartFile[] images) {
        try {
            log.info("Uploading print: {}", name);

            Print print = new Print();
            print.setName(name);
            print.setWidth(width);
            print.setHeight(height);
            print.setPrice(price);
            print.setStock(stock);

            Print savedPrint = printService.createPrint(print, images);

            log.info("Print uploaded successfully: {}", savedPrint.getId());

            return ResponseEntity.ok(savedPrint);

        } catch (Exception e) {
            log.error("Error uploading print: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error uploading print: " + e.getMessage()));
        }
    }

    /**
     * Update print
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrint(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double width,
            @RequestParam(required = false) Double height,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) MultipartFile[] images) {
        try {
            Print print = printService.getPrintById(id)
                    .orElseThrow(() -> new RuntimeException("Print not found"));

            if (name != null) print.setName(name);
            if (width != null) print.setWidth(width);
            if (height != null) print.setHeight(height);
            if (price != null) print.setPrice(price);
            if (stock != null) print.setStock(stock);

            Print updatedPrint = printService.updatePrint(id, print, images);
            return ResponseEntity.ok(updatedPrint);

        } catch (Exception e) {
            log.error("Error updating print: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error updating print: " + e.getMessage()));
        }
    }

    /**
     * Delete print
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrint(@PathVariable UUID id) {
        try {
            printService.deletePrint(id);
            return ResponseEntity.ok(Map.of("message", "Print deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error deleting print: " + e.getMessage()));
        }
    }

    /**
     * Search prints
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPrints(@RequestParam String q) {
        try {
            List<Print> prints = printService.searchPrints(q);
            return ResponseEntity.ok(prints);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error searching prints: " + e.getMessage()));
        }
    }
}
