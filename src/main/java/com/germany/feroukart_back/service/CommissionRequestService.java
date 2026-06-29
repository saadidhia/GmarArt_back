package com.germany.feroukart_back.service;

import com.germany.feroukart_back.entity.CommissionRequest;
import com.germany.feroukart_back.repository.CommissionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommissionRequestService {

    private final CommissionRequestRepository commissionRequestRepository;
    private final S3Service s3Service;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final int MAX_IMAGES = 2;

    public List<CommissionRequest> getAllCommissionRequests() {
        return commissionRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<CommissionRequest> getCommissionRequestById(UUID id) {
        return commissionRequestRepository.findById(id);
    }

    /**
     * Create a commission request and upload its reference images (up to 2)
     * into an S3 folder named after the request's generated id.
     */
    @Transactional
    public CommissionRequest createCommissionRequest(CommissionRequest request, MultipartFile[] images) {
        CommissionRequest saved = commissionRequestRepository.save(request);

        if (images != null && images.length > 0) {
            saved.setAllImageUrls(uploadImages(saved, images));
            saved = commissionRequestRepository.save(saved);
        }

        return saved;
    }

    private List<String> uploadImages(CommissionRequest request, MultipartFile[] images) {
        if (images.length > MAX_IMAGES) {
            throw new RuntimeException("A maximum of " + MAX_IMAGES + " reference images are allowed");
        }

        String folder = buildFolderName(request.getId());
        List<String> urls = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;
            validateImage(image);
            urls.add(s3Service.upload(folder, image));
        }
        return urls;
    }

    private String buildFolderName(UUID id) {
        return "commissions/" + id.toString().substring(0, 5);
    }

    private void validateImage(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
        if (!isValidImageType(file.getContentType())) {
            throw new RuntimeException("Invalid file type. Only JPG, PNG, WEBP, GIF are allowed");
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/webp") ||
                        contentType.equals("image/gif")
        );
    }
}
