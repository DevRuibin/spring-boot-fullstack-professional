package com.example.posts.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;



@JsonTest
public class PostJsonTest {
    @Autowired
    private JacksonTester<Post> jacksonTester;

    @Test
    void shouldSerializePost() throws Exception {
        Post post = new Post(1,1,"Hello, World!", "This is my first post.",null);
        String expected = String.format("""
                {
                    "id": %d,
                    "userId": %d,
                    "title": "%s",
                    "body": "%s",
                    "version": null
                }
                """, post.id(), post.userId(), post.title(), post.body());
        assertThat(jacksonTester.write(post)).isEqualTo(expected);
    }

    @Test
    void shouldDeserializePost() throws Exception {
        Post post = new Post(1,1,"Hello, World!", "This is my first post.",null);
        String content = String.format("""
                {
                    "id": %d,
                    "userId": %d,
                    "title": "%s",
                    "body": "%s",
                    "version": null
                }
                """, post.id(), post.userId(), post.title(), post.body());
        assertThat(jacksonTester.parse(content)).isEqualTo(post);
        assertThat(jacksonTester.parseObject(content).id()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).userId()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).title()).isEqualTo("Hello, World!");
        assertThat(jacksonTester.parseObject(content).body()).isEqualTo("This is my first post.");
    }
}
