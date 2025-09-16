package com.kh.ypjp.community.free.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ReplyDto {
    private int replyNo;
    private int refNo;                 // 참조 대상 번호
    private String category;           // 참조 대상 카테고리 ("FREE", "REPLY")
    private int userNo;
    private String username;
    private String sik_bti;            // (식별 아이디 - 프론트에 필요)
    private String content;
    private Date createdAt;
    private String profileImageServerName;
}