package com.tukac.repository;

import com.tukac.model.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {
    List<BlogComment> findByPostIdOrderByCreatedAtAsc(Long postId);
    long countByPostId(Long postId);
    void deleteAllByPostId(Long postId);
}
