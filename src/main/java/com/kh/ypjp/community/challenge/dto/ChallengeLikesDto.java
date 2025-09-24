package com.kh.ypjp.community.challenge.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ChallengeLikesDto{
    private int likeNo;
    private Long challengeNo;
    private Date likedAt;
    private Long userNo;
    private String likeStatus;

}
