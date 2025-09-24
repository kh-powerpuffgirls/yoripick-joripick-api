package com.kh.ypjp.community.market.controller;

import com.kh.ypjp.community.market.dto.MarketBuyDto;
import com.kh.ypjp.community.market.dto.MarketSellDto;
import com.kh.ypjp.community.market.service.MarketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.Arrays;
import jakarta.servlet.http.Cookie;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/community/market")
@RequiredArgsConstructor
@Slf4j
public class MarketController {

    private final MarketService marketService;

    @GetMapping
    public ResponseEntity<List<MarketSellDto>> getAllPosts() {
        List<MarketSellDto> posts = marketService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<MarketSellDto>> getPopularPosts() {
        List<MarketSellDto> popularPosts = marketService.getPopularPosts();
        return ResponseEntity.ok(popularPosts);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<MarketSellDto>> getRecentPosts() {
        List<MarketSellDto> recentPosts = marketService.getRecentPosts();
        return ResponseEntity.ok(recentPosts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketSellDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userNo,
            HttpServletRequest request,
            HttpServletResponse response) {

        String cookieName = "readMarketPostId";
        String readPostCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    readPostCookie = cookie.getValue();
                    break;
                }
            }
        }
        
        boolean increase = false;
        if (readPostCookie == null) {
            increase = true;
            readPostCookie = String.valueOf(id);
        } else if (!Arrays.asList(readPostCookie.split("/")).contains(String.valueOf(id))) {
            increase = true;
            readPostCookie += "/" + id;
        }

        Optional<MarketSellDto> optionalPost = marketService.getPost(id);
        
        if (optionalPost.isPresent()) {
            MarketSellDto post = optionalPost.get();

            if (increase) {
                marketService.incrementViews(id);
                post.setViews(post.getViews() + 1);

                Cookie newCookie = new Cookie(cookieName, readPostCookie);
                newCookie.setPath("/");
                newCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(newCookie);
            }
            
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<MarketSellDto>> getMyPosts(@AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MarketSellDto> myPosts = marketService.getMyPostsWithForms(userNo);

        if (myPosts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(myPosts);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerPost(
            @RequestPart("marketDto") MarketSellDto marketDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userNo) {

        if (userNo == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인 후 이용해주세요.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            marketDto.setUserNo(userNo);
            
            Long imageNo = marketService.registerPost(marketDto, image); 
            
            if (imageNo != null) {
                marketDto.setImageNo(imageNo.intValue());
            }

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "게시글이 성공적으로 등록되었습니다.");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        } catch (Exception e) {
            log.error("게시글 등록 실패: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long id,
            @RequestPart("marketDto") MarketSellDto marketDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userNo,
            Authentication authentication) {

        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            Optional<MarketSellDto> updatedPost = marketService.updatePost(id, marketDto, image, userNo, isAdmin);
            if (updatedPost.isPresent()) {
                return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("게시글 수정에 실패했거나 권한이 없습니다. 작성자 또는 관리자만 수정할 수 있습니다.");
            }
        } catch (Exception e) {
            log.error("게시글 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 수정 중 오류가 발생했습니다.");
        }
    }

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
    
    @PostMapping("/buy")
    public ResponseEntity<String> registerPurchaseForm(
            @RequestBody MarketBuyDto marketBuyDto,
            @AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }
        try {
            boolean available = marketService.checkQuantity(marketBuyDto.getProductId(), marketBuyDto.getCount());
            if (!available) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 재고가 부족합니다.");
            }

            // 🔥 로그인한 사용자 번호를 DTO에 세팅
            marketBuyDto.setUserNo(userNo);

            marketService.registerPurchaseForm(marketBuyDto);
            return ResponseEntity.ok("구매 신청이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("구매 신청 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구매 신청 중 오류가 발생했습니다.");
        }
    }
    
    // 판매자용 구매 신청 폼 상세 조회 API
    @GetMapping("/sell-buy-form/{formId}")
    public ResponseEntity<MarketBuyDto> getSellBuyForm(
            @PathVariable Long formId,
            @AuthenticationPrincipal Long userNo) { 
        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<MarketBuyDto> optionalForm = marketService.getSellBuyFormById(formId, userNo);

        if (optionalForm.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(optionalForm.get());
    }

    @DeleteMapping("/delete-buy-form/{formId}")
    public ResponseEntity<String> deleteBuyForm(
            @PathVariable Long formId,
            @AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }

        try {
            boolean isDeleted = marketService.deleteBuyForm(formId, userNo);
            if (isDeleted) {
                return ResponseEntity.ok("구매 폼이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("구매 폼 삭제에 실패했거나 권한이 없습니다.");
            }
        } catch (Exception e) {
            log.error("구매 폼 삭제 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구매 폼 삭제 중 오류가 발생했습니다.");
        }
    }
}