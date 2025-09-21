package com.kh.ypjp.community.market.controller;

import com.kh.ypjp.community.market.dto.MarketDto;
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
import org.springframework.web.util.UriComponentsBuilder;

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

    // 모든 마켓 게시글 조회 (삭제되지 않은 게시글만)
    @GetMapping
    public ResponseEntity<List<MarketDto>> getAllPosts(HttpServletRequest request) {
        List<MarketDto> posts = marketService.getAllPosts();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        
        for (MarketDto post : posts) {
            if (post.getServerName() != null && !post.getServerName().isEmpty()) {
                String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/images/")
                        .path(post.getServerName())
                        .toUriString();
                post.setImageUrl(imageUrl);
            }
        }
        return ResponseEntity.ok(posts);
    }

    // 인기 마켓 게시글 조회
    @GetMapping("/popular")
    public ResponseEntity<List<MarketDto>> getPopularPosts(HttpServletRequest request) {
        List<MarketDto> popularPosts = marketService.getPopularPosts();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        for (MarketDto post : popularPosts) {
            if (post.getServerName() != null && !post.getServerName().isEmpty()) {
                String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/images/")
                        .path(post.getServerName())
                        .toUriString();
                post.setImageUrl(imageUrl);
            }
        }
        return ResponseEntity.ok(popularPosts);
    }
    
    // 최신 게시글 조회
    @GetMapping("/recent")
    public ResponseEntity<List<MarketDto>> getRecentPosts(HttpServletRequest request) {
        List<MarketDto> recentPosts = marketService.getRecentPosts();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        for (MarketDto post : recentPosts) {
            if (post.getServerName() != null && !post.getServerName().isEmpty()) {
                String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/images/")
                        .path(post.getServerName())
                        .toUriString();
                post.setImageUrl(imageUrl);
                log.info("생성된 이미지 URL: {}", imageUrl);
            }
        }
        return ResponseEntity.ok(recentPosts);
    }

    // 게시글 상세 조회 및 조회수 증가
    @GetMapping("/{id}")
    public ResponseEntity<MarketDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userNo,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 쿠키를 이용한 조회수 증가 로직
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
        
        

        // 서비스 로직 호출
        Optional<MarketDto> optionalPost = marketService.getPost(id);
        
        if (optionalPost.isPresent()) {
            MarketDto post = optionalPost.get();

            // 조회수 증가 및 쿠키 추가
            if (increase) {
                marketService.incrementViews(id);
                post.setViews(post.getViews() + 1);

                Cookie newCookie = new Cookie(cookieName, readPostCookie);
                newCookie.setPath("/");
                newCookie.setMaxAge(60 * 60 * 24); // 24시간
                response.addCookie(newCookie);
            }

            if (post.getServerName() != null && !post.getServerName().isEmpty()) {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/images/")
                        .path(post.getServerName())
                        .toUriString();
                post.setImageUrl(imageUrl);
            }
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

 // 내 판매 목록 조회
 @GetMapping("/my-posts")
 public ResponseEntity<List<MarketDto>> getMyPosts(@AuthenticationPrincipal Long userNo) {
     if (userNo == null) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
     }
     
     List<MarketDto> myPosts = marketService.getMyPosts(userNo.intValue()); // userNo를 int로 변환
     
     if (myPosts.isEmpty()) {
         return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
     }
     
     return ResponseEntity.ok(myPosts);
 }
 // ...
    
    // 게시글 등록
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerPost(
            @RequestPart("marketDto") MarketDto marketDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userNo,
            HttpServletRequest request) {

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
    
    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long id,
            @RequestPart("marketDto") MarketDto marketDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal Long userNo,
            Authentication authentication) {

        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            Optional<MarketDto> updatedPost = marketService.updatePost(id, marketDto, image, userNo, isAdmin);
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

    // 게시글 삭제 (소프트 삭제)
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
    
    // 구매 신청
    @PostMapping("/buy")
    public ResponseEntity<String> registerPurchaseForm(
            @RequestBody MarketDto marketDto) {
        try {
            boolean available = marketService.checkQuantity(marketDto.getProductId(), marketDto.getCount());
            if (!available) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 재고가 부족합니다.");
            }
            marketService.registerPurchaseForm(marketDto);
            return ResponseEntity.ok("구매 신청이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("구매 신청 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구매 신청 중 오류가 발생했습니다.");
        }
    }
}