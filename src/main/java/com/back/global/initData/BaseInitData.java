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
        };
    }

    private void wokr1() {
        log.debug("post entity 개수 : {}", postService.count());
    }
}
