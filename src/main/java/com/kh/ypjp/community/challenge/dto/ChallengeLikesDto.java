package com.kh.ypjp.community.challenge.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ChallengeLikesDto{
    private int likeNo;
    private int challengeNo;
    private int userNo;
    private Date likedAt;
}
