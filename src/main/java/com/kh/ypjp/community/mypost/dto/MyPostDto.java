package com.kh.ypjp.community.mypost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MyPostDto {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdDate;
    private int views;
}
