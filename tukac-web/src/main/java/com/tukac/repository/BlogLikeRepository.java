package com.tukac.repository;

import com.tukac.model.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BlogLikeRepository extends JpaRepository<BlogLike, Long> {
    long countByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<BlogLike> findByPostIdAndUserId(Long postId, Long userId);
    void deleteAllByPostId(Long postId);
}
