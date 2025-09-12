package com.kh.ypjp.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotiDto {
    private String newMessage;
    private String newReply;
    private String newReview;
    private String expiration;
}
