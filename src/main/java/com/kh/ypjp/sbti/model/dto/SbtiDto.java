package com.kh.ypjp.sbti.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SbtiDto {
	
	@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SikBti {
        private int questionNo;
        private int optionNo;
        private String types;
        private int score;
    }

}
