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
                .andExpect(jsonPath("$.content").isArray());
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

    @Test
    @DisplayName("GET /api/v1/posts - Pagination 파라미터 테스트")
    void t11() throws Exception {
        // 여러 포스트 생성
        for (int i = 0; i < 15; i++) {
            mockMvc.perform(
                    post("/api/v1/posts")
                            .contentType("application/json")
                            .content(
                                    objectMapper.writeValueAsBytes(
                                            new PostController.CreatePostRequest(
                                                    "Pagination Test Title " + i,
                                                    "Pagination Test Content " + i,
                                                    "Pagination Test Author"
                                            )
                                    )
                            )
            ).andExpect(status().isCreated());
        }

        // 첫 번째 페이지 조회 (size=5)
        mockMvc.perform(
                        get("/api/v1/posts")
                                .param("page", "0")
                                .param("size", "5")
                                .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));

        // 두 번째 페이지 조회 (size=5)
        mockMvc.perform(
                        get("/api/v1/posts")
                                .param("page", "1")
                                .param("size", "5")
                                .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/posts - 기본 Pagination (page=0, size=10)")
    void t12() throws Exception {
        mockMvc.perform(
                        get("/api/v1/posts")
                                .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/posts/search - 제목 검색")
    void t13() throws Exception {
        // 검색용 포스트 생성
        mockMvc.perform(
                post("/api/v1/posts")
                        .contentType("application/json")
                        .content(
                                objectMapper.writeValueAsBytes(
                                        new PostController.CreatePostRequest(
                                                "UniqueSearchTitle",
                                                "Some Content",
                                                "Author"
                                        )
                                )
                        )
        ).andExpect(status().isCreated());

        // 제목으로 검색
        mockMvc.perform(
                        get("/api/v1/posts/search")
                                .param("keyword", "UniqueSearchTitle")
                                .param("searchType", "title")
                                .contentType("application/json")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("UniqueSearchTitle"));
    }

    @Test
    @DisplayName("GET /api/v1/posts/search - 내용 검색")
    void t14() throws Exception {
        // 검색용 포스트 생성
        mockMvc.perform(
                post("/api/v1/posts")
                        .contentType("application/json")
                        .content(
                                objectMapper.writeValueAsBytes(
                                        new PostController.CreatePostRequest(
                                                "Title",
                                                "UniqueSearchContent",
                                                "Author"
                                        )
                                )
                        )
        ).andExpect(status().isCreated());

        // 내용으로 검색
        mockMvc.perform(
                get("/api/v1/posts/search")
                        .param("keyword", "UniqueSearchContent")
                        .param("searchType", "content")
                        .contentType("application/json")
        ).andExpect(status().isOk())          .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].content").value("UniqueSearchContent"));
    }

    @Test
    @DisplayName("GET /api/v1/posts/search - 제목+내용 검색 (기본값)")
    void t15() throws Exception {
        // 검색용 포스트 생성
        mockMvc.perform(
                post("/api/v1/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsBytes(
                                        new PostController.CreatePostRequest(
                                                "TitleAndContentSearchTest",
                                                "Content for search",
                                                "Author"
                                        )
                                )
                        )
        ).andExpect(status().isCreated());

        // 제목+내용으로 검색 (기본값)
        mockMvc.perform(
                get("/api/v1/posts/search")
                        .param("keyword", "TitleAndContentSearchTest")
                        .contentType("application/json")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("TitleAndContentSearchTest"));
    }

}
