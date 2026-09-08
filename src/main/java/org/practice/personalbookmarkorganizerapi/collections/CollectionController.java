package org.practice.personalbookmarkorganizerapi.collections;

import jakarta.validation.Valid;
import org.practice.personalbookmarkorganizerapi.collections.dto.CollectionResponse;
import org.practice.personalbookmarkorganizerapi.collections.dto.CreateCollectionRequest;
import org.practice.personalbookmarkorganizerapi.collections.dto.UpdateCollectionRequest;
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
@RequestMapping("/api/v1/collections")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public ResponseEntity<Page<CollectionResponse>> getAllCollections(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CollectionResponse> collectionResponsePage = collectionService.getAllCollections(userId, pageable);

        return ResponseEntity.ok(collectionResponsePage);
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<CollectionResponse> getCollectionById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID collectionId) {
        UUID userId = UUID.fromString(jwt.getSubject());

        CollectionResponse collectionResponse = collectionService.getCollectionByIdAndUserId(collectionId, userId);

        return ResponseEntity.ok(collectionResponse);
    }

    @PostMapping
    public ResponseEntity<CollectionResponse> createCollection(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateCollectionRequest createCollectionRequest) {
        UUID userId = UUID.fromString(jwt.getSubject());

        CollectionResponse collectionResponse = collectionService.createCollection(userId, createCollectionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(collectionResponse);
    }

    @PatchMapping("/{collectionId}")
    public ResponseEntity<CollectionResponse> updateCollection(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID collectionId, @Valid @RequestBody UpdateCollectionRequest updateCollectionRequest) {
        UUID userId = UUID.fromString(jwt.getSubject());

        CollectionResponse collectionResponse = collectionService.updateCollection(userId, collectionId, updateCollectionRequest);

        return ResponseEntity.ok(collectionResponse);
    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Void> deleteCollection(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID collectionId) {
        UUID userId = UUID.fromString(jwt.getSubject());

        collectionService.deleteCollection(userId, collectionId);

        return ResponseEntity.noContent().build();
    }
}
