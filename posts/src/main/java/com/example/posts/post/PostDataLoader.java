package com.example.posts.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;


@Component
public class PostDataLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(PostDataLoader.class);
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;

    public PostDataLoader(ObjectMapper objectMapper, PostRepository postRepository) {
        this.objectMapper = objectMapper;
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(postRepository.count() == 0) {
            String Posts_JSON = "/data/posts.json";
            log.info("Loading posts into database form Json: {}", Posts_JSON);
            try(InputStream inputStream = TypeReference.class.getResourceAsStream(Posts_JSON)){
                Posts responses = objectMapper.readValue(inputStream, Posts.class);
                postRepository.saveAll(responses.posts());
            }catch (IOException e) {
                throw new RuntimeException("Failed to load posts from database", e);
            }
            log.info("Loaded {} posts", postRepository.count());
        }
    }
}
