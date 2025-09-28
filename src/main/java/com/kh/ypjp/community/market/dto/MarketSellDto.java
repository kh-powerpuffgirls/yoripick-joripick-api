package com.kh.ypjp.community.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketSellDto {

    private Long productId;
    private Long userNo;        
    private String title;
    private Integer imageNo;
    private String name;
    private String detail;
    private Long price;
    private int quantity;
    private Date createdAt;
    private Date deadline;
    private String phone;
    private String accountNo;
    private int views;
    private String deleteStatus;
    private boolean alwaysOnSale;
    
    private String author;           
    private String authorProfileUrl;    
    private String sikBti;             
    private String imageUrl;           
    private String serverName;          
    private String originName;         
    
    private String isPurchased; 
    
    private Long formNo;
    private List<MarketBuyDto> buyForms;
}