package com.germany.feroukart_back.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    public String upload(String folder, MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        String key = folder + "/" + UUID.randomUUID() + extension;
        return putToS3(key, file);
    }

    private String putToS3(String key, MultipartFile file) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(key)
                            .contentType(file.getContentType()).contentLength(file.getSize()).build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
        log.info("Uploaded to S3: {}", url);
        return url;
    }

    public void deleteByUrl(String url) {
        String marker = ".amazonaws.com/";
        int idx = url.indexOf(marker);
        if (idx == -1) {
            log.warn("Could not extract S3 key from URL: {}", url);
            return;
        }
        String key = url.substring(idx + marker.length());
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        log.info("Deleted from S3: {}", key);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
