package com.kh.ypjp.community.free.dto;

import lombok.Data;
import java.util.Date;

@Data
public class LikesDto {
    private int likeNo;
    private int boardNo;
    private int userNo;
    private Date likedAt;
}
