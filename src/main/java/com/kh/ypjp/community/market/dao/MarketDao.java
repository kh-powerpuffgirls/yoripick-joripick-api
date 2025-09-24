package com.kh.ypjp.community.market.dao;

import com.kh.ypjp.community.market.dto.MarketBuyDto;
import com.kh.ypjp.community.market.dto.MarketSellDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MarketDao {

    // 게시글 조회 관련 메서드
    List<MarketSellDto> getAllPosts();
    List<MarketSellDto> getPopularPosts();
    List<MarketSellDto> getRecentPosts();
    MarketSellDto getPostDetail(Long productId);
    void incrementViews(Long productId);

    int registerPost(MarketSellDto marketSellDto);
    void updatePost(MarketSellDto marketSellDto);
    int updateDeleteStatus(@Param("productId") Long productId, @Param("deleteStatus") String deleteStatus);

    Long findUserNoById(Long productId);
    String findImageServerName(Integer imageNo);
    String selectSikBtiByUserNo(int userNo);

    List<MarketSellDto> findMyPostsWithForms(Long userNo);

    void registerPurchaseForm(MarketBuyDto marketBuyDto);

    void decreaseQuantity(@Param("productId") Long productId, @Param("count") int count);

    int getProductQuantity(Long productId);
    
    // 판매자용 폼 상세 조회
    MarketBuyDto findPurchaseForm(Long formId);
    
    // 폼 ID를 통해 판매자 ID를 찾기
    Long findSellerByFormId(Long formId);

    // 🔥 구매 폼 삭제 상태 업데이트
    int updateBuyFormDeleteStatus(@Param("formId") Long formId, @Param("status") String status);
}
