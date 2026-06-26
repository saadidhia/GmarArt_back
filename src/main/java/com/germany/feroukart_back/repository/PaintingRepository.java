package com.germany.feroukart_back.repository;


import com.germany.feroukart_back.entity.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, UUID> {

    /**
     * Find painting by name
     */
    Optional<Painting> findByName(String name);

    /**
     * Find paintings by technique
     */
    List<Painting> findByTechnique(String technique);

    /**
     * Find paintings ordered by creation date descending
     */
    List<Painting> findAllByOrderByCreatedAtDesc();

    /**
     * Find paintings with original available
     */
    List<Painting> findByOriginalAvailableTrue();

    /**
     * Search paintings by name
     */
    @Query("SELECT p FROM Painting p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Painting> searchPaintings(@Param("searchTerm") String searchTerm);

    /**
     * Check if painting name exists
     */
    boolean existsByName(String name);
}