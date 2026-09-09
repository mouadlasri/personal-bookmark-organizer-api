package org.practice.personalbookmarkorganizerapi.tags;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Page<Tag> findAllByUser_IdAndUser_DeletedAtIsNull(UUID userId, Pageable pageable);

    Optional<Tag> findByIdAndUser_IdAndUser_DeletedAtIsNull(UUID tagId, UUID userId);

    boolean existsByNameAndUser_IdAndUser_DeletedAtIsNull(String name, UUID userId);
}
