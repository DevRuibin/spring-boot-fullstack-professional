package com.example.kotlinposts.posts

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Post(
    @Id var id: Int, var userId: Int,
    var title: String, var body: String, var version: Int) {
}
