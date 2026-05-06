package com.tukac.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    private String category;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "published_at")
    private LocalDateTime publishedAt = LocalDateTime.now();

    // URL/base64 for attached image or video
    @Column(name = "media_url", columnDefinition = "TEXT")
    private String mediaUrl;

    // 'image' or 'video'
    @Column(name = "media_type")
    private String mediaType;

    // Transient field for author name (not stored in DB)
    @Transient
    private String authorName;

    public BlogPost() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    // Alias: frontend sends "content", maps to body column
    public String getContent() { return body; }
    public void setContent(String content) { this.body = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
}
