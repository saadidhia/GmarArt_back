package com.germany.feroukart_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "paintings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Painting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String technique;

    @Column
    private Integer year;

    @Column
    private String style;

    @Column
    private String artist;

    @Column
    private Double width;

    @Column(name = "height")
    private Double height;

    @Column
    private Double depth;

    @Column(name = "price")
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Multiple image URLs (up to 7)
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

    @Column(name = "image_url_6")
    private String imageUrl6;

    @Column(name = "image_url_7")
    private String imageUrl7;

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
        if (imageUrl6 != null) urls.add(imageUrl6);
        if (imageUrl7 != null) urls.add(imageUrl7);
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
        this.imageUrl6 = urls.size() > 5 ? urls.get(5) : null;
        this.imageUrl7 = urls.size() > 6 ? urls.get(6) : null;
    }
}
