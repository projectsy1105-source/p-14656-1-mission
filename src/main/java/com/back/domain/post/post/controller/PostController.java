package com.back.domain.post.post.controller;

import com.back.domain.post.post.document.Post;
import com.back.domain.post.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    record CreatePostRequest(
            @NotBlank(message = "Title must not be blank") @Size(max = 100, min = 1) String title,
            @NotBlank(message = "Content must not be blank") String content,
            @NotBlank(message = "Author must not be blank") String author) { }

    record UpdatePostRequest(
            @NotBlank(message = "Title must not be blank") @Size(max = 100, min = 1) String title,
            @NotBlank(message = "Content must not be blank") String content) { }

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody @Valid CreatePostRequest request) {
        Post post = postService.create(request.title, request.content, request.author);
        return ResponseEntity.status(201).body(post);
    }

    @RequestMapping
    public ResponseEntity<Page<Post>> findAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(postService.findAll(PageRequest.of(page, size)));
    }


    @RequestMapping("/search")
    public ResponseEntity<Page<Post>> search(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(defaultValue = "title_content") String searchType) {

        if (keyword != null && !keyword.isBlank() && searchType != null) {
            return ResponseEntity.ok(postService.search(keyword, searchType, PageRequest.of(page, size)));
        }

        return ResponseEntity.ok(postService.findAll(PageRequest.of(page, size)));
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Post> findById(@PathVariable String id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable String id, @RequestBody @Valid UpdatePostRequest request) {
        return ResponseEntity.ok(postService.update(id, request.title, request.content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
