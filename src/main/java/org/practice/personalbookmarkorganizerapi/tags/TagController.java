package org.practice.personalbookmarkorganizerapi.tags;

import jakarta.validation.Valid;
import org.practice.personalbookmarkorganizerapi.tags.dto.CreateTagRequest;
import org.practice.personalbookmarkorganizerapi.tags.dto.TagResponse;
import org.practice.personalbookmarkorganizerapi.tags.dto.UpdateTagRequest;
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
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<TagResponse> tagResponsePage = tagService.getAllTags(userId, pageable);

        return ResponseEntity.ok(tagResponsePage);
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<TagResponse> getTagById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID tagId) {
        UUID userId = UUID.fromString(jwt.getSubject());

        TagResponse tagResponse = tagService.getTagById(userId, tagId);

        return ResponseEntity.ok(tagResponse);
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateTagRequest createTagRequest) {
        UUID userId = UUID.fromString(jwt.getSubject());

        TagResponse tagResponse = tagService.createTag(userId, createTagRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(tagResponse);
    }

    @PatchMapping("/{tagId}")
    public ResponseEntity<TagResponse> updateTag(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID tagId, @Valid @RequestBody UpdateTagRequest updateTagRequest) {
        UUID userId = UUID.fromString(jwt.getSubject());

        TagResponse tagResponse = tagService.updateTag(userId, tagId, updateTagRequest);

        return ResponseEntity.ok(tagResponse);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID tagId) {
        UUID userId = UUID.fromString(jwt.getSubject());

        tagService.deleteTag(userId, tagId);

        return ResponseEntity.noContent().build();
    }

}
