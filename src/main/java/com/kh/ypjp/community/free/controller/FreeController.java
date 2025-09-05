package com.kh.ypjp.community.free.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

    // 게시글 전체 조회 (GET /community/free)
    @GetMapping
    public ResponseEntity<List<FreeDto>> selectAllBoards() {
        List<FreeDto> list = freeService.selectAllBoards();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 게시글 상세 조회 (GET /community/free/{boardNo})
    @GetMapping("/{boardNo}")
    public ResponseEntity<FreeDto> selectBoardByNo(@PathVariable int boardNo) {
        // 임시로 사용자 번호를 2로 설정. 실제 환경에서는 로그인된 사용자의 userNo를 사용해야 함.
        int tempUserNo = 2; 

        // 조회수 증가 로직은 Service에서 처리
        freeService.incrementViews(boardNo, tempUserNo);
        
        FreeDto board = freeService.selectBoardByNo(boardNo);
        if (board != null) {
            return new ResponseEntity<>(board, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 게시글 등록 (POST /community/free)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insertBoard(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("subheading") String subheading,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            FreeDto freeDto = new FreeDto();
            freeDto.setTitle(title);
            freeDto.setContent(content);
            freeDto.setUserNo(2);
            freeDto.setSubheading(subheading);
            freeService.insertBoard(freeDto, file);
            return new ResponseEntity<>("게시글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("게시글 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 수정 (PUT /community/free/{boardNo})
    @PutMapping(value = "/{boardNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateBoard(
            @PathVariable int boardNo,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "subheading", required = false) String subheading,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            FreeDto freeDto = new FreeDto();
            freeDto.setBoardNo(boardNo);
            freeDto.setTitle(title);
            freeDto.setContent(content);
            freeDto.setSubheading(subheading);
            int result = freeService.updateBoard(freeDto, file);
            if (result > 0) {
                return new ResponseEntity<>("게시글이 성공적으로 수정되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("게시글 수정에 실패했습니다.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("게시글 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 삭제 (DELETE /community/free/{boardNo})
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<String> deleteBoard(@PathVariable int boardNo) {
        int result = freeService.deleteBoard(boardNo);
        if (result > 0) {
            return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("게시글 삭제에 실패했습니다.", HttpStatus.NOT_FOUND);
        }
    }

    // 좋아요 토글 (추가 또는 취소)
    @PostMapping("/{boardNo}/likes")
    public ResponseEntity<String> toggleLike(@PathVariable int boardNo, @RequestParam int userNo) {
        boolean liked = freeService.toggleLike(boardNo, userNo);
        if (liked) {
            return new ResponseEntity<>("좋아요가 추가되었습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("좋아요가 취소되었습니다.", HttpStatus.OK);
        }
    }

    // 좋아요 상태 조회 (특정 게시글에 대해 특정 사용자가 좋아요 눌렀는지)
    @GetMapping("/{boardNo}/likes/status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(@PathVariable int boardNo, @RequestParam int userNo) {
        boolean isLiked = freeService.isLiked(boardNo, userNo);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLiked", isLiked);
        return ResponseEntity.ok(response);
    }

    // 게시글 좋아요 수 조회
    @GetMapping("/{boardNo}/likes/count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable int boardNo) {
        int likesCount = freeService.getLikesCount(boardNo);
        return new ResponseEntity<>(likesCount, HttpStatus.OK);
    }
    
    // 댓글 조회 메서드
    @GetMapping("/{boardNo}/replies")
    public ResponseEntity<List<ReplyDto>> getRepliesByBoardNo(@PathVariable int boardNo) {
        try {
            List<ReplyDto> replies = freeService.selectAllRepliesByBoardNo(boardNo);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 등록 (대댓글 포함)
    @PostMapping("/replies")
    public ResponseEntity<String> addReply(@RequestBody ReplyDto replyDto) {
        try {
            if (replyDto.getCategory() == null || replyDto.getCategory().isEmpty()) {
                replyDto.setCategory("BOARD");
            }
            freeService.insertReply(replyDto);
            return new ResponseEntity<>("댓글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 댓글 수정 (PUT /community/free/replies/{replyNo}) 엔드포인트 추가
    @PutMapping("/replies/{replyNo}")
    public ResponseEntity<String> updateReply(@PathVariable int replyNo, @RequestBody ReplyDto replyDto) {
        try {
            replyDto.setReplyNo(replyNo); // URL의 replyNo를 DTO에 설정
            int result = freeService.updateReply(replyDto);
            if (result > 0) {
                return new ResponseEntity<>("댓글이 성공적으로 수정되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("댓글 수정에 실패했습니다.", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            // 순환 참조 예외 처리
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
