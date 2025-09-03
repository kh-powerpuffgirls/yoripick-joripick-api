package com.kh.ypjp.community.free.controller;

import com.kh.ypjp.community.free.service.FreeService;
import com.kh.ypjp.community.free.dto.FreeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 자유게시판 REST 컨트롤러입니다.
 * 클라이언트의 HTTP 요청을 받아 FreeService를 통해 게시글 데이터를 처리하고 응답을 반환합니다.
 */
@RestController
@RequestMapping("/community/free")
@CrossOrigin(origins = "http://localhost:5173")
public class FreeController {

    private final FreeService freeService;

    @Autowired
    public FreeController(FreeService freeService) {
        this.freeService = freeService;
    }

    /**
     * 모든 게시글 목록을 조회합니다.
     * GET /community/free
     * @return 모든 게시글 목록
     */
    @GetMapping
    public ResponseEntity<List<FreeDto>> getAllPosts() {
        List<FreeDto> posts = freeService.findAllPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * 특정 ID의 게시글을 조회합니다.
     * GET /community/free/{id}
     * @param id 게시글 ID
     * @return 조회된 게시글 (존재하지 않으면 404 Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<FreeDto> getPostById(@PathVariable Integer id) {
        Optional<FreeDto> post = freeService.findPostById(id);
        return post.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 새로운 게시글을 생성합니다.
     * POST /community/free
     * @param newPost 생성할 게시글 정보
     * @return 생성된 게시글과 201 Created 상태
     */
    @PostMapping
    public ResponseEntity<FreeDto> createPost(@RequestBody FreeDto newPost) {
        FreeDto createdPost = freeService.createPost(newPost);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    /**
     * 특정 ID의 게시글을 수정합니다.
     * PUT /community/free/{id}
     * @param id 수정할 게시글 ID
     * @param updatedPost 수정된 게시글 정보
     * @return 수정된 게시글 (존재하지 않으면 404 Not Found)
     */
    @PutMapping("/{id}")
    public ResponseEntity<FreeDto> updatePost(@PathVariable Integer id, @RequestBody FreeDto updatedPost) {
        Optional<FreeDto> post = freeService.updatePost(id, updatedPost);
        return post.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 특정 ID의 게시글을 삭제합니다.
     * DELETE /community/free/{id}
     * @param id 삭제할 게시글 ID
     * @return 204 No Content 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        freeService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
