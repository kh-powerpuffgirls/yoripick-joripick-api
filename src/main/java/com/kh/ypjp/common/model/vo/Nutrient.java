package com.kh.ypjp.common.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Nutrient {
    private int nutrientNo;
    private double energy;  // 칼로리
    private double carb;    // 탄수화물
    private double protein; // 단백질
    private double fat;     // 지방
    private double sodium;  // 나트륨
}