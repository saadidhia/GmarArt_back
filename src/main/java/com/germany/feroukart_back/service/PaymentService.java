package com.germany.feroukart_back.service;

import tools.jackson.databind.JsonNode;
import com.germany.feroukart_back.dto.OrderRequest;
import com.germany.feroukart_back.entity.Order;
import com.germany.feroukart_back.entity.OrderItem;
import com.germany.feroukart_back.repository.OrderRepository;
import com.germany.feroukart_back.repository.PrintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final PrintRepository printRepository;
    private final PayPalClient payPalClient;

    @Value("${paypal.client-id}")
    private String clientId;

    public Map<String, String> getClientConfig() {
        return Map.of("clientId", clientId, "currency", "EUR");
    }

    /**
     * Validates the cart, persists a PENDING_PAYMENT order, then opens a matching PayPal order.
     */
    @Transactional
    public Map<String, Object> createPaypalOrder(OrderRequest request) {
        Order order = orderService.createOrder(request);

        JsonNode paypalOrder = payPalClient.createOrder(order.getTotalAmount(), order.getCurrency());
        String paypalOrderId = paypalOrder.get("id").asText();

        order.setPaypalOrderId(paypalOrderId);
        orderRepository.save(order);

        return Map.of("localOrderId", order.getId(), "paypalOrderId", paypalOrderId);
    }

    /**
     * Stock is only decremented here, once PayPal confirms the funds were actually captured.
     */
    @Transactional
    public Order captureOrder(String paypalOrderId) {
        Order order = orderRepository.findByPaypalOrderId(paypalOrderId)
                .orElseThrow(() -> new RuntimeException("No order found for PayPal order " + paypalOrderId));

        if ("COMPLETED".equals(order.getStatus())) {
            return order;
        }

        JsonNode capture = payPalClient.captureOrder(paypalOrderId);
        String captureStatus = capture.path("status").asText();

        if (!"COMPLETED".equals(captureStatus)) {
            order.setStatus("FAILED");
            orderRepository.save(order);
            throw new RuntimeException("PayPal payment was not completed (status: " + captureStatus + ")");
        }

        for (OrderItem item : order.getItems()) {
            if ("PRINT".equals(item.getItemType()) && item.getPrintId() != null) {
                int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
                int updated = printRepository.decrementStock(item.getPrintId(), quantity);
                if (updated == 0) {
                    log.warn("Insufficient stock to decrement for print {} (order {}); payment already captured",
                            item.getPrintId(), order.getId());
                }
            }
        }

        order.setStatus("COMPLETED");
        order.setPaypalCaptureId(extractCaptureId(capture));
        return orderRepository.save(order);
    }

    private String extractCaptureId(JsonNode capture) {
        return capture.path("purchase_units").path(0).path("payments").path("captures").path(0).path("id").asText(null);
    }
}
