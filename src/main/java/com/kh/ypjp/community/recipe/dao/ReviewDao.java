package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewResponse;
import com.kh.ypjp.community.recipe.model.vo.Review;

public interface ReviewDao {
	List<ReviewResponse> selectReviewList(HashMap<String, Object> params);
    long selectReviewCount(int rcpNo);
    List<ReviewResponse> selectPhotoReviews(int rcpNo);
    int insertReview(Review review);

}
