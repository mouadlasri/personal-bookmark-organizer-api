package org.practice.personalbookmarkorganizerapi.bookmarks;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.practice.personalbookmarkorganizerapi.collections.Collection;
import org.practice.personalbookmarkorganizerapi.tags.Tag;

@Entity
@Table(name = "bookmarks")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @OneToMany(mappedBy = "bookmark", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookmarkTag> bookmarkTags = new ArrayList<>();

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "notes")
    private String notes;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookmarkStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private OffsetDateTime updatedAt;

    public Bookmark() {}

    public Bookmark(Collection collection, String url, String title, String notes) {
        this.collection = collection;
        this.url = url;
        this.title = title;
        this.notes = notes;
        this.status = BookmarkStatus.UNREAD;
    }

    public UUID getId() {
        return id;
    }

    public Collection getCollection() {
        return collection;
    }

    public List<BookmarkTag> getBookmarkTags() {
        return bookmarkTags;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public BookmarkStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(BookmarkStatus status) {
        this.status = status;
    }

    public void addTag(Tag tag) {
        BookmarkTag bookmarkTag = new BookmarkTag(this, tag);
        this.bookmarkTags.add(bookmarkTag);
    }
    public void removeTag(UUID tagId) {
        this.bookmarkTags.removeIf(
                bookmarkTag -> bookmarkTag.getTag().getId().equals(tagId)
        );
    }
}
