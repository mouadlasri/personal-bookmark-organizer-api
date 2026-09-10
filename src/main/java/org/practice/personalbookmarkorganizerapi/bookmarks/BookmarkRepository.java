package org.practice.personalbookmarkorganizerapi.bookmarks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    // An entity graph tells Hibernate which lazy relationships this query needs.
    // "bookmarkTags" loads the linking entities, while "bookmarkTags.tag" also
    // loads each actual Tag. This prevents one extra query for every bookmark.
    // Hibernate 7.4 paginates the bookmarks before joining this collection, so
    // the page still contains the requested number of distinct bookmarks.
    @EntityGraph(attributePaths = {"bookmarkTags", "bookmarkTags.tag"})
    Page<Bookmark> findAllByCollection_Id(UUID collectionId, Pageable pageable);

    @EntityGraph(attributePaths = {"bookmarkTags", "bookmarkTags.tag"})
    Page<Bookmark> findAllByCollection_IdAndStatus(UUID collectionId, BookmarkStatus status, Pageable pageable);

    // A BookmarkResponse contains tags, so load the same relationships when a
    // single bookmark is retrieved as well.
    @EntityGraph(attributePaths = {"bookmarkTags", "bookmarkTags.tag"})
    Optional<Bookmark> findByIdAndCollection_IdAndCollection_User_IdAndCollection_User_DeletedAtIsNull(UUID bookmarkId, UUID collectionId, UUID userId);
}
