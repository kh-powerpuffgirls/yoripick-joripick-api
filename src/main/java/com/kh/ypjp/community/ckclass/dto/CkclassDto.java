package com.kh.ypjp.community.ckclass.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CkclassDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String joinCode;
}