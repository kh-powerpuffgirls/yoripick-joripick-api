package com.kh.ypjp.community.market.dao;

import com.kh.ypjp.community.market.dto.MarketDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MarketDao {

    // 기존 메서드들
    List<MarketDto> getPopularPosts();
    List<MarketDto> getRecentPosts();
    List<MarketDto> getMyPosts(Long userNo);
    MarketDto getPostDetail(Long productId);
    void incrementViews(Long productId);
    int registerPost(MarketDto marketDto);
    void registerPurchaseForm(MarketDto marketDto);
    void decreaseQuantity(@Param("productId") Long productId, @Param("count") int count);
    int getProductQuantity(Long productId);

    // 게시글 수정 (추가)
    void updatePost(MarketDto marketDto);

    // 게시글 소프트 삭제 (추가)
    int updateDeleteStatus(@Param("productId") Long productId, @Param("deleteStatus") String deleteStatus);

    // 이미지 번호를 기준으로 서버에 저장된 파일명을 찾는 메서드 (추가)
    String findImageServerName(Integer imageNo);

    // 게시글 ID를 기준으로 작성자의 userNo를 찾는 메서드 (추가)
    Long findUserNoById(Long productId);
    
    String selectSikBtiByUserNo(int userNo);

 // 내 판매 목록 조회
    List<MarketDto> getMyPosts(int userNo);
}