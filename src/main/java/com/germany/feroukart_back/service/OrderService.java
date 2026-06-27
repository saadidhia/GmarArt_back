package com.germany.feroukart_back.service;

import com.germany.feroukart_back.dto.OrderRequest;
import com.germany.feroukart_back.entity.Order;
import com.germany.feroukart_back.entity.OrderItem;
import com.germany.feroukart_back.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setBuyerName(request.getBuyerName());
        order.setBuyerEmail(request.getBuyerEmail());
        order.setShippingAddress(request.getShippingAddress());

        double total = 0;
        for (OrderRequest.Item item : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPaintingId(item.getPaintingId());
            orderItem.setPaintingName(item.getPaintingName());
            orderItem.setImageUrl(item.getImageUrl());
            orderItem.setPrice(item.getPrice());
            order.addItem(orderItem);
            total += item.getPrice() != null ? item.getPrice() : 0;
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
}
