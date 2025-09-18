package com.kh.ypjp.community.challenge.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ChallengeInfoDto {
    private Long chInfoNo;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int imageNo;
}
