package com.kh.ypjp.community.recipe.model.vo;

import lombok.Data;

@Data
public class CookingStep {

    private int rcpNo;
    private int rcpOrder;
    private String description;
    private Integer imageNo; // 이미지가 없을 수 있으므로 Integer 사용
    
    // JOIN을 통해 가져올 요리 순서 이미지 경로
    private String serverName;

}