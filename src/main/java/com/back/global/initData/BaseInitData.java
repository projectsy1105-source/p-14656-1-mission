package com.back.global.initData;

import com.back.domain.post.comment.document.Comment;
import com.back.domain.post.comment.service.CommentService;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.post.document.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BaseInitData {

    private final PostService postService;
    private final CommentService commentService;

    @Bean
    public ApplicationRunner baseInitDataRunner() {
        return args -> {
            wokr1();
            work2();
            work3();
            work4();
            work5();
            work6();
            work7();
            work8();
            work9();
            work10();
            work11();
        };
    }

    private void wokr1() {
        log.debug("post entity 개수 : {}", postService.count());
        if (postService.count() == 0) {
            for (int i = 1; i <= 10; i++) {
                String title = "sample post title " + i;
                String content = "this is the content of sample post number " + i;
                String author = "Author" + i;
                Post post = postService.create(title, content, author);
                log.debug("created Post : {}", post);
            }
        }
    }

    private void work2(){
        log.debug("기존 Post 전체 조회");
        for (Post post : postService.findAll()) {
            log.debug("Existing Post: {}", post);
        }
    }

    private void work3(){
        log.debug("Post 단건 조회");
        for (Post post : postService.findAll()) {
            Post postRow = postService.findById(post.getId());
            log.debug("조회된 Post: {}", postRow);
        }
    }

    private void work4() {
        for (Post post : postService.findAll()) {
            String newTitle = post.getTitle() + " [updated]";
            String newContent = post.getContent() + " this content has been updated.";
            Post updatePost = postService.update(post.getId(), newTitle, newContent);
            log.debug("updated Post : {}", updatePost);
        }
    }

    private void work5() {
        log.debug("Post 삭제");
        for (Post post : postService.findAll()) {
            postService.delete(post.getId());
            log.debug("Deleted Post: {}", post.getId());
        }
        log.debug("삭제 후 Post 개수: {}", postService.count());
    }

    private void work6() {
        log.debug("comment 개수 : {}", commentService.count());
        if (commentService.count() == 0) {
            for (int i = 1; i <= 5; i++) {
                Post post = postService.create(
                        "post for comment " + i,
                        "content for post " + i,
                        "author" + i);
                String content = "this is a comment number " + i + " for post " + post.getId();
                String author = "commenter" + i;
                var comment = commentService.create(post, content, author);
                log.debug("created comment : {}", comment);
            }
        }
    }

    private void work7() {
        log.debug("기존 comment 전체 조회");
        for (Comment comment : commentService.findAll()) {
            log.debug("Existing comment: {}", comment);
        }
    }

    private void work8() {
        log.debug("comment 단건 조회");
        for (Comment comment : commentService.findAll()) {
            log.debug("Existing comment: {}", commentService.findById(comment.getId()));
        }
    }

    private void work9(){
        log.debug("Post 당 Comment 조회");

        for (int i = 1; i <= 5; i++) {
            Post post = postService.create("Post for Comment " + i, "Content for post " + i, "Author" + i);
            String content = "This is a comment number " + i + " for post " + post.getId();
            String author = "Commenter" + i;
            var comment = commentService.create(post, content, author);
            log.debug("Created Comment: {}", comment);
        }

        for (Post post : postService.findAll()) {
            var comments = commentService.findByPostId(post.getId());
            log.debug("Post ID: {} 에 대한 Comments: {}", post.getId(), comments);
        }
        log.debug("Comment 조회 완료");
    }

    private void work10() {
        for (Comment comment : commentService.findAll()) {
            String newContent = comment.getContent() + " this content has been updated.";
            Comment updateComment = commentService.update(comment.getId(), newContent);
            log.debug("updated comment : {}", updateComment);
        }
    }

    private void work11() {
        log.debug("comment 삭제");
        for (Comment comment : commentService.findAll()) {
            commentService.delete(comment.getId());
            log.debug("Deleted comment: {}", comment.getId());
        }
        log.debug("삭제 후 comment 개수: {}", commentService.count());
    }

}
