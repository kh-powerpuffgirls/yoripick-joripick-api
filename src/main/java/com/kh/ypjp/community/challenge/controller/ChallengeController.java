package com.kh.ypjp.community.challenge.controller;

import com.kh.ypjp.community.challenge.dto.*;
import com.kh.ypjp.community.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/community/challenge")
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllPosts(HttpServletRequest request) {
        List<ChallengeDto> challengeList = challengeService.getAllPosts();

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        for (ChallengeDto challenge : challengeList) {
            if (challenge.getServerName() != null && !challenge.getServerName().isEmpty()) {
                String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/images/")
                        .path(challenge.getServerName())
                        .toUriString();
                challenge.setImageUrl(imageUrl);
            }
        }
        return ResponseEntity.ok(challengeList);
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDto> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userNo,
            HttpServletRequest request) {

        Optional<ChallengeDto> optionalPost = challengeService.getPostAndIncrementViews(id, userNo);
        
        if (optionalPost.isPresent()) {
            ChallengeDto post = optionalPost.get();
            if (post.getServerName() != null && !post.getServerName().isEmpty()) {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                String imageUrl = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/images/")
                        .path(post.getServerName())
                        .toUriString();
                post.setImageUrl(imageUrl);
            }
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/navigation/{challengeNo}")
    public ResponseEntity<Map<String, Long>> getNavigation(@PathVariable Long challengeNo) {
        Map<String, Long> navigation = challengeService.getNavigation(challengeNo);
        return ResponseEntity.ok(navigation);
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("chInfoNo") Long chInfoNo,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @AuthenticationPrincipal Long userNo) {
        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setUserNo(userNo);
        challengeDto.setChInfoNo(chInfoNo);
        challengeDto.setVideoUrl(videoUrl);
        challengeDto.setTitle(title);

        try {
            Long newChallengeNo = challengeService.createPostAndReturnNo(challengeDto, file);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "게시글 등록 완료");
            response.put("challengeNo", newChallengeNo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("게시글 등록 실패: {}", e.getMessage());
            log.error("Stack trace:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "게시글 등록 실패"));
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(
            @PathVariable("id") Long id,
            @RequestParam("chInfoNo") Long chInfoNo,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @AuthenticationPrincipal Long userNo,
            Authentication authentication) {

        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setChallengeNo(id);
        challengeDto.setUserNo(userNo);
        challengeDto.setChInfoNo(chInfoNo);
        challengeDto.setVideoUrl(videoUrl);
        challengeDto.setTitle(title);

        try {
            Optional<ChallengeDto> updatedPost = challengeService.updatePost(id, challengeDto, file, userNo, isAdmin);
            if (updatedPost.isPresent()) {
                return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("게시글 수정에 실패했습니다. 작성자 또는 관리자만 수정할 수 있습니다.");
            }
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: {}", e.getMessage());
            log.error("Stack trace:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시글 수정 중 오류가 발생했습니다.");
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userNo,
            Authentication authentication) {

        if (userNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean deleted = challengeService.deletePost(id, userNo, isAdmin);
        if (deleted) return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("게시글 삭제에 실패했거나 권한이 없습니다. 작성자 또는 관리자만 삭제할 수 있습니다.");
    }

    // 댓글 전체 조회
    @GetMapping("/replies/{challengeNo}")
    public ResponseEntity<List<ChallengeReplyDto>> getReplies(@PathVariable Long challengeNo) {
        return ResponseEntity.ok(challengeService.selectAllRepliesByChallengeId(challengeNo));
    }

    // 댓글 등록
    @PostMapping("/replies")
    public ResponseEntity<String> addReply(@RequestBody ChallengeReplyDto replyDto,
                                           @AuthenticationPrincipal Long userNo) {
        if (replyDto.getCategory() == null || replyDto.getCategory().isEmpty())
            replyDto.setCategory("CHALLENGE");
        replyDto.setUserNo(userNo);
        challengeService.insertReply(replyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글이 성공적으로 등록되었습니다.");
    }

    // 댓글 수정
    @PutMapping("/replies/{replyNo}")
    public ResponseEntity<String> updateReply(@PathVariable Long replyNo, @RequestBody ChallengeReplyDto replyDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }
        Long userNo = Long.parseLong(principal.getName());

        replyDto.setReplyNo(replyNo);
        int result = challengeService.updateReply(replyDto, userNo);
        if (result > 0) return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("댓글 수정에 실패했습니다. 작성자만 수정할 수 있습니다.");
    }

    // 댓글 삭제
    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyNo, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }
        Long userNo = Long.parseLong(principal.getName());

        boolean deleted = challengeService.deleteReply(replyNo, userNo);
        if (deleted) return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("댓글 삭제에 실패했습니다. 권한을 확인해주세요.");
    }

    // 진행 중인 챌린지 정보 조회
    @GetMapping("/active")
    public ResponseEntity<List<ChallengeInfoDto>> getActiveChallengeInfo() {
        return challengeService.getActiveChallengeInfo()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 챌린지 신청 등록
    @PostMapping("/suggestion")
    public ResponseEntity<String> createSuggestion(@RequestBody ChallengeSuggestionDto suggestionDto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }
        Long userNo = Long.parseLong(principal.getName());

        suggestionDto.setUserNo(userNo);
        int result = challengeService.createSuggestion(suggestionDto);
        if (result > 0) return ResponseEntity.status(HttpStatus.CREATED).body("챌린지 신청서가 성공적으로 등록되었습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("챌린지 신청서 등록에 실패했습니다.");
    }

    @PostMapping("/like/{challengeNo}")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long challengeNo, 
            @RequestParam("status") String likeStatus, // "LIKE" or "DISLIKE"
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용해주세요.");
        }
        Long userNo = Long.parseLong(principal.getName());

        challengeService.setLikeStatus(challengeNo, userNo, likeStatus);
        return ResponseEntity.ok(likeStatus.equals("LIKE") ? "좋아요 적용됨" : "싫어요 적용됨");
    }

    @GetMapping("/like/status/{challengeNo}")
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable Long challengeNo, Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(false);
        }
        Long userNo = Long.parseLong(principal.getName());
        
        boolean isLiked = challengeService.getLikeStatus(challengeNo, userNo);
        return ResponseEntity.ok(isLiked);
    }

    // 좋아요 개수 조회
    @GetMapping("/like/count/{challengeNo}")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long challengeNo) {
        try {
            int count = challengeService.getLikesCount(challengeNo);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("좋아요 개수 조회 실패: {}", e.getMessage());
            log.error("Stack trace:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }
}