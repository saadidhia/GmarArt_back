package com.germany.feroukart_back.repository;

import com.germany.feroukart_back.entity.CommissionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommissionRequestRepository extends JpaRepository<CommissionRequest, UUID> {

    List<CommissionRequest> findAllByOrderByCreatedAtDesc();
}
