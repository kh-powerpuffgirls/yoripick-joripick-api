package com.kh.ypjp.community.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/community")
@CrossOrigin(origins = "http://localhost:5173")
public class CommunityController {

    static class BoardInfo {
        private String title;
        private String description;

        public BoardInfo(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    @GetMapping
    public List<BoardInfo> getBoards() {
        return Arrays.asList(
            new BoardInfo("자유 게시판", "자유롭게 식구들과 대화를 나눠보세요!"),
            new BoardInfo("레시피 공유", "나만의 레시피를 식구들과 공유해보세요!"),
            new BoardInfo("푸드 챌린지", "지금 유행중인 레시피를 따라해보세요!"),
            new BoardInfo("쿠킹 클래스", "함께 요리할 식구들을 찾아보세요!"),
            new BoardInfo("직거래 장터", "간편한 직거래, 지금 바로 경험해보세요!")
        );
    }
}
