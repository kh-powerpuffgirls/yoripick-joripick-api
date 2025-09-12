package com.kh.ypjp.community.mypost.controller;

import com.kh.ypjp.community.mypost.dto.MyPostDto;
import com.kh.ypjp.community.mypost.service.MyPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/mypost")
@CrossOrigin(origins = "http://localhost:5173")
public class MyPostController {

    private final MyPostService myPostService;

    @Autowired
    public MyPostController(MyPostService myPostService) {
        this.myPostService = myPostService;
    }

    // 내 게시물 전체 조회 (통합)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MyPostDto>> getMyPosts(@PathVariable Integer userId) {
        List<MyPostDto> myPosts = myPostService.findPostsByUser(userId);
        return ResponseEntity.ok(myPosts);
    }

    // 게시글 상세 조회
    @GetMapping("/{category}/{id}")
    public ResponseEntity<MyPostDto> getPostDetail(
            @PathVariable String category,
            @PathVariable Integer id) {
        return myPostService.findPostDetail(category, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 임시 유저 2번용 테스트 엔드포인트
    @GetMapping("/test")
    public ResponseEntity<List<MyPostDto>> getTestUserPosts() {
        List<MyPostDto> myPosts = myPostService.findPostsByUser(2); // userId = 2
        return ResponseEntity.ok(myPosts);
    }
}
