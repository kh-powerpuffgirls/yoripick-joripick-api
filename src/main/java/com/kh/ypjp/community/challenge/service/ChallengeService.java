package com.kh.ypjp.community.challenge.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.challenge.dao.ChallengeDao;
import com.kh.ypjp.community.challenge.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Optional<ChallengeDto> getPostAndIncrementViews(Long id, Long userNo) {
        ChallengeDto post = challengeDao.findByIdWithImage(id);

        if (post == null) {
            return Optional.empty();
        }

        // 작성자 본인이 아니면 조회수 증가
        if (userNo == null || !userNo.equals(post.getUserNo())) {
            challengeDao.incrementViews(id);
            post.setViews(post.getViews() + 1);
        }

        return Optional.of(post);
    }


    // 게시글 등록 및 이미지 저장
    @Transactional
    public Long createPostAndReturnNo(ChallengeDto challengeDto, MultipartFile file) throws Exception {
        if (challengeDto.getUserNo() == null) {
            throw new IllegalArgumentException("로그인 후 이용할 수 있습니다.");
        }

        List<ChallengeInfoDto> activeChallenges = challengeDao.findActiveChallengeInfo();
        if (activeChallenges == null || activeChallenges.isEmpty()) {
            throw new IllegalStateException("진행 중인 챌린지가 없습니다.");
        }

        ChallengeInfoDto activeChallenge = activeChallenges.get(0);
        LocalDate today = LocalDate.now();
        if (today.isBefore(activeChallenge.getStartDate()) || today.isAfter(activeChallenge.getEndDate())) {
            throw new IllegalStateException("챌린지 기간 내에만 등록 가능합니다.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("챌린지 이미지는 필수입니다.");
        }

        String webPath = "challenges/" + challengeDto.getUserNo();
        String savedFileName = utilService.getChangeName(file, webPath);
        String serverName = webPath + "/" + savedFileName;

        challengeDto.setServerName(serverName);
        challengeDto.setOriginName(file.getOriginalFilename());

        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(serverName)
                .toUriString();
        challengeDto.setImageUrl(imageUrl);

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
        Long authorNo = challengeDao.findUserNoById(id);
        if (authorNo == null || !authorNo.equals(userNo)) {
            return Optional.empty();
        }

        challengeDto.setChallengeNo(id);

        if (file != null && !file.isEmpty()) {
            String webPath = "challenges/" + challengeDto.getUserNo();
            String savedFileName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + savedFileName;

            challengeDto.setServerName(serverName);
            challengeDto.setOriginName(file.getOriginalFilename());

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(serverName)
                    .toUriString();
            challengeDto.setImageUrl(imageUrl);

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", challengeDto.getOriginName());

            utilService.insertImage(param);
            Long imageNo = utilService.getImageNo(param);
            challengeDto.setImageNo(imageNo.intValue());
        }

        challengeDao.update(challengeDto);
        return Optional.of(challengeDao.findByIdWithImage(id));
    }

    // 게시글 삭제
    @Transactional
    public boolean deletePost(Long id, Long userNo) {
        Long authorNo = challengeDao.findUserNoById(id);
        if (authorNo == null || !authorNo.equals(userNo)) {
            return false;
        }
        return challengeDao.updateDeleteStatus(id, "Y") > 0;
    }

    // 좋아요 👍👍👍
    @Transactional
    public boolean toggleLike(Long challengeNo, Long userNo) {
        boolean liked = challengeDao.checkIfLiked(userNo, challengeNo) > 0;
        if (liked) {
            challengeDao.deleteLike(userNo, challengeNo);
        } else {
            challengeDao.insertLike(userNo, challengeNo);
        }
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

    // 댓글 수정
    @Transactional
    public int updateReply(ChallengeReplyDto replyDto, Long userNo) {
        ChallengeReplyDto existing = challengeDao.selectReplyById(replyDto.getReplyNo());
        if (existing == null || !existing.getUserNo().equals(userNo)) {
            return 0;
        }
        return challengeDao.updateReply(replyDto);
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteReply(Long replyNo, Long userNo) {
        ChallengeReplyDto reply = challengeDao.selectReplyById(replyNo);
        if (reply == null || !reply.getUserNo().equals(userNo)) {
            return false;
        }
        return challengeDao.deleteReply(replyNo, userNo) > 0;
    }

    // 챌린지 신청 등록
    @Transactional
    public int createSuggestion(ChallengeSuggestionDto suggestionDto) {
        return challengeDao.insertSuggestion(suggestionDto);
    }
}
