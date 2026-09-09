package org.practice.personalbookmarkorganizerapi.bookmarks;

import org.practice.personalbookmarkorganizerapi.bookmarks.dto.BookmarkResponse;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.CreateBookmarkRequest;
import org.practice.personalbookmarkorganizerapi.bookmarks.dto.UpdateBookmarkRequest;
import org.practice.personalbookmarkorganizerapi.bookmarks.exception.BookmarkNotFoundException;
import org.practice.personalbookmarkorganizerapi.bookmarks.exception.InvalidBookmarkTitleException;
import org.practice.personalbookmarkorganizerapi.bookmarks.exception.InvalidBookmarkUrlException;
import org.practice.personalbookmarkorganizerapi.collections.Collection;
import org.practice.personalbookmarkorganizerapi.collections.CollectionRepository;
import org.practice.personalbookmarkorganizerapi.collections.CollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BookmarkService {
    private final CollectionRepository collectionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CollectionService collectionService;

    public BookmarkService(BookmarkRepository bookmarkRepository, CollectionService collectionService, CollectionRepository collectionRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.collectionService = collectionService;
        this.collectionRepository = collectionRepository;
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getAllBookmarks(UUID userId, UUID collectionId, Pageable pageable) {
        // check existing of collection and if collection belongs to this user
        collectionService.getCollectionByIdAndUserId(collectionId, userId);

        // fetch all bookmarks that belong to this collection
        Page<Bookmark> bookmarkPage = bookmarkRepository.findAllByCollection_Id(collectionId, pageable);

        Page<BookmarkResponse> bookmarkResponsePage = bookmarkPage.map(bookmark -> toBookmarkResponse(bookmark));

        return bookmarkResponsePage;
    }

    @Transactional(readOnly = true)
    public BookmarkResponse getBookmarkById(UUID userId, UUID collectionId, UUID bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_Id(bookmarkId, collectionId, userId)
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
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_Id(bookmarkId, collectionId, userId)
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

        if (!notes.equals(bookmark.getNotes())) {
            bookmark.setNotes(notes);
        }

        if (!status.equals(bookmark.getStatus())) {
            bookmark.setStatus(status);
        }

        return toBookmarkResponse(bookmark);
    }

    @Transactional
    public void deleteBookmark(UUID userId, UUID collectionId, UUID bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndCollection_IdAndCollection_User_Id(bookmarkId, collectionId, userId)
                .orElseThrow(() -> new BookmarkNotFoundException());

        bookmarkRepository.delete(bookmark);
    }

    private BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        return new BookmarkResponse(
                bookmark.getId(),
                bookmark.getUrl(),
                bookmark.getTitle(),
                bookmark.getNotes(),
                bookmark.getStatus(),
                bookmark.getCreatedAt(),
                bookmark.getUpdatedAt()
        );
    }
}
