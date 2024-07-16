package com.example.kotlinposts

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<KotlinpostsApplication>().with(TestcontainersConfiguration::class).run(*args)
}
