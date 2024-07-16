package com.example.posts.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/posts")
public class PostController {
    private static final Logger log = LoggerFactory.getLogger(PostDataLoader.class);

    private final PostRepository repository;

    @Autowired
    public PostController(PostRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    ResponseEntity<List<Post>> findAll() {
        List<Post> posts = repository.findAll();
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    ResponseEntity<Post> save(@RequestBody Post post) {
        System.out.println("new connection");
        if(post == null){
            return ResponseEntity.badRequest().body(null);
        }
        if(post.getVersion() == null){
            return ResponseEntity.badRequest().body(null);
        }
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    ResponseEntity<Post> findById(@PathVariable Integer id) {
        Optional<Post> post = repository.findById(id);
        return post.map(ResponseEntity::ok)
                .orElseThrow(PostNotFoundException::new);
    }



    @PutMapping("/{id}")
    ResponseEntity<Post> update(@PathVariable Integer id, @RequestBody Post post) {
        return repository.findById(id).map(
                existingPost -> {
                    Post updatedPost =new Post(
                            existingPost.getId(),
                            existingPost.getUserId(),
                            post.getTitle(),
                            post.getBody(),
                            post.getVersion()
                    );
                    repository.save(updatedPost);
                    return ResponseEntity.ok(updatedPost);
                }
        ).orElseThrow(PostNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
