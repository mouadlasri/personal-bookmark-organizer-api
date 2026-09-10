package org.practice.personalbookmarkorganizerapi.bookmarks;

import jakarta.validation.Valid;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.BookmarkResponse;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.CreateBookmarkRequest;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.UpdateBookmarkRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collections/{collectionId}/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public ResponseEntity<Page<BookmarkResponse>> getAllBookmarks(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID collectionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) BookmarkStatus status
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<BookmarkResponse> bookmarkResponsePage = bookmarkService.getAllBookmarks(userId, collectionId, pageable, status);

        return ResponseEntity.ok(bookmarkResponsePage);
    }

    @GetMapping("/{bookmarkId}")
    public ResponseEntity<BookmarkResponse> getBookmarkById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID collectionId, @PathVariable UUID bookmarkId) {
        UUID userId = UUID.fromString(jwt.getSubject());

        BookmarkResponse bookmarkResponse = bookmarkService.getBookmarkById(userId, collectionId, bookmarkId);

        return ResponseEntity.ok(bookmarkResponse);
    }

    @PostMapping
    public ResponseEntity<BookmarkResponse> createBookmark(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID collectionId, @Valid @RequestBody CreateBookmarkRequest createBookmarkRequest) {
        UUID userId = UUID.fromString(jwt.getSubject());

        BookmarkResponse bookmarkResponse = bookmarkService.createBookmark(userId, collectionId, createBookmarkRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookmarkResponse);
    }

    @PatchMapping("/{bookmarkId}")
    public ResponseEntity<BookmarkResponse> updateBookmark(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID collectionId, @PathVariable UUID bookmarkId, @Valid @RequestBody UpdateBookmarkRequest updateBookmarkRequest) {
        UUID userId = UUID.fromString(jwt.getSubject());

        BookmarkResponse bookmarkResponse = bookmarkService.updateBookmark(userId, collectionId, bookmarkId, updateBookmarkRequest);

        return ResponseEntity.ok(bookmarkResponse);
    }

    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID collectionId, @PathVariable UUID bookmarkId) {
        UUID userId = UUID.fromString(jwt.getSubject());

        bookmarkService.deleteBookmark(userId, collectionId, bookmarkId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{bookmarkId}/tags/{tagId}")
    public ResponseEntity<BookmarkResponse> addTagToBookmark(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID collectionId,
            @PathVariable UUID bookmarkId,
            @PathVariable UUID tagId
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        BookmarkResponse bookmarkResponse = bookmarkService.addTagToBookmark(
                userId,
                collectionId,
                bookmarkId,
                tagId
        );

        return ResponseEntity.ok(bookmarkResponse);
    }

    @DeleteMapping("/{bookmarkId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromBookmark(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID collectionId,
            @PathVariable UUID bookmarkId,
            @PathVariable UUID tagId
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        bookmarkService.removeTagFromBookmark(userId, collectionId, bookmarkId, tagId);

        return ResponseEntity.noContent().build();
    }














}
