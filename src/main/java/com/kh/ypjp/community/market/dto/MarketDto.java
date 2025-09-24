package com.kh.ypjp.community.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketDto {

    // MARKETPLACE (판매자) 테이블 관련 필드
    private Long productId;
    private Long userNo;
    private String title;
    private String name; // 상품명
    private String detail; // 상세설명
    private int price;
    private int quantity; // 재고
    private Date createdAt;
    private Date deadline;
    private String phone; // 판매자 연락처
    private String accountNo;
    private int views;
    private String deleteStatus;

    // USERS (판매자 정보) 테이블 관련 필드
    private String author; // 닉네임
    private String authorProfileUrl; // 프로필 이미지 URL
    private String sikBti;

    // 이미지 관련 필드
    private String originName;
    private String serverName;
    private Integer imageNo;
    private String imageUrl;

    // LIKES 테이블 관련 필드
    private int likes;

    // MARKET_FORM (구매자) 테이블 관련 필드
    private Long formNo;
    private int count; // 구매 수량
    private String buyerName; // 구매자 예금주명
    private String dlvrName; // 수령인 이름
    private String address;
    private String detailAddress;
    private String buyerPhone; // 구매자 전화번호

    // 추가 필드 (상시 판매 체크박스)
    private boolean alwaysOnSale;


}