package org.practice.personalbookmarkorganizerapi.tags;

import org.practice.personalbookmarkorganizerapi.tags.dto.CreateTagRequest;
import org.practice.personalbookmarkorganizerapi.tags.dto.TagResponse;
import org.practice.personalbookmarkorganizerapi.tags.dto.UpdateTagRequest;
import org.practice.personalbookmarkorganizerapi.tags.exception.InvalidTagNameException;
import org.practice.personalbookmarkorganizerapi.tags.exception.TagNameAlreadyExistsException;
import org.practice.personalbookmarkorganizerapi.tags.exception.TagNotFoundException;
import org.practice.personalbookmarkorganizerapi.users.User;
import org.practice.personalbookmarkorganizerapi.users.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final UserService userService;

    public TagService(TagRepository tagRepository, UserService userService) {
        this.tagRepository = tagRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Page<TagResponse> getAllTags(UUID userId, Pageable pageable) {
        Page<Tag> tagPage = tagRepository.findAllByUser_IdAndUser_DeletedAtIsNull(userId, pageable);

        Page<TagResponse> tagResponsePage = tagPage.map(tag -> toTagResponse(tag));

        return tagResponsePage;
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(UUID userId, UUID tagId) {
        Tag tag = tagRepository.findByIdAndUser_IdAndUser_DeletedAtIsNull(tagId, userId).orElseThrow(() -> new TagNotFoundException());

        return toTagResponse(tag);
    }

    @Transactional(readOnly = true)
    public Tag getTagEntityById(UUID userId, UUID tagId) {
        return tagRepository.findByIdAndUser_IdAndUser_DeletedAtIsNull(tagId, userId).orElseThrow(() -> new TagNotFoundException());
    }

    @Transactional
    public TagResponse createTag(UUID userId, CreateTagRequest createTagRequest) {
        User user = userService.getActiveUserEntityById(userId);

        String name = createTagRequest.getName();

        if (tagRepository.existsByNameAndUser_IdAndUser_DeletedAtIsNull(name, userId)) {
            throw new TagNameAlreadyExistsException();
        }

        Tag tag = new Tag(
                user,
                name
        );

        Tag newTag = tagRepository.save(tag);

        return toTagResponse(newTag);
    }

    @Transactional
    public TagResponse updateTag(UUID userId, UUID tagId, UpdateTagRequest updateTagRequest) {
        Tag tag = tagRepository.findByIdAndUser_IdAndUser_DeletedAtIsNull(tagId, userId).orElseThrow(() -> new TagNotFoundException());

        String name = updateTagRequest.getName();

        if (name != null) {
            if (name.isBlank()) {
                throw new InvalidTagNameException();
            }

            if (!name.equals(tag.getName())) {
                // check if another tag has the tag as the update request name
                if (tagRepository.existsByNameAndUser_IdAndUser_DeletedAtIsNull(name, userId)) {
                    throw new TagNameAlreadyExistsException();
                }

                tag.setName(name);
            }
        }

        return toTagResponse(tag);
    }

    @Transactional
    public void deleteTag(UUID userId, UUID tagId) {
        Tag tag = tagRepository.findByIdAndUser_IdAndUser_DeletedAtIsNull(tagId, userId).orElseThrow(() -> new TagNotFoundException());

        tagRepository.delete(tag);
    }

    private TagResponse toTagResponse(Tag tag) {
        return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.getCreatedAt(),
                tag.getUpdatedAt()
        );
    }
}
