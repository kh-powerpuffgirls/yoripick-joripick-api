package com.kh.ypjp.community.recipe.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RcpSituation {

    private int rcpStaNo;           // DB의 RCP_STA_NO 컬럼
    private String rcpSituation;    // DB의 RCP_SITUATION 컬럼

}