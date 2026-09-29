package com.back.domain.post.post.repository;

import com.back.domain.post.post.document.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PostRepository extends ElasticsearchRepository<Post, String> {

    List<Post> findAll();
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTitleContaining(String keyword, Pageable pageable);
    Page<Post> findByContentContaining(String keyword, Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

}
