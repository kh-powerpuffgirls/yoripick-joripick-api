package com.kh.ypjp.community.free.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 게시글 데이터 전송 객체(DTO)입니다.
 * 계층 간 데이터 교환을 위해 사용되며, 게시글의 모든 정보를 담고 있습니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreeDto {
    /**
     * 게시글의 고유 식별자(ID).
     */
    private Integer id;
    /**
     * 게시글의 제목.
     */
    private String title;
    /**
     * 게시글의 본문 내용.
     */
    private String description;
    /**
     * 게시글이 생성된 날짜 및 시간.
     */
    private LocalDateTime createdDate;
    /**
     * 게시글의 조회수.
     */
    private int views;
}
