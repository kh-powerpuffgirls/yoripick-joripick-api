package com.kh.ypjp.community.recipe.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RcpIngredient {
    private int rcpNo;
    private int ingNo;
    private String quantity;
    private int weight;
    private String ingName;

}
