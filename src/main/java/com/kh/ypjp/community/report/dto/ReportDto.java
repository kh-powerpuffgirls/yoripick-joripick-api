package com.kh.ypjp.community.report.dto;

import lombok.Data;
import java.sql.Date;

@Data
public class ReportDto {

    // REPORT 테이블
	private int reportNo;       // 신고 번호 (DB에서 자동 생성)
    private int userNo;         // 신고한 유저 번호
    private int reportType;     // 신고 유형 (REPORT_TYPE 테이블 PK)
    private int refNo;          // 신고 대상 번호
    private String content;     // 신고 내용
    private String resolved;    // 처리 여부 ('Y', 'N')
    private Date reportedAt;    // 신고 일시

    // REPORT_TYPE 테이블
    private String category;    // 신고 대상 카테고리 (예: COOKING_CLASS, BOARD)
    private String detail;      // 신고 사유 설명
}
