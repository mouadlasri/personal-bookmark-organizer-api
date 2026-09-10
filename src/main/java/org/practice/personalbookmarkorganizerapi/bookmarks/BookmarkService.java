package org.practice.personalbookmarkorganizerapi.bookmarks;

import org.practice.personalbookmarkorganizerapi.bookmarks.dto.BookmarkResponse;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.BookmarkTagResponse;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.CreateBookmarkRequest;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.UpdateBookmarkRequest;
import org.practice.personalbookmarkorganizerapi.bookmarks.exception.BookmarkNotFoundException;
import org.practice.personalbookmarkorganizerapi.bookmarks.exception.InvalidBookmarkTitleException;
import org.practice.personalbookmarkorganizerapi.bookmarks.exception.InvalidBookmarkUrlException;
import org.practice.personalbookmarkorganizerapi.collections.Collection;
import org.practice.personalbookmarkorganizerapi.collections.CollectionRepository;
import org.practice.personalbookmarkorganizerapi.collections.CollectionService;
import org.practice.personalbookmarkorganizerapi.tags.Tag;
import org.practice.personalbookmarkorganizerapi.tags.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookmarkService {
    private final CollectionRepository collectionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CollectionService collectionService;
    private final TagService tagService;

    public BookmarkService(BookmarkRepository bookmarkRepository, CollectionService collectionService, CollectionRepository collectionRepository, TagService tagService) {
        this.bookmarkRepository = bookmarkRepository;
        this.collectionService = collectionService;
        this.collectionRepository = collectionRepository;
        this.tagService = tagService;
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getAllBookmarks(UUID userId, UUID collectionId, Pageable pageable, BookmarkStatus status) {
        // confirm that the collection exists and belongs to this user
        collectionService.getCollectionByIdAndUserId(collectionId, userId);

        // The repository's entity graph loads each bookmark together with its
        // BookmarkTag links and Tag entities. Accessing the tags while mapping
        // the responses therefore does not trigger an extra query per bookmark.
        Page<Bookmark> bookmarkPage;
        if (status == null) {
            bookmarkPage = bookmarkRepository.findAllByCollection_Id(collectionId, pageable);
        } else {
            bookmarkPage = bookmarkRepository.findAllByCollection_IdAndStatus(collectionId, status, pageable);
        }

        // Page.map keeps the existing page number, size, and total count while
        // converting each Bookmark entity into the API's BookmarkResponse DTO.
        return bookmarkPage.map(bookmark -> toBookmarkResponse(bookmark));
    }

    @Transactional(readOnly = true)
    public BookmarkResponse getBookmarkById(UUID userId, UUID collectionId, UUID bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_IdAndCollection_User_DeletedAtIsNull(bookmarkId, collectionId, userId)
                .orElseThrow(() -> new BookmarkNotFoundException());

        return toBookmarkResponse(bookmark);
    }

    @Transactional
    public BookmarkResponse createBookmark(UUID userId, UUID collectionId, CreateBookmarkRequest createBookmarkRequest) {
        Collection collection = collectionService.getCollectionEntityByIdAndUserId(collectionId, userId);

        Bookmark bookmark = new Bookmark(
                collection,
                createBookmarkRequest.getUrl(),
                createBookmarkRequest.getTitle(),
                createBookmarkRequest.getNotes()
        );

        Bookmark newBookmark = bookmarkRepository.save(bookmark);

        return toBookmarkResponse(newBookmark);
    }

    @Transactional
    public BookmarkResponse updateBookmark(UUID userId, UUID collectionId, UUID bookmarkId, UpdateBookmarkRequest updateBookmarkRequest) {
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_IdAndCollection_User_DeletedAtIsNull(bookmarkId, collectionId, userId)
                .orElseThrow(() -> new BookmarkNotFoundException());
        String url = updateBookmarkRequest.getUrl();
        String title = updateBookmarkRequest.getTitle();
        String notes = updateBookmarkRequest.getNotes();
        BookmarkStatus status = updateBookmarkRequest.getStatus();

        if (url != null) {
            if (url.isBlank()) {
                throw new InvalidBookmarkUrlException();
            }

            if (!url.equals(bookmark.getUrl())) {
                bookmark.setUrl(url);
            }
        }

        if (title != null) {
            if (title.isBlank()) {
                throw new InvalidBookmarkTitleException();
            }

            if (!title.equals(bookmark.getTitle())) {
                bookmark.setTitle(title);
            }
        }

        if (notes != null && !notes.equals(bookmark.getNotes())) {
            bookmark.setNotes(notes);
        }

        if (status != null && status != bookmark.getStatus()) {
            bookmark.setStatus(status);
        }

        return toBookmarkResponse(bookmark);
    }

    @Transactional
    public void deleteBookmark(UUID userId, UUID collectionId, UUID bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_IdAndCollection_User_DeletedAtIsNull(bookmarkId, collectionId, userId)
                .orElseThrow(() -> new BookmarkNotFoundException());

        bookmarkRepository.delete(bookmark);
    }

    @Transactional
    public BookmarkResponse addTagToBookmark(UUID userId, UUID collectionId, UUID bookmarkId, UUID tagId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_IdAndCollection_User_DeletedAtIsNull(bookmarkId, collectionId, userId)
                .orElseThrow(() -> new BookmarkNotFoundException());
        Tag tag = tagService.getTagEntityById(userId, tagId);

        boolean alreadyAttached = bookmark.getBookmarkTags()
                .stream()
                .anyMatch(bookmarkTag -> bookmarkTag.getTag().getId().equals(tagId));

        if (!alreadyAttached) {
            bookmark.addTag(tag);
        }

        return toBookmarkResponse(bookmark);
    }

    @Transactional
    public void removeTagFromBookmark(UUID userId, UUID collectionId, UUID bookmarkId, UUID tagId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_IdAndCollection_User_DeletedAtIsNull(bookmarkId, collectionId, userId)
                .orElseThrow(() -> new BookmarkNotFoundException());

        bookmark.removeTag(tagId);
    }

    private BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        List<BookmarkTagResponse> tags = bookmark.getBookmarkTags()
                .stream()
                .map(bookmarkTag -> new BookmarkTagResponse(
                        bookmarkTag.getTag().getId(),
                        bookmarkTag.getTag().getName()
                ))
                .toList();

        return new BookmarkResponse(
                bookmark.getId(),
                bookmark.getUrl(),
                bookmark.getTitle(),
                bookmark.getNotes(),
                bookmark.getStatus(),
                tags,
                bookmark.getCreatedAt(),
                bookmark.getUpdatedAt()
        );
    }
}
