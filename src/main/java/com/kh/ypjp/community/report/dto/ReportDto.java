package com.kh.ypjp.community.report.dto;

import lombok.Data;
import java.sql.Date;

@Data
public class ReportDto {

	private int reportNo;       
    private int userNo;  
    private int reportType; 
    private int refNo;     
    private String content;    
    private String resolved;   
    private Date reportedAt;   

    private String category;    
    private String detail;   
    
    private int reportedUserNo;
    private String reportedUserNickname; 
    private String reportedUserProfileImageUrl;
}
