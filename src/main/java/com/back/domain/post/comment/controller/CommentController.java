package com.back.domain.post.comment.controller;

import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.comment.service.CommentService;
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
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final PostService postService;
    private final CommentService commentService;

    public record CreateCommentRequest(
            @NotBlank(message = "Content must not be blank") @Size(max = 500, min = 1) String content,
            @NotBlank(message = "Author must not be blank") @Size(max = 50, min = 1) String author
    ) {}

    public record UpdateCommentRequest(
            @NotBlank(message = "Content must not be blank") @Size(max = 500, min = 1) String content
    ) {}

    @GetMapping
    public ResponseEntity<Page<Comment>> findByPostId(@PathVariable String postId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        postService.findById(postId);
        return ResponseEntity.ok().body(commentService.findByPostId(PageRequest.of(page, size), postId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Comment>> findByPostId(@PathVariable String postId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) CommentService.SearchType searchType) {
        postService.findById(postId);
        if (keyword != null && !keyword.isBlank()) {
            if (searchType == null) {searchType = CommentService.SearchType.CONTENT_AUTHOR;}
            return ResponseEntity.ok(commentService.search(postId, keyword, searchType, PageRequest.of(page, size)));
        }

        return ResponseEntity.ok().body(commentService.findByPostId(PageRequest.of(page, size), postId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> findById(@PathVariable String postId, @PathVariable String id) {
        postService.findById(postId);
        return ResponseEntity.ok().body(commentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@PathVariable String postId, @Valid @RequestBody CreateCommentRequest request) {
        postService.findById(postId);
        Comment comment = commentService.create(postService.findById(postId), request.content, request.author);
        return ResponseEntity.status(201).body(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> update(@PathVariable String postId, @PathVariable String id, @Valid @RequestBody UpdateCommentRequest request) {
        postService.findById(postId);
        return ResponseEntity.ok().body(commentService.update(id, request.content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String postId, @PathVariable String id) {
        postService.findById(postId);
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
