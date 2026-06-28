package com.germany.feroukart_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @Column(name = "buyer_email", nullable = false)
    private String buyerEmail;

    @Column(name = "shipping_street")
    private String shippingStreet;

    @Column(name = "shipping_house_number")
    private String shippingHouseNumber;

    @Column(name = "shipping_postal_code")
    private String shippingPostalCode;

    @Column(name = "shipping_region")
    private String shippingRegion;

    @Column(name = "shipping_country")
    private String shippingCountry;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column
    private String currency;

    @Column
    private String status;

    @Column(name = "paypal_order_id")
    private String paypalOrderId;

    @Column(name = "paypal_capture_id")
    private String paypalCaptureId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING_PAYMENT";
        if (currency == null) currency = "EUR";
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
