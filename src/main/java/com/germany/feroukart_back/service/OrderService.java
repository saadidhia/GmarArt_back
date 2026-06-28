package com.germany.feroukart_back.service;

import com.germany.feroukart_back.dto.OrderRequest;
import com.germany.feroukart_back.entity.Order;
import com.germany.feroukart_back.entity.OrderItem;
import com.germany.feroukart_back.entity.Print;
import com.germany.feroukart_back.repository.OrderRepository;
import com.germany.feroukart_back.repository.PrintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PrintRepository printRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }
        if (isBlank(request.getShippingStreet()) || isBlank(request.getShippingHouseNumber())
                || isBlank(request.getShippingPostalCode()) || isBlank(request.getShippingRegion())
                || isBlank(request.getShippingCountry())) {
            throw new RuntimeException("Shipping street, house number, postal code, region and country are required");
        }

        Order order = new Order();
        order.setBuyerName(request.getBuyerName());
        order.setBuyerEmail(request.getBuyerEmail());
        order.setShippingStreet(request.getShippingStreet());
        order.setShippingHouseNumber(request.getShippingHouseNumber());
        order.setShippingPostalCode(request.getShippingPostalCode());
        order.setShippingRegion(request.getShippingRegion());
        order.setShippingCountry(request.getShippingCountry());
        order.setCurrency("EUR");

        double total = 0;
        for (OrderRequest.Item item : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            int quantity = 1;

            if (item.getPrintId() != null) {
                quantity = (item.getQuantity() != null && item.getQuantity() > 0) ? item.getQuantity() : 1;
                Print print = printRepository.findById(item.getPrintId())
                        .orElseThrow(() -> new RuntimeException("Print '" + item.getPrintName() + "' no longer exists"));
                if (print.getStock() < quantity) {
                    throw new RuntimeException("Only " + print.getStock() + " left in stock for '" + item.getPrintName() + "'");
                }
                orderItem.setItemType("PRINT");
                orderItem.setPrintId(item.getPrintId());
                orderItem.setPrintName(item.getPrintName());
            } else {
                orderItem.setItemType("PAINTING");
                orderItem.setPaintingId(item.getPaintingId());
                orderItem.setPaintingName(item.getPaintingName());
            }

            orderItem.setQuantity(quantity);
            orderItem.setImageUrl(item.getImageUrl());
            orderItem.setPrice(item.getPrice());
            order.addItem(orderItem);
            total += (item.getPrice() != null ? item.getPrice() : 0) * quantity;
        }
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
