package com.kh.ypjp.community.recipe.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipeStep {
    private int stepNo;
    private int rcpNo;
    private int stepOrder;
    private String description;
    private int imageNo;
}