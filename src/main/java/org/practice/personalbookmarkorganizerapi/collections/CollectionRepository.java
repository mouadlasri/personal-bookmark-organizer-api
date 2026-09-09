package org.practice.personalbookmarkorganizerapi.collections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CollectionRepository extends JpaRepository<Collection, UUID> {
    Page<Collection> findAllByUser_Id(UUID userId, Pageable pageable);

    @Query("SELECT c FROM Collection c WHERE c.id = :collectionId AND c.user.id = :userId")
    Optional<Collection> findByIdAndUserId(@Param("collectionId") UUID collectionId, @Param("userId") UUID userId);

    boolean existsByUser_IdAndName(UUID userId, String name);
}
