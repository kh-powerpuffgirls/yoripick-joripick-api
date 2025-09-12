package com.kh.ypjp.community.challenge.controller;

import com.kh.ypjp.community.challenge.dto.*;
import com.kh.ypjp.community.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;

@RestController
@RequestMapping("/community/challenge")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * userDetails 객체에서 userNo를 안전하게 추출하는 유틸리티 메서드
     *
     * @param userDetails Spring Security의 UserDetails 객체
     * @return 유효한 userNo
     * @throws IllegalArgumentException userDetails가 null일 경우 발생
     */
    private Long getUserNoFromUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인 후 이용할 수 있습니다.");
        }
        return Long.valueOf(userDetails.getUsername());
    }

    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllPosts() {
        return ResponseEntity.ok(challengeService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "isAuthenticated() ? userDetails : null") UserDetails userDetails,
            HttpServletRequest req,
            HttpServletResponse res) {
        
        Long userNo = (userDetails != null) ? Long.valueOf(userDetails.getUsername()) : null;

        return challengeService.getPostAndIncrementViews(id, userNo, req, res)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestPart("challengeDto") ChallengeDto challengeDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = Long.valueOf(userDetails.getUsername());
            challengeDto.setUserNo(userNo);
            Long newChallengeNo = challengeService.createPostAndReturnNo(challengeDto, file);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "게시글이 성공적으로 등록되었습니다.");
            response.put("challengeNo", newChallengeNo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) { // RuntimeException으로 통합
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "게시글 등록 중 오류가 발생했습니다."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChallengeDto> updatePost(
            @PathVariable Long id,
            @RequestPart("challengeDto") ChallengeDto challengeDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            Optional<ChallengeDto> updatedPost = challengeService.updatePost(id, challengeDto, file, userNo);
            return updatedPost
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            if (challengeService.deletePost(id, userNo)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/like/{challengeId}")
    public ResponseEntity<Void> toggleLike(@PathVariable Long challengeId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            challengeService.toggleLike(userNo, challengeId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/like/status/{challengeId}")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable Long challengeId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            return ResponseEntity.ok(challengeService.checkIfLiked(userNo, challengeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/replies/{challengeId}")
    public ResponseEntity<List<ChallengeReplyDto>> getReplies(@PathVariable Long challengeId) {
        try {
            List<ChallengeReplyDto> replies = challengeService.selectAllRepliesByChallengeId(challengeId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/replies")
    public ResponseEntity<String> addReply(@RequestBody ChallengeReplyDto replyDto, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            if (replyDto.getCategory() == null || replyDto.getCategory().isEmpty()) {
                replyDto.setCategory("CHALLENGE");
            }
            replyDto.setUserNo(userNo);
            challengeService.insertReply(replyDto);
            return new ResponseEntity<>("댓글이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("로그인 후 이용할 수 있습니다.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/replies/{replyNo}")
    public ResponseEntity<String> updateReply(@PathVariable Long replyNo, @RequestBody ChallengeReplyDto replyDto, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            replyDto.setReplyNo(replyNo);
            int result = challengeService.updateReply(replyDto, userNo);
            if (result > 0) {
                return new ResponseEntity<>("댓글이 성공적으로 수정되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("댓글 수정에 실패했습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("로그인 후 이용할 수 있습니다.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyNo, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            int result = challengeService.deleteReply(replyNo, userNo);
            if (result > 0) {
                return new ResponseEntity<>("댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("댓글 삭제에 실패했습니다. 권한을 확인해주세요.", HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("로그인 후 이용할 수 있습니다.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<ChallengeInfoDto>> getActiveChallengeInfo() {
        return challengeService.getActiveChallengeInfo()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/suggestion")
    public ResponseEntity<String> createSuggestion(@RequestBody ChallengeSuggestionDto suggestionDto, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userNo = getUserNoFromUserDetails(userDetails);
            suggestionDto.setUserNo(userNo);
            int result = challengeService.createSuggestion(suggestionDto);
            if (result > 0) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("챌린지 신청서가 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("챌린지 신청서 등록에 실패했습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("챌린지 신청서 등록 중 오류가 발생했습니다.");
        }
    }
}