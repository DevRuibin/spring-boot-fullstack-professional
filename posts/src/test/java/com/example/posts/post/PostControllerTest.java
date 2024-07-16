package com.example.posts.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class) // This focuses on testing the PostController
@AutoConfigureMockMvc // Automatically configures MockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc; // Injects MockMvc to perform HTTP requests

    @MockBean
    PostRepository repository; // Mocks the PostRepository to isolate controller behavior

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        posts = List.of(
                new Post(1, 1, "Hello, World!", "This is my first post.", null),
                new Post(2, 1, "Second Post", "This is my second post.", null)
        );
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello, World!",
                        "body":"This is my first post.",
                        "version": null
                    },
                    {
                        "id":2,
                        "userId":1,
                        "title":"Second Post",
                        "body":"This is my second post.",
                        "version": null
                    }
                ]
                """;

        when(repository.findAll()).thenReturn(posts);

        ResultActions resultActions = mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        Post post = new Post(1, 1, "Test Title", "Test Body", null);
        when(repository.findById(1)).thenReturn(Optional.of(post));
        String json = """
                {
                    "id":1,
                    "userId":1,
                    "title":"Test Title",
                    "body":"Test Body",
                    "version": null
                }
                """;

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldCreateNewPostWhenGivenValidID() throws Exception {
        Post post = new Post(3, 1, "This is my brand new post", "TEST BODY", 1);
        when(repository.save(post)).thenReturn(post);
        String json = """
                {
                    "id":3,
                    "userId":1,
                    "title":"This is my brand new post",
                    "body":"TEST BODY",
                    "version": 1
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(json));
    }

    @Test
    void shouldUpdatePostWhenGivenValidPost() throws Exception {
        Post updated = new Post(1, 1, "This is my brand new post", "UPDATED BODY", 1);
        when(repository.findById(1)).thenReturn(Optional.of(posts.get(0)));
        when(repository.save(updated)).thenReturn(updated);
        String requestBody = """
                {
                    "id":1,
                    "userId":1,
                    "title":"This is my brand new post",
                    "body":"UPDATED BODY",
                    "version": 1
                }
                """;

        mockMvc.perform(put("/api/posts/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(requestBody));
    }

    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostID() throws Exception {
        Post updated = new Post(50, 1, "This is my brand new post", "UPDATED BODY", 1);
        when(repository.findById(999)).thenReturn(Optional.empty());
        String json = """
                {
                    "id":50,
                    "userId":1,
                    "title":"This is my brand new post",
                    "body":"UPDATED BODY",
                    "version": 1
                }
                """;

        mockMvc.perform(put("/api/posts/999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePostWhenGivenValidID() throws Exception {
        doNothing().when(repository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(repository, times(1)).deleteById(1);
    }
}
