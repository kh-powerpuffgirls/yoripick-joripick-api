package com.kh.ypjp.community.recipe.service;

import java.util.List;

import com.kh.ypjp.community.recipe.dto.ReviewDto;
import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewPage;
import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewResponse;

public interface ReviewService {
	ReviewPage getReviewPage(int rcpNo, int page, String sort);
    List<ReviewResponse> getPhotoReviews(int rcpNo);
    void createReview(ReviewDto.ReviewWriteRequest request, Long userNo);
}
