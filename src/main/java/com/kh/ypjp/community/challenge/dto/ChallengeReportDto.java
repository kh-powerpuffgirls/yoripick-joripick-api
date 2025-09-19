package com.kh.ypjp.community.challenge.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ChallengeReportDto {
    // REPORT 테이블
    private Long reportNo;
    private Long userNo;
    private String reportType; // REPORT_TYPE 테이블 PK
    private Long refNo;        // 신고 대상 (댓글번호, 글번호 등)
    private String content;
    private String resolved;   // Y/N
    private Date reportedAt;

    // REPORT_TYPE 테이블
    private String category;
    private String detail;
}
