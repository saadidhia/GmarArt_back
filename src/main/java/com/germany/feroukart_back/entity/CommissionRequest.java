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
@Table(name = "commission_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String street;

    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(name = "desired_size", nullable = false)
    private String desiredSize;

    @Column(nullable = false)
    private String style;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url_1")
    private String imageUrl1;

    @Column(name = "image_url_2")
    private String imageUrl2;

    @Column
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    public List<String> getAllImageUrls() {
        List<String> urls = new ArrayList<>();
        if (imageUrl1 != null) urls.add(imageUrl1);
        if (imageUrl2 != null) urls.add(imageUrl2);
        return urls;
    }

    public void setAllImageUrls(List<String> urls) {
        this.imageUrl1 = urls.size() > 0 ? urls.get(0) : null;
        this.imageUrl2 = urls.size() > 1 ? urls.get(1) : null;
    }
}
