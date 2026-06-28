package com.germany.feroukart_back.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Locale;

@Service
public class PayPalClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;

    private String cachedAccessToken;
    private Instant cachedTokenExpiry = Instant.EPOCH;

    public PayPalClient(RestTemplate restTemplate,
                         ObjectMapper objectMapper,
                         @Value("${paypal.base-url}") String baseUrl,
                         @Value("${paypal.client-id}") String clientId,
                         @Value("${paypal.client-secret}") String clientSecret) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public JsonNode createOrder(double amount, String currency) {
        ObjectNode amountNode = objectMapper.createObjectNode();
        amountNode.put("currency_code", currency);
        amountNode.put("value", String.format(Locale.US, "%.2f", amount));

        ObjectNode purchaseUnit = objectMapper.createObjectNode();
        purchaseUnit.set("amount", amountNode);

        ObjectNode body = objectMapper.createObjectNode();
        body.put("intent", "CAPTURE");
        body.set("purchase_units", objectMapper.createArrayNode().add(purchaseUnit));

        return postWithAuth("/v2/checkout/orders", body);
    }

    public JsonNode captureOrder(String paypalOrderId) {
        return postWithAuth("/v2/checkout/orders/" + paypalOrderId + "/capture", objectMapper.createObjectNode());
    }

    private synchronized String getAccessToken() {
        if (cachedAccessToken != null && Instant.now().isBefore(cachedTokenExpiry)) {
            return cachedAccessToken;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/v1/oauth2/token", request, String.class);
        JsonNode body = parse(response.getBody());

        cachedAccessToken = body.get("access_token").asText();
        int expiresIn = body.get("expires_in").asInt();
        cachedTokenExpiry = Instant.now().plusSeconds(Math.max(expiresIn - 60, 60));
        return cachedAccessToken;
    }

    private JsonNode postWithAuth(String path, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());

        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + path, request, String.class);
        return parse(response.getBody());
    }

    private JsonNode parse(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Invalid response from PayPal: " + e.getMessage(), e);
        }
    }
}
