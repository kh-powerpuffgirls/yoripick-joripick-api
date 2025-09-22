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
	    
	    private String username;          // USERS 테이블에서 JOIN
	    private String userProfileImage;  // IMAGE 테이블에서 JOIN (작성자 프로필)
	    private String serverName;
	    
	    private String sikBti;
}
