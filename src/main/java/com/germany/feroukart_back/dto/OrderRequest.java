package com.germany.feroukart_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String buyerName;
    private String buyerEmail;
    private String shippingAddress;
    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private UUID paintingId;
        private String paintingName;
        private String imageUrl;
        private Double price;
    }
}
