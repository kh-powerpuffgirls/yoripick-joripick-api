package com.kh.ypjp.community.challenge.controller;

import com.kh.ypjp.community.challenge.dto.ChallengeDto;
import com.kh.ypjp.community.challenge.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/community/challenge")
@CrossOrigin(origins = "http://localhost:3000") // CORS 설정
public class ChallengeController {

    private final ChallengeService challengeService;

    @Autowired
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    // 게시글 전체 목록 조회 (메인 페이지)
    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllPosts() {
        List<ChallengeDto> posts = challengeService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시글 상세 조회 (상세보기)
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDto> getPost(@PathVariable Long id) {
        challengeService.incrementViews(id); // 조회수 증가
        return challengeService.getPost(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새 게시글 등록
    @PostMapping
    public ResponseEntity<ChallengeDto> createPost(@RequestBody ChallengeDto challengeDto) {
        ChallengeDto createdPost = challengeService.createPost(challengeDto);
        return ResponseEntity.ok(createdPost);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDto> updatePost(@PathVariable Long id, @RequestBody ChallengeDto challengeDto) {
        Optional<ChallengeDto> updatedPost = challengeService.updatePost(id, challengeDto);
        return updatedPost
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (challengeService.deletePost(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // 좋아요 수 증가
    @PostMapping("/like/{id}")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        challengeService.incrementLikes(id);
        return ResponseEntity.ok().build();
    }
}