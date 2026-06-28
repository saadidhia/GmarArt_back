package com.germany.feroukart_back.controller;

import com.germany.feroukart_back.dto.OrderRequest;
import com.germany.feroukart_back.entity.Order;
import com.germany.feroukart_back.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/paypal")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Public PayPal client id + currency, used by the frontend to load the PayPal JS SDK.
     */
    @GetMapping("/client-id")
    public ResponseEntity<?> getClientConfig() {
        return ResponseEntity.ok(paymentService.getClientConfig());
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        try {
            return ResponseEntity.ok(paymentService.createPaypalOrder(request));
        } catch (Exception e) {
            log.error("Error creating PayPal order: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/capture-order/{paypalOrderId}")
    public ResponseEntity<?> captureOrder(@PathVariable String paypalOrderId) {
        try {
            Order order = paymentService.captureOrder(paypalOrderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error capturing PayPal order: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
