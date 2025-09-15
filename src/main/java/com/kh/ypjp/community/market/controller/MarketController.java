package com.kh.ypjp.community.market.controller;

import com.kh.ypjp.community.market.dto.MarketDto;
import com.kh.ypjp.community.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @PostMapping("/posts")
    public ResponseEntity<MarketDto> createPost(@RequestBody MarketDto postDTO) {
        MarketDto createdPost = marketService.createMarketPost(postDTO);
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/posts/popular")
    public ResponseEntity<List<MarketDto>> getPopularPosts() {
        List<MarketDto> popularPosts = marketService.getPopularPosts();
        return ResponseEntity.ok(popularPosts);
    }

    @GetMapping("/posts/recent")
    public ResponseEntity<List<MarketDto>> getRecentPosts() {
        List<MarketDto> recentPosts = marketService.getRecentPosts();
        return ResponseEntity.ok(recentPosts);
    }
    
    @GetMapping("/posts/{id}")
    public ResponseEntity<MarketDto> getPost(@PathVariable Long id) {
        MarketDto post = marketService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}