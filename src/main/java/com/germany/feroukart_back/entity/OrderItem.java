package com.germany.feroukart_back.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @Column(name = "painting_id", nullable = false)
    private UUID paintingId;

    @Column(name = "painting_name", nullable = false)
    private String paintingName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Double price;
}
