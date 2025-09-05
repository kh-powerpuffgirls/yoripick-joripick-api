package com.kh.ypjp.community.challenge.controller;

import com.kh.ypjp.community.challenge.dto.ChallengeDto;
import com.kh.ypjp.community.challenge.dto.ChallengeReplyDto;
import com.kh.ypjp.community.challenge.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID; // 랜덤하고 고유한 문자열 생성해줌. 필요없음 빼기
import java.util.HashMap;

@RestController
@RequestMapping("/community/challenge")
@CrossOrigin(origins = "http://localhost:5173")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final Long USER_NO = 2L; // Temporary fixed user ID

    @Autowired
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllPosts() {
        List<ChallengeDto> posts = challengeService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // Retrieves a specific user challenge post
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDto> getPost(@PathVariable Long id) {
        challengeService.incrementViews(id);
        return challengeService.getPost(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChallengeDto> createPost(@RequestBody ChallengeDto challengeDto) {
        challengeDto.setUserNo(USER_NO);
        ChallengeDto createdPost = challengeService.createPost(challengeDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
    
    // Updates a user challenge post
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDto> updatePost(@PathVariable Long id, @RequestBody ChallengeDto challengeDto) {
        Optional<ChallengeDto> updatedPost = challengeService.updatePost(id, challengeDto, USER_NO);
        return updatedPost
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (challengeService.deletePost(id, USER_NO)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    @PostMapping("/like/{challengeId}")
    public ResponseEntity<Void> toggleLike(@PathVariable Long challengeId) {
        challengeService.toggleLike(USER_NO, challengeId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/like/status/{challengeId}")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable Long challengeId, @RequestParam Long userId) {
        return ResponseEntity.ok(challengeService.checkIfLiked(userId, challengeId));
    }
    
    @GetMapping("/replies/{challengeId}")
    public ResponseEntity<List<ChallengeReplyDto>> getReplies(@PathVariable Long challengeId) {
        List<ChallengeReplyDto> replies = challengeService.getReplies(challengeId);
        return ResponseEntity.ok(replies);
    }
    
    @PostMapping("/replies/{challengeId}")
    public ResponseEntity<ChallengeReplyDto> createReply(@PathVariable Long challengeId, @RequestBody ChallengeReplyDto replyDto) {
        replyDto.setRefNo(challengeId.intValue());
        replyDto.setUserNo(USER_NO.intValue());
        
        int result = challengeService.createReply(replyDto);
        if (result > 0) {
            return new ResponseEntity<>(replyDto, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ChallengeDto> getActiveChallengeInfo() {
        return challengeService.getActiveChallengeInfo()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Handles image uploads
    @PostMapping("/images/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("error", "파일이 비어있습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // 이미지 저장 경로 (프로젝트 내부에 'upload' 디렉토리 생성 필요)
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/upload/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 파일명 생성 (UUID를 사용하여 고유한 파일명 보장)
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 파일 저장
            File dest = new File(uploadDir + uniqueFilename);
            file.transferTo(dest);

            // 클라이언트에 반환할 정보
            response.put("fileName", uniqueFilename);
            response.put("filePath", "/images/upload/" + uniqueFilename);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "파일 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}