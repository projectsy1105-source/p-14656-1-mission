package com.back.domain.post.comment.service;

import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.comment.repository.CommentRepository;
import com.back.domain.post.post.document.Post;
import com.back.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public long count() {
        return commentRepository.count();
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public List<Comment> findByPostId(String postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public Comment findById(String id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("comment not found with id: " + id));
    }

    public Comment create(Post post, String content, String author) {
        Comment comment = new Comment(post.getId(), content, author);
        return commentRepository.save(comment);
    }

    public Comment update(String id, String content) {
        Comment comment = findById(id);
        if (comment != null) {
            comment.setContent(content);
        }
        return commentRepository.save(comment);
    }

    public void delete(String id) {
        Comment comment = findById(id);
        commentRepository.delete(comment);
    }
}
