package com.kh.ypjp.community.mypost.controller;

import com.kh.ypjp.community.mypost.dto.MyPostDto;
import com.kh.ypjp.community.mypost.service.MyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/community/mypost")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MyPostController {

    private final MyPostService myPostService;

    @GetMapping
    public ResponseEntity<List<MyPostDto>> getMyPosts(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userNo = Integer.parseInt(principal.getName());
        List<MyPostDto> myPosts = myPostService.findPostsByUser(userNo);
        return ResponseEntity.ok(myPosts);
    }

    @GetMapping("/{category}/{id}")
    public ResponseEntity<MyPostDto> getPostDetail(
            @PathVariable String category,
            @PathVariable Integer id) {
        return myPostService.findPostDetail(category, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
