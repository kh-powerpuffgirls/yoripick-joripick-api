package com.kh.ypjp.community.recipe.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.recipe.dao.ReviewDao;
import com.kh.ypjp.community.recipe.dto.ReviewDto;
import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewPage;
import com.kh.ypjp.community.recipe.dto.ReviewDto.ReviewResponse;
import com.kh.ypjp.community.recipe.model.vo.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
	
	private final ReviewDao dao;
    private final UtilService utilService;
    
	@Override
	public ReviewPage getReviewPage(int rcpNo, int page, String sort) {
		int pageSize = 5; // 한 페이지에 5개씩
		
        HashMap<String, Object> params = new HashMap<>();
        params.put("rcpNo", rcpNo);
        params.put("page", page);
        params.put("sort", sort);
        params.put("pageSize", pageSize);
        
        long totalElements = dao.selectReviewCount(rcpNo);
        
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        List<ReviewResponse> reviews = dao.selectReviewList(params);
        
        return new ReviewPage(reviews, totalPages);
	}
	@Override
	public List<ReviewResponse> getPhotoReviews(int rcpNo) {
		 return dao.selectPhotoReviews(rcpNo);
	}
	
	@Override
    @Transactional(rollbackFor = Exception.class)
	public void createReview(ReviewDto.ReviewWriteRequest request, Long userNo) {
		Integer imageNo = null; // 이미지 번호는 없을 수도 있으므로 Integer
        
		// 1. DB에서 다음에 사용할 rcpNo를 미리 가져옵니다.
		// long rcpNo = dao.getNextRcpNo();
        
        // 1. 이미지가 있으면 저장
		if (request.getImage() != null && !request.getImage().isEmpty()) {
            // 파일 저장 경로: .../resources/community/recipe/{레시피번호}/reviews/
            String webPath = "community/recipe/" + request.getRcpNo() + "/reviews";
            String changeName = utilService.getChangeName(request.getImage(), webPath);
            String serverName = webPath + "/" + changeName;
            
            Map<String, Object> imageParam = new HashMap<>();
            imageParam.put("originName", request.getImage().getOriginalFilename());
            imageParam.put("serverName", serverName);
            
            utilService.insertImage(imageParam);
            imageNo = ((Number) imageParam.get("imageNo")).intValue();
        }
        
        // ✨ 2. Review VO 객체를 생성하여 DB에 저장할 데이터 담기
        Review review = Review.builder()
	                            .userNo(userNo.intValue()) // Long을 int로 변환
	                            .refNo(request.getRcpNo())   // DTO에서 받은 rcpNo를 refNo에 설정
	                            .stars(request.getStars())
	                            .content(request.getContent())
	                            .imageNo(imageNo)
	                            .build();
        
        dao.insertReview(review);
    }
		

}
