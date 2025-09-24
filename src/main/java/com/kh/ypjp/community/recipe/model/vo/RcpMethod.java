package com.kh.ypjp.community.recipe.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString 등 자동 생성
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자
public class RcpMethod {
    
    private int rcpMthNo;       // DB의 RCP_MTH_NO 컬럼
    private String rcpMethod;   // DB의 RCP_METHOD 컬럼

}