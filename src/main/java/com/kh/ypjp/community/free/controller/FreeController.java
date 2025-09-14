package com.kh.ypjp.community.free.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    // ===== 로그인 체크 =====
    private boolean isLoggedIn(int userNo) {
        return userNo > 0;
    }

    private ResponseEntity<String> unauthorizedResponse() {
        return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
    }

    // ====== 게시글 조회 ======
    @GetMapping
    public ResponseEntity<List<FreeDto>> selectAllBoards() {
        List<FreeDto> list = freeService.selectAllBoards();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{boardNo}")
    public ResponseEntity<FreeDto> selectBoardByNo(
            @PathVariable int boardNo,
            HttpServletRequest req,
            HttpServletResponse res) {

        FreeDto board = freeService.selectBoardByNo(boardNo);
        if (board == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // 쿠키 조회수
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> insertBoard(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("userNo") int userNo,
            @RequestParam(value = "subheading", required = false) String subheading,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        try {
            FreeDto freeDto = new FreeDto();
            freeDto.setTitle(title);
            freeDto.setContent(content);
            freeDto.setUserNo(userNo);
            freeDto.setSubheading(subheading != null ? subheading : "");

            // 서비스에서 MultipartFile 처리
            freeService.insertBoard(freeDto, file);

            return new ResponseEntity<>("게시글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("게시글 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{boardNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateBoard(
            @PathVariable int boardNo,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("userNo") int userNo,
            @RequestParam(value = "subheading", required = false) String subheading,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        try {
            FreeDto freeDto = new FreeDto();
            freeDto.setBoardNo(boardNo);
            freeDto.setTitle(title);
            freeDto.setContent(content);
            freeDto.setSubheading(subheading != null ? subheading : "");
            freeDto.setUserNo(userNo);

            // 서비스에서 MultipartFile 처리
            int result = freeService.updateBoard(freeDto, file);

            if (result > 0) return new ResponseEntity<>("게시글이 성공적으로 수정되었습니다.", HttpStatus.OK);
            else return new ResponseEntity<>("게시글 수정에 실패했습니다. 작성자만 수정할 수 있습니다.", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("게시글 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====== 게시글 삭제 ======
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<String> softDeleteBoard(
            @PathVariable int boardNo,
            @RequestParam("userNo") int userNo) {

        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        boolean isDeleted = freeService.softDeleteBoard(boardNo, userNo);
        if (isDeleted) return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("게시글 삭제에 실패했거나 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    // ====== 좋아요 ======
    @PostMapping("/{boardNo}/likes")
    public ResponseEntity<String> toggleLike(@PathVariable int boardNo, @RequestBody Map<String, Integer> payload) {
        int userNo = payload.get("userNo");
        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        boolean liked = freeService.toggleLike(boardNo, userNo);
        if (liked) return new ResponseEntity<>("좋아요가 추가되었습니다.", HttpStatus.OK);
        else return new ResponseEntity<>("좋아요가 취소되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/{boardNo}/likes/status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(@PathVariable int boardNo, @RequestParam("userNo") int userNo) {
        if (!isLoggedIn(userNo)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean isLiked = freeService.isLiked(boardNo, userNo);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLiked", isLiked);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardNo}/likes/count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable int boardNo) {
        int likesCount = freeService.getLikesCount(boardNo);
        return new ResponseEntity<>(likesCount, HttpStatus.OK);
    }

    // ====== 댓글 ======
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

    @PostMapping("/replies")
    public ResponseEntity<String> addReply(@RequestBody ReplyDto replyDto) {
        if (!isLoggedIn(replyDto.getUserNo())) return unauthorizedResponse();

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

    @PutMapping("/replies/{replyNo}")
    public ResponseEntity<String> updateReply(@PathVariable int replyNo, @RequestBody ReplyDto replyDto) {
        if (!isLoggedIn(replyDto.getUserNo())) return unauthorizedResponse();

        try {
            replyDto.setReplyNo(replyNo);
            int result = freeService.updateReply(replyDto);
            if (result > 0) return new ResponseEntity<>("댓글이 성공적으로 수정되었습니다.", HttpStatus.OK);
            else return new ResponseEntity<>("댓글 수정에 실패했습니다. 작성자만 수정할 수 있습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyNo, @RequestParam("userNo") int userNo) {
        if (!isLoggedIn(userNo)) return unauthorizedResponse();

        try {
            boolean isDeleted = freeService.deleteReply(replyNo, userNo);
            if (isDeleted) return new ResponseEntity<>("댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
            else return new ResponseEntity<>("댓글 삭제에 실패했습니다. 권한을 확인해주세요.", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ====== 파일 업로드 헬퍼 ======
    private String getChangeName(MultipartFile upfile, String webPath) {
        String projectRoot = System.getProperty("user.dir");
        File dir = new File(projectRoot, "resources/" + webPath);
        if (!dir.exists()) dir.mkdirs();

        String originName = upfile.getOriginalFilename();
        String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int random = (int) (Math.random() * 90000 + 10000);
        String ext = originName.substring(originName.lastIndexOf("."));
        String changeName = currentTime + random + ext;

        try {
            upfile.transferTo(new File(dir, changeName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return changeName;
    }
}
