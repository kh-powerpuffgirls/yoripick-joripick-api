package com.kh.ypjp.community.recipe.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewResponse;
import com.kh.ypjp.community.recipe.model.vo.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao{
	private final SqlSession session;
    private static final String NAMESPACE = "reviewMapper.";

    @Override
    public List<ReviewResponse> selectReviewList(HashMap<String, Object> params) {
        return session.selectList(NAMESPACE + "selectReviewList", params);
    }

    @Override
    public long selectReviewCount(int rcpNo) {
        return session.selectOne(NAMESPACE + "selectReviewCount", rcpNo);
    }

    @Override
    public List<ReviewResponse> selectPhotoReviews(int rcpNo) {
        return session.selectList(NAMESPACE + "selectPhotoReviews", rcpNo);
    }

    @Override
    public int insertReview(Review review) {
        return session.insert(NAMESPACE + "insertReview", review);
    }
}
