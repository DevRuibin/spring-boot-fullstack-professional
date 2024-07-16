package com.example.posts.post;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class PostControllerIntTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    TestRestTemplate restTemplate;


    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldFindAllPosts(){
        Post[] posts = restTemplate.getForObject("/api/posts", Post[].class);
        assertThat(posts.length).isEqualTo(100);
    }

    @Test
    void shouldFindPostWhenValidPostID(){
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/1", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldThrowNotFoundWhenInvalidPostID(){
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/9999", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @Rollback
    void shouldCreatePostWhenPostIsValid(){
        Post post = new Post(101, 1, "101 Title", "101 Body", 1);
        ResponseEntity<Post> response = restTemplate.postForEntity("/api/posts", post, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).userId()).isEqualTo(1);
        assertThat(Objects.requireNonNull(response.getBody()).title()).isEqualTo("101 Title");
        assertThat(Objects.requireNonNull(response.getBody()).body()).isEqualTo("101 Body");
    }

    @Test
    void shouldNotCreateNewPostWhenValidationFails(){
        Post post = new Post(101, 1, "", "", null);
        ResponseEntity<Post> response = restTemplate.postForEntity("/api/posts", post, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Rollback
    void shouldUpdatePostWhenPostIsValid(){
        ResponseEntity<Post> response = restTemplate.getForEntity("/api/posts/1", Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Post post = response.getBody();
        assertThat(post).isNotNull();
        assert post != null;
        Post updatedPost = new Post(post.id(), post.userId(), "NEW POST TITLE", "NEW POST BODY", post.version());
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Post> updatedEntity = new HttpEntity<>(updatedPost, headers);

        ResponseEntity<Post> response2 = restTemplate.exchange("/api/posts/1", HttpMethod.PUT, updatedEntity, Post.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response2.getBody()).title()).isEqualTo("NEW POST TITLE");
        assertThat(Objects.requireNonNull(response2.getBody()).body()).isEqualTo("NEW POST BODY");

    }

    @Test
    @Rollback
    void shouldDeleteWithValidId(){
        ResponseEntity<Void> response = restTemplate.exchange("/api/posts/88", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
