package com.back.domain.post.post.controller;

import com.back.BaseTest;
import com.back.domain.post.post.document.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostControllerTests extends BaseTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("POST /api/v1/posts - 실패 title 누락")
    void t1() throws Exception {
        mockMvc.perform(
                post("/api/v1/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(
                                new PostController.CreatePostRequest(null, "test content", "test author")
                        ))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/posts - 성공")
    void t2() throws Exception {
        mockMvc.perform(
                post("/api/v1/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(
                                new PostController.CreatePostRequest("test title", "test content", "test author")
                        ))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("test title"))
                .andExpect(jsonPath("$.content").value("test content"))
                .andExpect(jsonPath("$.author").value("test author"))
                .andExpect(jsonPath("id").isNotEmpty());
    }

    @Test
    @DisplayName("Get /api/v1/posts - 성공")
    void t3() throws Exception {
        mockMvc.perform(
                get("/api/v1/posts")
                        .contentType("application/json")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Get /api/v1/posts/{id} - 실패")
    void t4() throws Exception {
        mockMvc.perform(
                        get("/api/v1/posts/{id}", "nonexistent-id")
                                .contentType("application/json")
                ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get /api/v1/posts/{id} - 성공")
    void t5() throws Exception {
        // 먼저 포스트를 생성
        String response = mockMvc.perform(
                post("/api/v1/posts")
                        .contentType("application/json")
                        .content(
                                objectMapper.writeValueAsBytes(
                                        new PostController.CreatePostRequest(
                                                "Test Title for GetById",
                                                "Test Content for GetById",
                                                "Test Author for GetById"
                                        )
                                )
                        )
        ).andExpect(status().isCreated())
                .andReturn().getResponse()
                .getContentAsString();

        Post createdPost = objectMapper.readValue(response, Post.class);

        mockMvc.perform(get("/api/v1/posts/{id}", createdPost.getId())
                        .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(createdPost.getId()))
                .andExpect(jsonPath("title").value("Test Title for GetById"))
                .andExpect(jsonPath("content").value("Test Content for GetById"))
                .andExpect(jsonPath("author").value("Test Author for GetById"));
    }

    @Test
    @DisplayName("put /api/v1/posts/{id} - 실패")
    void t6() throws Exception {
        mockMvc.perform(put("/api/v1/posts/{id}", "id")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(
                                new PostController.UpdatePostRequest("z", "zz")
                        ))
                ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("put /api/v1/posts/{id} - 성공")
    void t7() throws Exception {
        // 먼저 포스트를 생성
        String response = mockMvc.perform(
                        post("/api/v1/posts")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsBytes(
                                                new PostController.CreatePostRequest(
                                                        "Test Title for GetById",
                                                        "Test Content for GetById",
                                                        "Test Author for GetById"
                                                )
                                        )
                                )
                ).andExpect(status().isCreated())
                .andReturn().getResponse()
                .getContentAsString();

        Post createdPost = objectMapper.readValue(response, Post.class);

        mockMvc.perform(put("/api/v1/posts/{id}", createdPost.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(
                        new PostController.UpdatePostRequest("update title zz", "update content zz")
                ))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(createdPost.getId()))
                .andExpect(jsonPath("title").value("update title zz"))
                .andExpect(jsonPath("content").value("update content zz"))
                .andExpect(jsonPath("author").value("Test Author for GetById"));

    }

    @Test
    @DisplayName("delete /api/v1/posts/{id} - 실패")
    void t8() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/{id}", "id")
                .contentType("application/json")
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete /api/v1/posts/{id} - 성공")
    void t9() throws Exception {
        // 먼저 포스트를 생성
        String response = mockMvc.perform(
                        post("/api/v1/posts")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsBytes(
                                                new PostController.CreatePostRequest(
                                                        "Test Title for GetById",
                                                        "Test Content for GetById",
                                                        "Test Author for GetById"
                                                )
                                        )
                                )
                ).andExpect(status().isCreated())
                .andReturn().getResponse()
                .getContentAsString();

        Post createdPost = objectMapper.readValue(response, Post.class);

        mockMvc.perform(delete("/api/v1/posts/{id}", createdPost.getId())
                        .contentType("application/json")
                ).andExpect(status().isNoContent());

    }
}
