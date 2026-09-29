package com.back.global.initData;

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

    @Bean
    public ApplicationRunner baseInitDataRunner() {
        return args -> {
            wokr1();
            work2();
            work3();
            work4();
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
}
