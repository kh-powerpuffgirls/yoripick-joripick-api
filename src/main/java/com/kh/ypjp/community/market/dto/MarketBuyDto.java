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
public class MarketBuyDto {

    private Long formNo;
    private Long productId;
    private int count;
    private String buyerName; 
    private String dlvrName;
    private String address;
    private String detailAddress;
    private String buyerPhone;
    private Long userNo;
    private Date createdAt;
    
    private String buyerNickname;
}
