package com.kh.ypjp.community.market.controller;

import com.kh.ypjp.community.market.dto.MarketDto;
import com.kh.ypjp.community.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/community/market")
@RequiredArgsConstructor
@Slf4j
public class MarketController {

    private final MarketService marketService;

    // 모든 마켓 게시글 조회
    @GetMapping
    public ResponseEntity<List<MarketDto>> getAllPosts() {
        return ResponseEntity.ok(marketService.getAllPosts());
    }
    
    // 인기 게시글 조회 엔드포인트
    @GetMapping("/popular")
    public ResponseEntity<List<MarketDto>> getPopularPosts() {
        return ResponseEntity.ok(marketService.getPopularPosts());
    }
    
    // 최신 게시글 조회 엔드포인트
    @GetMapping("/recent")
    public ResponseEntity<List<MarketDto>> getRecentPosts() {
        return ResponseEntity.ok(marketService.getRecentPosts());
    }
    // 게시글 상세 조회 및 조회수 증가
    @GetMapping("/{id}")
    public ResponseEntity<MarketDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userNo) {

        Optional<MarketDto> post = marketService.getPostAndIncrementViews(id, userNo);
        return post.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("marketDto") String marketDtoJson,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userNo) {

        MarketDto marketDto = null;
        try {
            marketDto = new com.fasterxml.jackson.databind.ObjectMapper().readValue(marketDtoJson, MarketDto.class);
            marketDto.setUserNo(userNo);

            marketService.registerPost(marketDto, image);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "판매 글이 성공적으로 등록되었습니다.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("판매 글 등록 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "판매 글 등록에 실패했습니다."));
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(
            @PathVariable("id") Long id,
            @RequestParam("marketDto") String marketDtoJson,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userNo,
            Authentication authentication) {

        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            MarketDto marketDto = new com.fasterxml.jackson.databind.ObjectMapper().readValue(marketDtoJson, MarketDto.class);
            Optional<MarketDto> updatedPost = marketService.updatePost(id, marketDto, image, userNo, isAdmin);
            if (updatedPost.isPresent()) {
                return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("게시글 수정에 실패했습니다. 작성자 또는 관리자만 수정할 수 있습니다.");
            }
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 수정 중 오류가 발생했습니다.");
        }
    }
    
    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userNo,
            Authentication authentication) {
        
        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean deleted = marketService.deletePost(id, userNo, isAdmin);
        if (deleted) {
            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("게시글 삭제에 실패했거나 권한이 없습니다. 작성자 또는 관리자만 삭제할 수 있습니다.");
    }

    // 구매 폼 등록
    @PostMapping("/purchase/form")
    public ResponseEntity<String> registerPurchaseForm(@RequestBody MarketDto marketDto) {
        try {
            marketService.registerPurchaseForm(marketDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("구매 폼이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("구매 폼 등록 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구매 폼 등록에 실패했습니다.");
        }
    }

    // 재고 확인
    @GetMapping("/check-quantity/{productId}")
    public ResponseEntity<Boolean> checkQuantity(@PathVariable Long productId, @RequestParam int count) {
        boolean isAvailable = marketService.checkQuantity(productId, count);
        return ResponseEntity.ok(isAvailable);
    }
}