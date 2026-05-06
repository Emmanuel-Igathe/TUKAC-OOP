package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.BlogComment;
import com.tukac.model.BlogLike;
import com.tukac.model.BlogPost;
import com.tukac.repository.BlogCommentRepository;
import com.tukac.repository.BlogLikeRepository;
import com.tukac.repository.BlogPostRepository;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @Autowired private BlogPostRepository blogPostRepository;
    @Autowired private BlogCommentRepository commentRepository;
    @Autowired private BlogLikeRepository likeRepository;
    @Autowired private UserRepository userRepository;

    // ── GET all posts ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPosts(Authentication auth) {
        Long userId = auth != null ? (Long) auth.getCredentials() : null;
        List<BlogPost> posts = blogPostRepository.findAllByOrderByPublishedAtDesc();

        List<Map<String, Object>> result = new ArrayList<>();
        for (BlogPost post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("body", post.getBody());
            map.put("category", post.getCategory());
            map.put("publishedAt", post.getPublishedAt());
            map.put("mediaUrl", post.getMediaUrl());
            map.put("mediaType", post.getMediaType());
            map.put("authorId", post.getAuthorId());
            if (post.getAuthorId() != null) {
                userRepository.findById(post.getAuthorId())
                        .ifPresent(u -> map.put("authorName", u.getName()));
            }
            map.put("likeCount", likeRepository.countByPostId(post.getId()));
            map.put("commentCount", commentRepository.countByPostId(post.getId()));
            map.put("liked", userId != null && likeRepository.existsByPostIdAndUserId(post.getId(), userId));
            result.add(map);
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── CREATE post ────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','SECRETARY')")
    public ResponseEntity<ApiResponse<BlogPost>> createPost(@RequestBody Map<String, Object> payload, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");
        String category = (String) payload.get("category");
        String mediaUrl = (String) payload.get("mediaUrl");
        String mediaType = (String) payload.get("mediaType");

        if (title == null || title.isBlank()) return ResponseEntity.badRequest().body(ApiResponse.error("Title is required"));
        if (content == null || content.isBlank()) return ResponseEntity.badRequest().body(ApiResponse.error("Content is required"));

        BlogPost post = new BlogPost();
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setAuthorId(userId);
        post.setMediaUrl(mediaUrl);
        post.setMediaType(mediaType);

        BlogPost saved = blogPostRepository.save(post);
        userRepository.findById(userId).ifPresent(u -> saved.setAuthorName(u.getName()));
        return ResponseEntity.ok(ApiResponse.ok("Post published", saved));
    }

    // ── UPDATE post ────────────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','SECRETARY')")
    public ResponseEntity<ApiResponse<BlogPost>> updatePost(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Optional<BlogPost> opt = blogPostRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        BlogPost post = opt.get();
        String title = (String) payload.get("title");
        String content = (String) payload.get("content");
        String category = (String) payload.get("category");

        if (title != null && !title.isBlank()) post.setTitle(title);
        if (content != null && !content.isBlank()) post.setContent(content);
        if (category != null) post.setCategory(category);
        if (payload.containsKey("mediaUrl")) post.setMediaUrl((String) payload.get("mediaUrl"));
        if (payload.containsKey("mediaType")) post.setMediaType((String) payload.get("mediaType"));

        BlogPost saved = blogPostRepository.save(post);
        if (saved.getAuthorId() != null) {
            userRepository.findById(saved.getAuthorId()).ifPresent(u -> saved.setAuthorName(u.getName()));
        }
        return ResponseEntity.ok(ApiResponse.ok("Post updated", saved));
    }

    // ── DELETE post ────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','SECRETARY')")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        if (!blogPostRepository.existsById(id)) return ResponseEntity.notFound().build();
        likeRepository.deleteAllByPostId(id);
        commentRepository.deleteAllByPostId(id);
        blogPostRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Post deleted", null));
    }

    // ── UPLOAD media ───────────────────────────────────────────────
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','SECRETARY')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            String contentType = file.getContentType();
            String mediaType;
            if (contentType != null && contentType.startsWith("video/")) mediaType = "video";
            else if (contentType != null && contentType.startsWith("image/")) mediaType = "image";
            else return ResponseEntity.badRequest().body(ApiResponse.error("Only image or video files are allowed"));

            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            String dataUrl = "data:" + contentType + ";base64," + base64;
            return ResponseEntity.ok(ApiResponse.ok("File uploaded", Map.of("mediaUrl", dataUrl, "mediaType", mediaType)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    // ── LIKE / UNLIKE ─────────────────────────────────────────────
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        if (!blogPostRepository.existsById(id)) return ResponseEntity.notFound().build();

        boolean liked;
        Optional<BlogLike> existing = likeRepository.findByPostIdAndUserId(id, userId);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            liked = false;
        } else {
            likeRepository.save(new BlogLike(id, userId));
            liked = true;
        }
        long count = likeRepository.countByPostId(id);
        return ResponseEntity.ok(ApiResponse.ok(liked ? "Liked" : "Unliked", Map.of("liked", liked, "likeCount", count)));
    }

    // ── GET comments ───────────────────────────────────────────────
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getComments(@PathVariable Long id) {
        List<BlogComment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(id);
        List<Map<String, Object>> result = new ArrayList<>();
        for (BlogComment c : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("postId", c.getPostId());
            map.put("authorId", c.getAuthorId());
            map.put("content", c.getContent());
            map.put("createdAt", c.getCreatedAt());
            userRepository.findById(c.getAuthorId()).ifPresent(u -> map.put("authorName", u.getName()));
            result.add(map);
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── POST comment ───────────────────────────────────────────────
    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        String content = body.get("content");
        if (content == null || content.isBlank())
            return ResponseEntity.badRequest().body(ApiResponse.error("Comment cannot be empty"));
        if (!blogPostRepository.existsById(id)) return ResponseEntity.notFound().build();

        BlogComment comment = new BlogComment();
        comment.setPostId(id);
        comment.setAuthorId(userId);
        comment.setContent(content.trim());
        BlogComment saved = commentRepository.save(comment);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId());
        result.put("postId", saved.getPostId());
        result.put("authorId", saved.getAuthorId());
        result.put("content", saved.getContent());
        result.put("createdAt", saved.getCreatedAt());
        userRepository.findById(userId).ifPresent(u -> result.put("authorName", u.getName()));

        return ResponseEntity.ok(ApiResponse.ok("Comment added", result));
    }

    // ── DELETE comment (own comment, or management role) ──────────
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        Optional<BlogComment> opt = commentRepository.findById(commentId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        BlogComment comment = opt.get();
        boolean isOwner = comment.getAuthorId().equals(userId);
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().matches("ROLE_(CHAIRPERSON|VICE-CHAIRPERSON|SECRETARY)"));

        if (!isOwner && !isManager)
            return ResponseEntity.status(403).body(ApiResponse.error("Not authorised to delete this comment"));

        commentRepository.deleteById(commentId);
        return ResponseEntity.ok(ApiResponse.ok("Comment deleted", null));
    }
}
