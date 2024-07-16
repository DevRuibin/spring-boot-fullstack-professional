package com.example.posts.post;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Version;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post{
    @Id
    private Integer id;
    private Integer userId;
    @NotNull
    private String title;
    @NotNull
    private String body;
    @Version
    private Integer version;
}
