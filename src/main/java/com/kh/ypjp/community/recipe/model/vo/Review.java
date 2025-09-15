package com.kh.ypjp.community.recipe.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
	private int reviewNo;
    private int userNo;
    private int refNo; // 레시피 번호 (RCP_NO)
    private String rcpSource;
    private double stars;
    private String content;
    private Integer imageNo; // 이미지가 없을 수 있으므로 Integer 사용
    private Date reviewDate;
    private char deleteStatus;
}
