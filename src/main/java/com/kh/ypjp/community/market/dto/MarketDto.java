package com.kh.ypjp.community.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketDto {
    private Long id;
    private String title;
    private String author;
    private String authorProfileUrl;
    private LocalDate postDate;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean alwaysOnSale;
    private String description;
    private String itemName;
    private int price;
    private int stock;
    private String contactNumber;
    private String bankAccount;
    private int views;
    private int likes;
}