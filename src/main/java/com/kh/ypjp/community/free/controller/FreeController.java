package com.kh.ypjp.community.free.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kh.ypjp.community.free.dto.FreeDto;
import com.kh.ypjp.community.free.dto.ReplyDto;
import com.kh.ypjp.community.free.service.FreeService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/community/free")
public class FreeController {

    private final FreeService freeService;

    public FreeController(FreeService freeService) {
        this.freeService = freeService;
    }

    // 로그인 여부 확인
    private boolean isLoggedIn(int userNo) {
        return userNo > 0;
    }

    // 로그인 필요 응답
    private ResponseEntity<String> unauthorizedResponse() {
        return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
    }

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<FreeDto>> selectAllBoards() {
        return new ResponseEntity<>(freeService.selectAllBoards(), HttpStatus.OK);
    }

    // 특정 게시글 조회 및 조회수 증가 (은비 코드)
    @GetMapping("/{boardNo}")
    public ResponseEntity<FreeDto> selectBoardByNo(@PathVariable int boardNo,
                                                   HttpServletRequest req,
                                                   HttpServletResponse res) {

        FreeDto board = freeService.selectBoardByNo(boardNo);
        if (board == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        String cookieName = "readBoardNo";
        String readBoardNoCookie = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    readBoardNoCookie = cookie.getValue();
                    break;
                }
            }
        }

        boolean increase = false;
        if (readBoardNoCookie == null) {
            increase = true;
            readBoardNoCookie = String.valueOf(boardNo);
        } else if (!Arrays.asList(readBoardNoCookie.split("/")).contains(String.valueOf(boardNo))) {
            increase = true;
            readBoardNoCookie += "/" + boardNo;
        }

        if (increase) {
            freeService.incrementViews(boardNo);
            board.setViews(board.getViews() + 1);

            Cookie newCookie = new Cookie(cookieName, readBoardNoCookie);
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            res.addCookie(newCookie);
        }

        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<String> insertBoard(
            @RequestParam("title") String title,
            @RequestParam(value = "subheading", required = false) String subheading,
            @RequestParam("content") String content,
            @RequestParam("userNo") int userNo,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        FreeDto freeDto = new FreeDto();
        freeDto.setTitle(title);
        freeDto.setSubheading(subheading);
        freeDto.setContent(content);
        freeDto.setUserNo(userNo);

        freeService.insertBoard(freeDto, file);
        return new ResponseEntity<>("게시글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
    }

    // 게시글 수정
    @PutMapping("/{boardNo}")
    public ResponseEntity<String> updateBoard(
            @PathVariable int boardNo,
            @RequestParam("title") String title,
            @RequestParam(value = "subheading", required = false) String subheading,
            @RequestParam("content") String content,
            @RequestParam("userNo") int userNo,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        FreeDto freeDto = new FreeDto();
        freeDto.setBoardNo(boardNo);
        freeDto.setTitle(title);
        freeDto.setSubheading(subheading);
        freeDto.setContent(content);
        freeDto.setUserNo(userNo);

        int result = freeService.updateBoard(freeDto, file);
        if (result > 0)
            return new ResponseEntity<>("게시글이 성공적으로 수정되었습니다.", HttpStatus.OK);
        else
            return new ResponseEntity<>("게시글 수정에 실패했습니다. 작성자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN);
    }

    // 게시글 삭제
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<String> softDeleteBoard(@PathVariable int boardNo, @RequestParam("userNo") int userNo) {
        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        boolean isDeleted = freeService.softDeleteBoard(boardNo, userNo);
        if (isDeleted) return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("게시글 삭제에 실패했거나 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    // 좋아요 토글
    @PostMapping("/{boardNo}/likes")
    public ResponseEntity<String> toggleLike(@PathVariable int boardNo, @RequestBody Map<String, Integer> payload) {
        int userNo = payload.get("userNo");
        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        boolean liked = freeService.toggleLike(boardNo, userNo);
        return new ResponseEntity<>(liked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.", HttpStatus.OK);
    }

    // 좋아요 상태 조회
    @GetMapping("/{boardNo}/likes/status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(@PathVariable int boardNo, @RequestParam("userNo") int userNo) {
        if (!isLoggedIn(userNo)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Map<String, Boolean> response = new HashMap<>();
        response.put("isLiked", freeService.isLiked(boardNo, userNo));
        return ResponseEntity.ok(response);
    }

    // 좋아요 개수 조회
    @GetMapping("/{boardNo}/likes/count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable int boardNo) {
        return new ResponseEntity<>(freeService.getLikesCount(boardNo), HttpStatus.OK);
    }

    // 댓글 조회
    @GetMapping("/{boardNo}/replies")
    public ResponseEntity<List<ReplyDto>> getRepliesByBoardNo(@PathVariable int boardNo) {
        return ResponseEntity.ok(freeService.selectAllRepliesByBoardNo(boardNo));
    }

    // 댓글 등록
    @PostMapping("/replies")
    public ResponseEntity<String> addReply(@RequestBody ReplyDto replyDto) {
        if (!isLoggedIn(replyDto.getUserNo())) return unauthorizedResponse();

        if (replyDto.getCategory() == null || replyDto.getCategory().isEmpty()) {
            replyDto.setCategory("BOARD");
        }
        freeService.insertReply(replyDto);
        return new ResponseEntity<>("댓글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
    }

    // 댓글 수정
    @PutMapping("/replies/{replyNo}")
    public ResponseEntity<String> updateReply(@PathVariable int replyNo, @RequestBody ReplyDto replyDto) {
        if (!isLoggedIn(replyDto.getUserNo())) return unauthorizedResponse();

        replyDto.setReplyNo(replyNo);
        int result = freeService.updateReply(replyDto);
        if (result > 0) return new ResponseEntity<>("댓글이 성공적으로 수정되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("댓글 수정에 실패했습니다. 작성자만 수정할 수 있습니다.", HttpStatus.NOT_FOUND);
    }

    // 댓글 삭제
    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyNo, @RequestParam("userNo") int userNo) {
        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        boolean isDeleted = freeService.deleteReply(replyNo, userNo);
        if (isDeleted) return new ResponseEntity<>("댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("댓글 삭제에 실패했습니다. 권한을 확인해주세요.", HttpStatus.FORBIDDEN);
    }
}