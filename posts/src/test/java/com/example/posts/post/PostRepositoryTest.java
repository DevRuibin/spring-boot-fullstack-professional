package com.example.posts.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");


    @Autowired
    PostRepository postRepository;

    @Autowired
    JdbcConnectionDetails jdbcConnectionDetailsl;

    @BeforeEach
    void setUp() {
        List<Post> posts = List.of(new Post(1,1,"Hello, World!", "This is my first post!",null));
        postRepository.saveAll(posts);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void connectionEstablished(){
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldReturnPostByTitle() {
        Post post = postRepository.findByTitle("Hello, World!").orElseThrow();
        assertEquals("Hello, World!", post.title(), "Post title should be 'Hello, World!'");
    }

    @Test
    void shouldNotReturnPostWhenTitleIsNotFound() {
        Optional<Post> post = postRepository.findByTitle("Hello, Wrong Title!");
        assertFalse(post.isPresent(), "Post should not be present");
    }


    @Test
    void findByTitle() {
    }
}