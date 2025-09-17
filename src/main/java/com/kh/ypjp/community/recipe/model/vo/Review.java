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
	    private long userNo;
	    private Date reviewDate;   
	    private double stars;
	    private String content;
	    private int imageNo;
	    private String rcpSource;
	    private int refNo;
	    private String deleteStatus; 
}
