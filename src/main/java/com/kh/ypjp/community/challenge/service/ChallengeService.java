package com.kh.ypjp.community.challenge.service;

import com.kh.ypjp.community.challenge.dao.ChallengeDao;
import com.kh.ypjp.community.challenge.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeDao challengeDao;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<ChallengeDto> getAllPosts() {
        return challengeDao.findAll();
    }

    @Transactional
    public Optional<ChallengeDto> getPostAndIncrementViews(
            Long id,
            Long userNo,
            HttpServletRequest req,
            HttpServletResponse res) {
        
        ChallengeDto post = challengeDao.findById(id);

        if (post == null) {
            return Optional.empty();
        }

        // 1. 조회수 증가 로직
        // 게시글 작성자 본인이 아닐 경우에만 조회수 증가 로직 실행
        if (userNo == null || !userNo.equals(post.getUserNo())) {
            
            // 2. 사용자 식별자 결정 (로그인 유저는 userNo, 비로그인 유저는 세션 ID)
            String userIdentifier = (userNo != null) ? String.valueOf(userNo) : req.getSession().getId();
            String cookieName = "viewed_challenge_" + userIdentifier;
            String viewedPosts = null;

            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        viewedPosts = cookie.getValue();
                        break;
                    }
                }
            }

            boolean isAlreadyViewed = false;
            if (viewedPosts != null) {
                isAlreadyViewed = Arrays.asList(viewedPosts.split("_")).contains(String.valueOf(id));
            }

            if (!isAlreadyViewed) {
                // DB 조회수 증가
                challengeDao.incrementViews(id);
                
                // 쿠키 업데이트
                String newViewedPosts = (viewedPosts == null) ? String.valueOf(id) : viewedPosts + "_" + id;
                Cookie cookie = new Cookie(cookieName, newViewedPosts);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24); // 24시간 동안 유효
                res.addCookie(cookie);
                
                // DTO 객체도 조회수 업데이트
                post.setViews(post.getViews() + 1);
            }
        }
        
        return Optional.of(post);
    }
    
    @Transactional
    public Long createPostAndReturnNo(ChallengeDto challengeDto, MultipartFile image) throws Exception {
        if (challengeDto.getUserNo() == null)
            throw new IllegalArgumentException("작성자는 필수입니다.");
        if (challengeDto.getVideoUrl() == null || challengeDto.getVideoUrl().trim().isEmpty())
            throw new IllegalArgumentException("비디오 URL은 필수입니다.");

        List<ChallengeInfoDto> activeChallenges = challengeDao.findActiveChallengeInfo();
        if (activeChallenges == null || activeChallenges.isEmpty()) {
            throw new IllegalStateException("진행 중인 챌린지가 없습니다.");
        }
        ChallengeInfoDto activeChallenge = activeChallenges.get(0);

        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(activeChallenge.getStartDate()) || currentDate.isAfter(activeChallenge.getEndDate()))
            throw new IllegalStateException("챌린지 기간 내만 등록 가능합니다.");

        Integer newImageNo = null;
        if (image != null && !image.isEmpty()) {
            String savedFileName = saveFileToServer(image);
            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("originName", image.getOriginalFilename());
            imageInfo.put("serverName", savedFileName);
            challengeDao.insertImage(imageInfo);
            newImageNo = (Integer) imageInfo.get("imageNo");
        }

        challengeDto.setImageNo(newImageNo);
        challengeDto.setChInfoNo(activeChallenge.getChInfoNo());
        challengeDao.saveChallenge(challengeDto);

        return challengeDto.getChallengeNo();
    }

    @Transactional
    public Optional<ChallengeDto> updatePost(Long id, ChallengeDto challengeDto, MultipartFile file, Long userNo) {
        ChallengeDto existingPost = challengeDao.findById(id);
        if (existingPost == null || !existingPost.getUserNo().equals(userNo)) {
            return Optional.empty();
        }

        challengeDto.setChallengeNo(id);
        challengeDao.update(challengeDto);

        if (file != null && !file.isEmpty()) {
            Integer imageNo = challengeDao.selectImageNoByChallengeNo(id);
            String savedFileName = saveFileToServer(file);

            Map<String, Object> imageInfo = new HashMap<>();
            if (imageNo != null) {
                imageInfo.put("imageNo", imageNo);
            }
            imageInfo.put("originName", file.getOriginalFilename());
            imageInfo.put("serverName", savedFileName);

            if (imageNo != null) {
                challengeDao.updateImage(imageInfo);
            }
        }

        return Optional.of(challengeDao.findById(id));
    }

    @Transactional
    public boolean deletePost(Long id, Long currentUserId) {
        List<ChallengeInfoDto> activeChallenges = challengeDao.findActiveChallengeInfo();
        if (activeChallenges == null || activeChallenges.isEmpty()) {
            throw new IllegalStateException("진행 중인 챌린지가 없습니다.");
        }
        ChallengeInfoDto activeChallenge = activeChallenges.get(0);

        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(activeChallenge.getStartDate()) || currentDate.isAfter(activeChallenge.getEndDate()))
            throw new IllegalStateException("챌린지 기간 내만 삭제 가능합니다.");

        ChallengeDto existingPost = challengeDao.findById(id);
        if (existingPost != null && existingPost.getUserNo().equals(currentUserId)) {
            return challengeDao.updateDeleteStatus(id, "Y") > 0;
        }
        return false;
    }

    // 이 메서드는 이제 사용하지 않습니다.
    // @Transactional
    // public void incrementViews(Long id) {
    //     challengeDao.incrementViews(id);
    // }

    @Transactional
    public void toggleLike(Long userId, Long challengeId) {
        ChallengeLikesDto likesDto = new ChallengeLikesDto();
        likesDto.setUserNo(userId);
        likesDto.setChallengeNo(challengeId);

        if (challengeDao.checkIfLiked(likesDto) > 0) {
            challengeDao.deleteLike(likesDto);
        } else {
            challengeDao.insertLike(likesDto);
        }
    }

    public boolean checkIfLiked(Long userId, Long challengeId) {
        ChallengeLikesDto likesDto = new ChallengeLikesDto();
        likesDto.setUserNo(userId);
        likesDto.setChallengeNo(challengeId);

        return challengeDao.checkIfLiked(likesDto) > 0;
    }

    public Optional<List<ChallengeInfoDto>> getActiveChallengeInfo() {
        return Optional.ofNullable(challengeDao.findActiveChallengeInfo());
    }

    public List<ChallengeReplyDto> selectAllRepliesByChallengeId(Long challengeId) {
        return challengeDao.selectAllRepliesByChallengeId(challengeId);
    }
    
    @Transactional
    public int insertReply(ChallengeReplyDto replyDto) {
        return challengeDao.insertReply(replyDto);
    }

    @Transactional
    public int updateReply(ChallengeReplyDto replyDto, Long currentUserId) {
        ChallengeReplyDto existingReply = challengeDao.selectReplyById(replyDto.getReplyNo());

        if (existingReply == null || !existingReply.getUserNo().equals(currentUserId)) {
            return 0;
        }

        if ("REPLY".equals(replyDto.getCategory())) {
            Long newRefNo = (long) replyDto.getRefNo();
            Long replyNo = (long) replyDto.getReplyNo(); 

            int circularCount = challengeDao.checkCircularReference(replyNo, newRefNo);
            if (circularCount > 0) {
                throw new IllegalStateException("댓글 계층 구조에 순환 참조가 발생하여 댓글을 수정할 수 없습니다.");
            }
        }
        return challengeDao.updateReply(replyDto);
    }

    @Transactional
    public int deleteReply(Long replyNo, Long currentUserId) {
        ChallengeReplyDto reply = challengeDao.selectReplyById(replyNo);
        if (reply == null || !reply.getUserNo().equals(currentUserId)) {
            return 0;
        }
        return challengeDao.deleteReply(replyNo, currentUserId);
    }

    @Transactional
    public int createSuggestion(ChallengeSuggestionDto suggestionDto) {
        return challengeDao.insertSuggestion(suggestionDto);
    }

    private String saveFileToServer(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String savedFileName = UUID.randomUUID().toString() + extension;
            String filePath = uploadDir + File.separator + savedFileName;

            File dest = new File(filePath);
            dest.getParentFile().mkdirs();
            file.transferTo(dest);

            return savedFileName;
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }
}