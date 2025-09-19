package com.kh.ypjp.community.ckclass.dto;

import lombok.Data;

@Data
public class CkclassDto {
    private Integer roomNo; 
    private Integer userNo; 
    private String className; 
    private String classInfo; 
    private String passcode; 
    private String deleteStatus; 

    // 이미지 관련 필드
    private String originName;
    private String serverName;
    private Integer imageNo;
    private String imageUrl;

    // 방장 이름
    private String username;
    
    // 추가 정보
    private Integer memberCount;
    private Integer unreadCount;


}
