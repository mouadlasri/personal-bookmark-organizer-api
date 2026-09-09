package org.practice.personalbookmarkorganizerapi.bookmarks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    Page<Bookmark> findAllByCollection_Id(UUID collectionId, Pageable pageable);

    Optional<Bookmark> findByIdAndCollection_IdAndCollection_User_IdAndCollection_User_DeletedAtIsNull(UUID bookmarkId, UUID collectionId, UUID userId);
}
