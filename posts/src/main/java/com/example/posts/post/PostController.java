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

    @GetMapping("")
    ResponseEntity<List<Post>> findAll() {
        List<Post> posts = repository.findAll();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("")
    ResponseEntity<Post> save(@RequestBody Post post) {
        try {
            if (post == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (post.version() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Post savedPost = repository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
        } catch (OptimisticLockingFailureException ole) {
            System.out.println("Optimistic lock exception: " + ole.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
                            existingPost.id(),
                            existingPost.userId(),
                            post.title(),
                            post.body(),
                            post.version()
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
