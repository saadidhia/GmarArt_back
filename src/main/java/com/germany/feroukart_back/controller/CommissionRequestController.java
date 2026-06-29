package com.germany.feroukart_back.controller;

import com.germany.feroukart_back.entity.CommissionRequest;
import com.germany.feroukart_back.service.CommissionRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/commissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class CommissionRequestController {

    private final CommissionRequestService commissionRequestService;

    /**
     * Submit a custom piece commission request, with up to 2 reference images
     */
    @PostMapping
    public ResponseEntity<?> createCommissionRequest(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String street,
            @RequestParam String houseNumber,
            @RequestParam String city,
            @RequestParam String country,
            @RequestParam String desiredSize,
            @RequestParam String style,
            @RequestParam String subject,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile[] images) {
        try {
            CommissionRequest request = new CommissionRequest();
            request.setFullName(fullName);
            request.setEmail(email);
            request.setPhone(phone);
            request.setStreet(street);
            request.setHouseNumber(houseNumber);
            request.setCity(city);
            request.setCountry(country);
            request.setDesiredSize(desiredSize);
            request.setStyle(style);
            request.setSubject(subject);
            request.setDescription(description);

            CommissionRequest saved = commissionRequestService.createCommissionRequest(request, images);

            log.info("Commission request created: {} from {}", saved.getId(), saved.getEmail());

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Error creating commission request: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error creating commission request: " + e.getMessage()));
        }
    }

    /**
     * List all commission requests (admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllCommissionRequests() {
        try {
            List<CommissionRequest> requests = commissionRequestService.getAllCommissionRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get a single commission request by id (admin)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommissionRequestById(@PathVariable UUID id) {
        try {
            return commissionRequestService.getCommissionRequestById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
