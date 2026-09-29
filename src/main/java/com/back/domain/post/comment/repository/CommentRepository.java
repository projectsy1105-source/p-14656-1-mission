package com.back.domain.post.comment.repository;

import com.back.domain.post.comment.document.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CommentRepository extends ElasticsearchRepository<Comment, String> {

    List<Comment> findAll();
    Page<Comment> findAll(Pageable pageable);
    List<Comment> findAllByPostId(String postId);
    Page<Comment> findAllByPostId(String postId, Pageable pageable);

}
