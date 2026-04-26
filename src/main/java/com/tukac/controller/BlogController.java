package com.tukac.controller;

import com.tukac.model.BlogPost;
import com.tukac.model.Comment;
import com.tukac.model.User;
import com.tukac.repository.BlogPostRepository;
import com.tukac.repository.CommentRepository;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @Autowired private BlogPostRepository blogRepo;
    @Autowired private CommentRepository commentRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/public")
    public ResponseEntity<List<BlogPost>> getAllPosts(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(blogRepo.findByCategoryOrderByCreatedAtDesc(category));
        }
        return ResponseEntity.ok(blogRepo.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id) {
        BlogPost post = blogRepo.findById(id).orElseThrow();
        List<Comment> comments = commentRepo.findByBlogPostOrderByCreatedAtAsc(post);
        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("comments", comments);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody BlogPost post, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        post.setAuthor(user);
        BlogPost saved = blogRepo.save(post);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody Map<String, String> payload, @AuthenticationPrincipal UserDetails principal) {
        BlogPost post = blogRepo.findById(id).orElseThrow();
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        Comment comment = new Comment();
        comment.setContent(payload.get("content"));
        comment.setBlogPost(post);
        comment.setAuthor(user);
        Comment saved = commentRepo.save(comment);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        blogRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Post deleted"));
    }
}
