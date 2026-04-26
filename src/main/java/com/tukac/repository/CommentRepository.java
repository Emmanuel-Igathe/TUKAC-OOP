package com.tukac.repository;

import com.tukac.model.Comment;
import com.tukac.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBlogPostOrderByCreatedAtAsc(BlogPost blogPost);
}
