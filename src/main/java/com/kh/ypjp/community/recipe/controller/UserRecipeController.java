package com.kh.ypjp.community.recipe.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeDetailResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewPageResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewResponseDto;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.service.UserRecipeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserRecipeController {

    private final UserRecipeService recipeService;

    @GetMapping("/community/recipe")
    @CrossOrigin(origins = "http://localhost:5173/", exposedHeaders = "Location")
    public ResponseEntity<RecipePage> selectList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String rcpMthNo,
            @RequestParam(required = false) String rcpStaNo) 
   	{        
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sort", sort);
        params.put("ingredients", ingredients);
        params.put("rcpMthNo", rcpMthNo);
        params.put("rcpStaNo", rcpStaNo);
        
        RecipePage recipePage = recipeService.selectRecipePage(params);
        return ResponseEntity.ok(recipePage);
    }
    @GetMapping("/community/recipe/ranking")
    public ResponseEntity<List<UserRecipeDto.UserRecipeResponse>> selectRankingList() {
    	List<UserRecipeDto.UserRecipeResponse> list = recipeService.selectRankingRecipes();
        return ResponseEntity.ok(list);
    }

    
    @GetMapping("/options/methods")
    public ResponseEntity<List<RcpMethod>> getRcpMethods() {
        return ResponseEntity.ok(recipeService.selectRcpMethods());
    }

    @GetMapping("/options/situations")
    public ResponseEntity<List<RcpSituation>> getRcpSituations() {
        return ResponseEntity.ok(recipeService.selectRcpSituations());
    }

    @GetMapping("/ingredients/search")
    public ResponseEntity<List<UserRecipeDto.IngredientInfo>> searchIngredients(@RequestParam String keyword) {
        return ResponseEntity.ok(recipeService.searchIngredients(keyword));
    }

 // --- 레시피 등록 메소드 ---
    @PostMapping(value = "/community/recipe/{userNo}", consumes = "multipart/form-data")
    public ResponseEntity<Void> createRecipe(
            @ModelAttribute UserRecipeDto.RecipeWriteRequest request,
            @PathVariable Long userNo 
    ) {
    	System.out.println(userNo);
    	System.out.println(request.toString());
        // Security 설정으로 인해 인증된 사용자만 이 메소드에 접근 가능하므로,
        // customUser는 null이 아니라고 가정할 수 있습니다.
        if (userNo == null) {
            // 혹시 모를 비인증 접근에 대한 방어 코드
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // ✨ CustomOAuth2User 객체에서 사용자 번호를 가져옵니다.
//            long userNo = customUser.getUserNo(); // 또는 getNo(), getId() 등 실제 메소드명 사용
            
            // 서비스에 사용자 번호와 요청 데이터를 전달합니다.
            recipeService.createRecipe(request, userNo);
            
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    // 리뷰 목록 조회 API 
    @GetMapping("/community/recipe/{rcpNo}/reviews")
    public ResponseEntity<UserRecipeDto.ReviewPageResponse> selectReviewList(
            @PathVariable int rcpNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "latest") String sort) {
        
    	ReviewPageResponse reviewPage = recipeService.selectReviewPage(rcpNo, page, sort);
        return ResponseEntity.ok(reviewPage);
    }
    
    //  포토 리뷰 목록 조회 API 
    @GetMapping("/community/recipe/{rcpNo}/reviews/photos")
    public ResponseEntity<List<ReviewResponseDto>> selectPhotoReviewList(@PathVariable int rcpNo) {
    	List<ReviewResponseDto> photoReviews = recipeService.selectPhotoReviewList(rcpNo);
        return ResponseEntity.ok(photoReviews);
    }
    
    @GetMapping("/community/recipe/{rcpNo}")
    public ResponseEntity<RecipeDetailResponse> selectRecipeDetailForGuest(
            @PathVariable int rcpNo,
            HttpServletRequest req,
            HttpServletResponse res) {
        
        // 기존 상세 조회 메소드를 호출하되, userNo를 null로 전달합니다.
        return selectRecipeDetail(rcpNo, null, req, res);
    }
    
    // 레시피 상세
    @GetMapping("/community/recipe/{rcpNo}/{userNo}")
    public ResponseEntity<RecipeDetailResponse> selectRecipeDetail(
    		@PathVariable int rcpNo,
    		@PathVariable Long userNo,
    		HttpServletRequest req,
    		HttpServletResponse res
    		) {
    	// 조회수 쿠키로직
    	String cookieName = "readRecipeNo"; // 쿠키 이름을 레시피용으로 변경
        String readRecipeNoCookie = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    readRecipeNoCookie = cookie.getValue();
                    break;
                }
            }
        }
        
        boolean increase = false;
        if (readRecipeNoCookie == null) {
            increase = true;
            readRecipeNoCookie = String.valueOf(rcpNo);
        } else if (!Arrays.asList(readRecipeNoCookie.split("/")).contains(String.valueOf(rcpNo))) {
            increase = true;
            readRecipeNoCookie += "/" + rcpNo;
        }
        
    	RecipeDetailResponse recipeDetail = recipeService.selectRecipeDetail(rcpNo, userNo, increase);

        if (recipeDetail == null) {
            return ResponseEntity.notFound().build();
        }

        // 조회수를 증가시켜야 하는 경우에만 새 쿠키를 응답에 추가합니다.
        if (increase) {
            Cookie newCookie = new Cookie(cookieName, readRecipeNoCookie);
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24); // 24시간
            res.addCookie(newCookie);
        }
        
        return ResponseEntity.ok(recipeDetail);
    }
    
    // --- 좋아요 상태 변경 ---
    @PostMapping("/community/recipe/{rcpNo}/like/{userNo}")
    public ResponseEntity<Void> updateLikeStatus(
            @PathVariable int rcpNo,
            @PathVariable Long userNo, // ✨ @PathVariable로 받음
            @RequestBody LikeRequest request) {
        
        recipeService.updateLikeStatus(rcpNo, userNo, request.getStatus());
        return ResponseEntity.ok().build();
    }
    
    @Data
    public static class LikeRequest {
        private String status;
    }
    
    
    //리뷰작성
    @PostMapping(value = "/community/recipe/{rcpNo}/reviews/{userNo}", consumes = "multipart/form-data")
    public ResponseEntity<Void> createReview(
            @PathVariable int rcpNo,
            @PathVariable long userNo,
            @ModelAttribute UserRecipeDto.ReviewWriteRequest request) {
        
        try {
            recipeService.createReview(rcpNo, userNo, request);
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created 응답
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    //리뷰 삭제 
    @DeleteMapping("/community/recipe/{rcpNo}/reviews/{reviewNo}/{userNo}")
    public ResponseEntity<Void> deleteReview(
    		@PathVariable int rcpNo,
            @PathVariable int reviewNo,
            @PathVariable long userNo) {
        
        try {
            recipeService.deleteReview(reviewNo, userNo);
            return ResponseEntity.ok().build(); // 성공 시 200 OK 응답
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 레시피 수정
    @PutMapping(value = "/community/recipe/{rcpNo}/{userNo}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateRecipe(
            @PathVariable int rcpNo,
            @PathVariable long userNo,
            @ModelAttribute UserRecipeDto.RecipeWriteRequest request) {
        
        try {
            recipeService.updateRecipe(rcpNo, userNo, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    //레시피 삭제 
    @DeleteMapping("/community/recipe/{rcpNo}")
    public ResponseEntity<Void> deleteRecipe(
    		@PathVariable int rcpNo) {
        
        try {
            recipeService.deleteRecipe(rcpNo);
            return ResponseEntity.ok().build(); // 성공 시 200 OK 응답
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
 
}