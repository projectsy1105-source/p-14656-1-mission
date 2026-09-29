package com.back.domain.post.post.service;

import com.back.domain.post.post.document.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public enum SearchType {
        TITLE,
        CONTENT,
        TITLE_CONTENT,
    }

    public Page<Post> search(String keyword, String type, Pageable pageable) {
        return switch (type) {
            case "title" -> postRepository.findByTitleContaining(keyword, pageable);
            case "content" -> postRepository.findByContentContaining(keyword, pageable);
            case "title_content" -> postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
            default -> postRepository.findAll(pageable);
        };
    }

    public long count() {
        return postRepository.count();
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post findById(String id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
    }

    public Post create(String title, String content, String author) {
        Post post = new Post(title, content, author);
        return postRepository.save(post);
    }

    public Post update(String id, String title, String content) {
        Post post = findById(id);
        if (title != null) {
            post.setTitle(title);
        }
        if (content != null) {
            post.setContent(content);
        }
        return postRepository.save(post);
    }

    public void delete(String id) {
        Post post = findById(id);
        postRepository.delete(post);
    }
}
