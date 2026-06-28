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

    @Column(name = "item_type", nullable = false)
    private String itemType;

    @Column(name = "painting_id")
    private UUID paintingId;

    @Column(name = "painting_name")
    private String paintingName;

    @Column(name = "print_id")
    private UUID printId;

    @Column(name = "print_name")
    private String printName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity = 1;
}
