package com.kh.ypjp.community.free.dto;

import lombok.Data;
import java.util.Date;

@Data
public class FreeDto {
    private int boardNo;
    private int userNo;
    private String username;
    private String title;
    private String subheading;
    private String content;
    private Date createdDate;
    private int views;
    private int likes;
    private int replyCount;
    
    private String originName; 
    private String serverName;

    private String sik_bti;
}