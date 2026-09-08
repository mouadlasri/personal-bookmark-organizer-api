package org.practice.personalbookmarkorganizerapi.collections;

import org.practice.personalbookmarkorganizerapi.collections.dto.CollectionResponse;
import org.practice.personalbookmarkorganizerapi.collections.dto.CreateCollectionRequest;
import org.practice.personalbookmarkorganizerapi.collections.dto.UpdateCollectionRequest;
import org.practice.personalbookmarkorganizerapi.collections.exception.CollectionNameAlreadyExistsException;
import org.practice.personalbookmarkorganizerapi.collections.exception.CollectionNotFoundException;
import org.practice.personalbookmarkorganizerapi.collections.exception.InvalidCollectionNameException;
import org.practice.personalbookmarkorganizerapi.users.User;
import org.practice.personalbookmarkorganizerapi.users.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final UserService userService;

    public CollectionService(CollectionRepository collectionRepository, UserService userService) {
        this.collectionRepository = collectionRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Page<CollectionResponse> getAllCollections(UUID userId, Pageable pageable) {
        userService.getActiveUserEntityById(userId);

        Page<Collection> collectionPage = collectionRepository.findAllByUserId(userId, pageable);

        Page<CollectionResponse> collectionResponsePage = collectionPage.map(collection -> toCollectionResponse(collection));

        return collectionResponsePage;
    }

    @Transactional(readOnly = true)
    public CollectionResponse getCollectionByIdAndUserId(UUID collectionId, UUID userId) {
        userService.getActiveUserEntityById(userId);
        Collection collection = collectionRepository.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new CollectionNotFoundException());

        return toCollectionResponse(collection);
    }

    @Transactional
    public CollectionResponse createCollection(UUID userId, CreateCollectionRequest createCollectionRequest) {
        User user = userService.getActiveUserEntityById(userId);

        if (collectionRepository.existsByUser_IdAndName(user.getId(), createCollectionRequest.getName())) {
            throw new CollectionNameAlreadyExistsException(createCollectionRequest.getName());
        }

        Collection collection = new Collection(
                user,
                createCollectionRequest.getName(),
                createCollectionRequest.getDescription()
        );

        Collection newCollection = collectionRepository.save(collection);

        return toCollectionResponse(newCollection);
    }

    @Transactional
    public CollectionResponse updateCollection(UUID userId, UUID collectionId, UpdateCollectionRequest updateCollectionRequest) {
        userService.getActiveUserEntityById(userId);
        Collection collection = collectionRepository.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new CollectionNotFoundException());

        String name = updateCollectionRequest.getName();
        String description = updateCollectionRequest.getDescription();

        if (name != null) {
            if (name.isBlank()) {
                throw new InvalidCollectionNameException();
            }

            if (!name.equals(collection.getName())) {
                if (collectionRepository.existsByUser_IdAndName(userId, name)) {
                    throw new CollectionNameAlreadyExistsException(name);
                }

                collection.setName(name);
            }
        }

        if (description != null && !description.equals(collection.getDescription())) {
            collection.setDescription(description);
        }

        return toCollectionResponse(collection);
    }

    @Transactional
    public void deleteCollection(UUID userId, UUID collectionId) {
        userService.getActiveUserEntityById(userId);
        Collection collection = collectionRepository.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new CollectionNotFoundException());

        collectionRepository.delete(collection);
    }

    private CollectionResponse toCollectionResponse(Collection collection) {
        return new CollectionResponse(
                collection.getId(),
                collection.getName(),
                collection.getDescription(),
                collection.getCreatedAt(),
                collection.getUpdatedAt()
        );
    }
}
