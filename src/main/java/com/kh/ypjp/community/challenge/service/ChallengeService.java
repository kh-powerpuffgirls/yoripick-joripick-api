package com.kh.ypjp.community.challenge.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.challenge.dao.ChallengeDao;
import com.kh.ypjp.community.challenge.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeDao challengeDao;
    private final UtilService utilService;

    // 모든 챌린지 게시글 조회
    public List<ChallengeDto> getAllPosts() {
        return challengeDao.findAll();
    }

    // 게시글 조회 및 조회수 증가 처리
    @Transactional
    public Optional<ChallengeDto> getPostAndIncrementViews(Long id, Long userNo, HttpServletRequest req, HttpServletResponse res) {
        ChallengeDto post = challengeDao.findById(id);
        if (post == null) return Optional.empty();

        // 게시글 작성자가 아닐 경우에만 조회수 증가
        if (userNo == null || !userNo.equals(post.getUserNo())) {
            String userIdentifier = (userNo != null) ? String.valueOf(userNo) : req.getSession().getId();
            String cookieName = "viewed_challenge_" + userIdentifier;
            String viewedPosts = null;

            // 쿠키에서 조회 기록 확인
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        viewedPosts = cookie.getValue();
                        break;
                    }
                }
            }

            // 조회 기록이 없을 경우에만 조회수 증가 및 쿠키 생성
            boolean isAlreadyViewed = viewedPosts != null && Arrays.asList(viewedPosts.split("_")).contains(String.valueOf(id));
            if (!isAlreadyViewed) {
                challengeDao.incrementViews(id);

                String newViewedPosts = (viewedPosts == null) ? String.valueOf(id) : viewedPosts + "_" + id;
                Cookie cookie = new Cookie(cookieName, newViewedPosts);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24);
                res.addCookie(cookie);

                post.setViews(post.getViews() + 1);
            }
        }

        return Optional.of(post);
    }

    // 게시글 등록 및 이미지 저장
    @Transactional
    public Long createPostAndReturnNo(ChallengeDto challengeDto, MultipartFile file) throws Exception {
        // 필수 값 검증
        if (challengeDto.getUserNo() == null)
            throw new IllegalArgumentException("로그인 후 이용할 수 있습니다.");

        // 현재 진행 중인 챌린지 정보 조회
        List<ChallengeInfoDto> activeChallenges = challengeDao.findActiveChallengeInfo();
        if (activeChallenges == null || activeChallenges.isEmpty())
            throw new IllegalStateException("진행 중인 챌린지가 없습니다.");

        // 챌린지 기간 검증
        ChallengeInfoDto activeChallenge = activeChallenges.get(0);
        LocalDate today = LocalDate.now();
        if (today.isBefore(activeChallenge.getStartDate()) || today.isAfter(activeChallenge.getEndDate()))
            throw new IllegalStateException("챌린지 기간 내만 등록 가능합니다.");

        // 파일 유무 검증
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("챌린지 이미지는 필수입니다.");

        // 이미지 저장 및 관련 정보 DB 저장
        String webPath = "challenges/" + challengeDto.getUserNo();
        String savedFileName = utilService.getChangeName(file, webPath);
        String serverName = webPath + "/" + savedFileName;

        Map<String, Object> param = new HashMap<>();
        param.put("serverName", serverName);
        param.put("originName", file.getOriginalFilename());
        utilService.insertImage(param);
        Long imageNo = utilService.getImageNo(param);

        challengeDto.setChInfoNo(activeChallenge.getChInfoNo());
        challengeDto.setImageNo(imageNo.intValue());
        challengeDao.saveChallenge(challengeDto);

        return challengeDto.getChallengeNo();
    }

    // 게시글 수정 및 이미지 업데이트
    @Transactional
    public Optional<ChallengeDto> updatePost(Long id, ChallengeDto challengeDto, MultipartFile file, Long userNo) {
        // 수정 권한 검증
        ChallengeDto existingPost = challengeDao.findById(id);
        if (existingPost == null || !existingPost.getUserNo().equals(userNo)) return Optional.empty();

        challengeDto.setChallengeNo(id);

        // 새 파일이 있을 경우 이미지 업데이트 처리
        if (file != null && !file.isEmpty()) {
            String webPath = "challenges/" + challengeDto.getUserNo();
            String savedFileName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + savedFileName;

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", file.getOriginalFilename());
            utilService.insertImage(param);
            Long imageNo = utilService.getImageNo(param);

            challengeDto.setImageNo(imageNo.intValue());
            challengeDto.setOriginName(file.getOriginalFilename());
            challengeDto.setServerName(serverName);
        }

        challengeDao.update(challengeDto);
        return Optional.of(challengeDao.findById(id));
    }

    // 게시글 삭제
    @Transactional
    public boolean deletePost(Long id, Long userNo) {
        // 삭제 권한 검증
        ChallengeDto existingPost = challengeDao.findById(id);
        if (existingPost != null && existingPost.getUserNo().equals(userNo)) {
            return challengeDao.updateDeleteStatus(id, "Y") > 0;
        }
        return false;
    }

    // 좋아요 👍👍👍
    @Transactional
    public boolean toggleLike(Long challengeNo, Long userNo) {
        // 좋아요 기록 확인 후 추가 또는 삭제
        boolean liked = challengeDao.checkIfLiked(userNo, challengeNo) > 0;
        if (liked) challengeDao.deleteLike(userNo, challengeNo);
        else challengeDao.insertLike(userNo, challengeNo);
        return !liked;
    }

    // 좋아요 상태 확인
    public boolean isLiked(Long challengeNo, Long userNo) {
        return challengeDao.checkIfLiked(userNo, challengeNo) > 0;
    }

    // 좋아요 개수 조회
    public int getLikesCount(Long challengeNo) {
        Integer count = challengeDao.getLikesCount(challengeNo); 
        return count != null ? count : 0;
    }

    // 현재 진행 중인 챌린지 정보 조회
    public Optional<List<ChallengeInfoDto>> getActiveChallengeInfo() {
        return Optional.ofNullable(challengeDao.findActiveChallengeInfo());
    }

    // 특정 게시글의 댓글 전체 조회
    public List<ChallengeReplyDto> selectAllRepliesByChallengeId(Long challengeId) {
        return challengeDao.selectAllRepliesByChallengeId(challengeId);
    }

    // 댓글 등록
    @Transactional
    public int insertReply(ChallengeReplyDto replyDto) {
        return challengeDao.insertReply(replyDto);
    }

    // 댓글 수정 (트랜잭션 관리)
    @Transactional
    public int updateReply(ChallengeReplyDto replyDto, Long userNo) {
        // 수정 권한 검증
        ChallengeReplyDto existing = challengeDao.selectReplyById(replyDto.getReplyNo());
        if (existing == null || !existing.getUserNo().equals(userNo)) return 0;
        return challengeDao.updateReply(replyDto);
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteReply(Long replyNo, Long userNo) {
        // 삭제 권한 검증
        ChallengeReplyDto reply = challengeDao.selectReplyById(replyNo);
        if (reply == null || !reply.getUserNo().equals(userNo)) return false;
        return challengeDao.deleteReply(replyNo, userNo) > 0;
    }

    // 챌린지 신청 등록 (트랜잭션 관리)
    @Transactional
    public int createSuggestion(ChallengeSuggestionDto suggestionDto) {
        return challengeDao.insertSuggestion(suggestionDto);
    }
}