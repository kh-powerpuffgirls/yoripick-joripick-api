package com.kh.ypjp.community.recipe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.community.recipe.dto.ReviewDto;
import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewPage;
import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewResponse;
import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewWriteRequest;
import com.kh.ypjp.community.recipe.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/community/recipe/review")
@CrossOrigin(origins = "http://localhost:5173/", exposedHeaders = "Location")
public class ReviewController {
	private final ReviewService reviewService;

    @GetMapping("/{rcpNo}")
    public ResponseEntity<ReviewPage> getReviews(
            @PathVariable int rcpNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "latest") String sort) {
        
        ReviewPage reviewPage = reviewService.getReviewPage(rcpNo, page, sort);
        return ResponseEntity.ok(reviewPage);
    }
    
    @GetMapping("/{rcpNo}/photos")
    public ResponseEntity<List<ReviewResponse>> getPhotoReviews(@PathVariable int rcpNo) {
        List<ReviewResponse> photoReviews = reviewService.getPhotoReviews(rcpNo);
        return ResponseEntity.ok(photoReviews);
    }
    
    @PostMapping(value = "/{userNo}", consumes = "multipart/form-data")
    public ResponseEntity<Void> createReview(
            @ModelAttribute ReviewDto.ReviewWriteRequest request,
            @PathVariable Long userNo
    ) { 
    	System.out.println(userNo);
    	System.out.println(request.toString());
    	
    	if (userNo == null) {
            // 혹시 모를 비인증 접근에 대한 방어 코드
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    	try {
    		reviewService.createReview(request, userNo);
    		return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
