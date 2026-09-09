package org.practice.personalbookmarkorganizerapi.bookmarks;

import jakarta.persistence.*;
import org.practice.personalbookmarkorganizerapi.tags.Tag;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookmark_tags")
public class BookmarkTag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bookmark_id", nullable = false)
    private Bookmark bookmark;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public BookmarkTag() {}

    public BookmarkTag(Bookmark bookmark, Tag tag) {
        this.bookmark = bookmark;
        this.tag = tag;
    }

    public UUID getId() {
        return id;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public Tag getTag() {
        return tag;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
