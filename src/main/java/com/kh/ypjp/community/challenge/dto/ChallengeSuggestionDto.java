package com.kh.ypjp.community.challenge.dto;

import lombok.Data;

@Data
public class ChallengeSuggestionDto {
    private int formNo;
    private Long userNo;
    private String chTitle;
    private String description;
    private String reference;
}
