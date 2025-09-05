package com.kh.ypjp.community.challenge.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ChallengeReplyDto {
    private int replyNo;
    private int parentReplyNo;
    private int refNo;
    private int userNo;
    private String username; 
    private String sik_bti; 
    private String content;
    private Date createdAt;
    private String profileImageServerName; 
}