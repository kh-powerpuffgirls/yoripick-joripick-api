package com.kh.ypjp.community.free.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ReplyDto {
    private int replyNo;
    private int refNo;
    private String category;
    private int userNo;
    private String username;
    private String sik_bti;
    private String content;
    private Date createdAt;
    private String profileImageServerName;
    private int depth;
}