package com.germany.feroukart_back.repository;


import com.germany.feroukart_back.entity.Print;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrintRepository extends JpaRepository<Print, UUID> {

    /**
     * Find print by name
     */
    Optional<Print> findByName(String name);

    /**
     * Find prints ordered by creation date descending
     */
    List<Print> findAllByOrderByCreatedAtDesc();

    /**
     * Search prints by name
     */
    @Query("SELECT p FROM Print p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Print> searchPrints(@Param("searchTerm") String searchTerm);

    /**
     * Check if print name exists
     */
    boolean existsByName(String name);

    /**
     * Atomically decrement stock by the given quantity, only if enough stock is available.
     * Returns the number of rows updated (0 means insufficient stock).
     */
    @Modifying
    @Query("UPDATE Print p SET p.stock = p.stock - :qty WHERE p.id = :id AND p.stock >= :qty")
    int decrementStock(@Param("id") UUID id, @Param("qty") int qty);
}
