package com.kh.ypjp.community.ckclass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CkclassDto {
    private Integer roomNo; 
    private Integer userNo; 
    private String className; 
    private String classInfo; 
    private String passcode; 
    private String deleteStatus; 

    private String originName;
    private String serverName;
    private Integer imageNo;
    private String imageUrl;

    private String username;
    
    private Integer memberCount;
    private Integer unreadCount;
    private String isNotificationOn;
}
