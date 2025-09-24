package com.kh.ypjp.community.recipe.model.vo;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Recipe {
    private int rcpNo;
    private long userNo;
    private String rcpName;
    private String rcpInfo;
    private int rcpMthNo;
    private int rcpStaNo;
    private String tag;
    private int imageNo;
    private int categoryNo;
    private int nutrientNo;
    private String ingredient;
    private String approval;
    private int views;
    private Date createdAt;
    private char deleteStaus;
}