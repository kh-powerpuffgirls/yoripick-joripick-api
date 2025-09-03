package com.kh.ypjp.community.mypost.controller;

import com.kh.ypjp.community.mypost.service.MyPostService;
import com.kh.ypjp.community.mypost.dto.MyPostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/community/mypost")
@CrossOrigin(origins = "http://localhost:5173")
public class MyPostController {

    private final MyPostService myPostService;

    @Autowired
    public MyPostController(MyPostService myPostService) {
        this.myPostService = myPostService;
    }

    @GetMapping
    public ResponseEntity<List<MyPostDto>> getAllPosts() {
        List<MyPostDto> posts = myPostService.findAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyPostDto> getPostById(@PathVariable Integer id) {
        Optional<MyPostDto> post = myPostService.findPostById(id);
        return post.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MyPostDto> createPost(@RequestBody MyPostDto newPost) {
        MyPostDto createdPost = myPostService.createPost(newPost);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MyPostDto> updatePost(@PathVariable Integer id, @RequestBody MyPostDto updatedPost) {
        Optional<MyPostDto> post = myPostService.updatePost(id, updatedPost);
        return post.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        myPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
