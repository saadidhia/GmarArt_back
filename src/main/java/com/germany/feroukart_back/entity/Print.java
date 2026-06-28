package com.germany.feroukart_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Print {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "height")
    private Double height;

    @Column(name = "width")
    private Double width;

    @Column(name = "price")
    private Double price;

    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    // Multiple image URLs (up to 5)
    @Column(name = "image_url_1")
    private String imageUrl1;

    @Column(name = "image_url_2")
    private String imageUrl2;

    @Column(name = "image_url_3")
    private String imageUrl3;

    @Column(name = "image_url_4")
    private String imageUrl4;

    @Column(name = "image_url_5")
    private String imageUrl5;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Get all image URLs as a list
     */
    public java.util.List<String> getAllImageUrls() {
        java.util.List<String> urls = new java.util.ArrayList<>();
        if (imageUrl1 != null) urls.add(imageUrl1);
        if (imageUrl2 != null) urls.add(imageUrl2);
        if (imageUrl3 != null) urls.add(imageUrl3);
        if (imageUrl4 != null) urls.add(imageUrl4);
        if (imageUrl5 != null) urls.add(imageUrl5);
        return urls;
    }

    /**
     * Set all images from a list
     */
    public void setAllImageUrls(java.util.List<String> urls) {
        this.imageUrl1 = urls.size() > 0 ? urls.get(0) : null;
        this.imageUrl2 = urls.size() > 1 ? urls.get(1) : null;
        this.imageUrl3 = urls.size() > 2 ? urls.get(2) : null;
        this.imageUrl4 = urls.size() > 3 ? urls.get(3) : null;
        this.imageUrl5 = urls.size() > 4 ? urls.get(4) : null;
    }
}
