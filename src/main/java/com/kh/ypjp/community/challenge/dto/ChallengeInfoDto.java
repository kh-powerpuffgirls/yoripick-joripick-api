package com.kh.ypjp.community.challenge.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ChallengeInfoDto {
    private Long chInfoNo;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int imageNo;
}
