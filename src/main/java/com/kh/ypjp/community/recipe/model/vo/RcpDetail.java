package com.kh.ypjp.community.recipe.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RcpDetail {
    private int rcpNo;
    private int rcpOrder;
    private String description;
    private int imageNo; // 이미지가 없는 경우 null이 될 수 있도록 Integer 사용

}
