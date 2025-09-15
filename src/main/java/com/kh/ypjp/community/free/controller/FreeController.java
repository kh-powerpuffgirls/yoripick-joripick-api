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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

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

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<FreeDto>> selectAllBoards() {
        List<FreeDto> boardList = freeService.selectAllBoards();
        String baseUrl = "http://localhost:8081";

        for (FreeDto board : boardList) {
            if (board.getServerName() != null && !board.getServerName().isEmpty()) {
                String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                                                    .path("/images/")
                                                    .path(board.getServerName())
                                                    .toUriString();
                board.setImageUrl(imageUrl);
            }
        }
        return new ResponseEntity<>(boardList, HttpStatus.OK);
    }

    // 게시글 조회 및 조회수 증가
    @GetMapping("/{boardNo}")
    public ResponseEntity<FreeDto> selectBoardByNo(@PathVariable int boardNo,
                                                   HttpServletRequest req,
                                                   HttpServletResponse res) {

        FreeDto board = freeService.selectBoardByNo(boardNo);
        if (board == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (board.getServerName() != null && !board.getServerName().isEmpty()) {
            String baseUrl = "http://localhost:8081";
            String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                                                .path("/images/")
                                                .path(board.getServerName())
                                                .toUriString();
            board.setImageUrl(imageUrl);
        }

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
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userNo) {

        FreeDto freeDto = new FreeDto();
        freeDto.setTitle(title);
        freeDto.setSubheading(subheading);
        freeDto.setContent(content);
        freeDto.setUserNo(userNo.intValue()); // 변환

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
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userNo) {

        FreeDto freeDto = new FreeDto();
        freeDto.setBoardNo(boardNo);
        freeDto.setTitle(title);
        freeDto.setSubheading(subheading);
        freeDto.setContent(content);
        freeDto.setUserNo(userNo.intValue()); // 변환

        int result = freeService.updateBoard(freeDto, file);
        if (result > 0)
            return new ResponseEntity<>("게시글이 성공적으로 수정되었습니다.", HttpStatus.OK);
        else
            return new ResponseEntity<>("게시글 수정에 실패했습니다. 작성자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN);
    }
    
    // 게시글 삭제
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<String> softDeleteBoard(@PathVariable int boardNo, 
                                                  @AuthenticationPrincipal Long userNo) {
        boolean isDeleted = freeService.softDeleteBoard(boardNo, userNo.intValue()); // 변환
        if (isDeleted) return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("게시글 삭제에 실패했거나 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
    
    // 좋아요 토글
    @PostMapping("/{boardNo}/likes")
    public ResponseEntity<String> toggleLike(@PathVariable int boardNo, 
                                             @AuthenticationPrincipal Long userNo) {
        boolean liked = freeService.toggleLike(boardNo, userNo.intValue()); // 변환
        return new ResponseEntity<>(liked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.", HttpStatus.OK);
    }

    // 좋아요 상태 조회
    @GetMapping("/{boardNo}/likes/status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(@PathVariable int boardNo,
                                                              @AuthenticationPrincipal Long userNo) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLiked", freeService.isLiked(boardNo, userNo.intValue())); // 변환
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
    public ResponseEntity<String> addReply(@RequestBody ReplyDto replyDto,
                                           @AuthenticationPrincipal Long userNo) {
        if (replyDto.getCategory() == null || replyDto.getCategory().isEmpty()) {
            replyDto.setCategory("BOARD");
        }
        replyDto.setUserNo(userNo.intValue()); // 변환

        freeService.insertReply(replyDto);
        return new ResponseEntity<>("댓글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
    }

    // 댓글 수정
    @PutMapping("/replies/{replyNo}")
    public ResponseEntity<String> updateReply(@PathVariable int replyNo,
                                              @RequestBody ReplyDto replyDto,
                                              @AuthenticationPrincipal Long userNo) {
        replyDto.setReplyNo(replyNo);
        replyDto.setUserNo(userNo.intValue()); // 변환

        int result = freeService.updateReply(replyDto);
        if (result > 0) return new ResponseEntity<>("댓글이 성공적으로 수정되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("댓글 수정에 실패했습니다. 작성자만 수정할 수 있습니다.", HttpStatus.NOT_FOUND);
    }

    // 댓글 삭제
    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyNo,
                                              @AuthenticationPrincipal Long userNo) {
        boolean isDeleted = freeService.deleteReply(replyNo, userNo.intValue()); // 변환
        if (isDeleted) return new ResponseEntity<>("댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("댓글 삭제에 실패했습니다. 권한을 확인해주세요.", HttpStatus.FORBIDDEN);
    }
}
