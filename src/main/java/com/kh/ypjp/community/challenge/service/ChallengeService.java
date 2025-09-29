package com.kh.ypjp.community.challenge.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.challenge.dao.ChallengeDao;
import com.kh.ypjp.community.challenge.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeDao challengeDao;
    private final UtilService utilService;

    // 모든 챌린지 게시글 조회 (프로필 이미지 경로 가공 로직 추가)
    public List<ChallengeDto> getAllPosts() {
        List<ChallengeDto> postList = challengeDao.findAll();
        
        for (ChallengeDto post : postList) {
            if (post.getProfileImageServerName() != null && !post.getProfileImageServerName().isEmpty()) {
                String fullPath = "profile/" + post.getUserNo() + "/" + post.getProfileImageServerName();

                String imageUrl = "/images/" + fullPath;
                post.setProfileImageServerName(imageUrl); 
            }
        }
        
        return postList;
    }

	 public Optional<ChallengeDto> getPost(Long id) {
	     ChallengeDto post = challengeDao.findByIdWithImage(id); 
	     if (post == null) return Optional.empty();
	
	     if (post.getProfileImageServerName() != null && !post.getProfileImageServerName().isEmpty()) {
	         String fullPath = "profile/" + post.getUserNo() + "/" + post.getProfileImageServerName();
	         String imageUrl = "/images/" + fullPath;
	         post.setProfileImageServerName(imageUrl);
	     }
	     
	     return Optional.of(post);
	 }

    @Transactional
    public boolean incrementViewsWithSelfCheck(Long id, Long userNo) {

        Long authorNo = challengeDao.findUserNoById(id); 
        if (authorNo == null) return false;

        if (userNo == null || !userNo.equals(authorNo)) {
            challengeDao.incrementViews(id);
            return true;
        }
        return false;
    }

    public Map<String, Long> getNavigation(Long challengeNo) {
        Map<String, Long> navigation = new HashMap<>();
        navigation.put("next", challengeDao.findNextChallenge(challengeNo));
        navigation.put("prev", challengeDao.findPreviousChallenge(challengeNo));
        return navigation;
    }

    @Transactional
    public Long createPostAndReturnNo(ChallengeDto challengeDto, MultipartFile file) throws Exception {
        if (challengeDto.getUserNo() == null)
            throw new IllegalArgumentException("로그인 후 이용할 수 있습니다.");

        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("챌린지 이미지는 필수입니다.");

        String webPath = "challenge/" + challengeDto.getUserNo();
        String savedFileName = utilService.getChangeName(file, webPath);
        String serverName = webPath + "/" + savedFileName;

        challengeDto.setServerName(serverName);
        challengeDto.setOriginName(file.getOriginalFilename());

        Map<String, Object> param = new HashMap<>();
        param.put("serverName", serverName);
        param.put("originName", file.getOriginalFilename());
        utilService.insertImage(param);
        Long imageNo = utilService.getImageNo(param);

        challengeDto.setImageNo(imageNo.intValue());
        challengeDao.saveChallenge(challengeDto);

        return challengeDto.getChallengeNo();
    }

    @Transactional
    public Optional<ChallengeDto> updatePost(Long id, ChallengeDto challengeDto, MultipartFile file, Long userNo, boolean isAdmin) {
        Long authorNo = challengeDao.findUserNoById(id);
        if (authorNo == null || (!authorNo.equals(userNo) && !isAdmin)) return Optional.empty();

        challengeDto.setChallengeNo(id);

        if (file != null && !file.isEmpty()) {
            String webPath = "challenge/" + challengeDto.getUserNo();
            String savedFileName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + savedFileName;

            challengeDto.setServerName(serverName);
            challengeDto.setOriginName(file.getOriginalFilename());

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
    public boolean deletePost(Long id, Long userNo, boolean isAdmin) {
        Long authorNo = challengeDao.findUserNoById(id);
        if (authorNo == null || (!authorNo.equals(userNo) && !isAdmin)) return false;
        return challengeDao.updateDeleteStatus(id, "Y") > 0;
    }

    @Transactional
    public void setLikeStatus(Long challengeNo, Long userNo, String likeStatus) {
        if (!"LIKE".equals(likeStatus) && !"DISLIKE".equals(likeStatus) && !"COMMON".equals(likeStatus)) {
            likeStatus = "COMMON";
        }
        String currentStatus = challengeDao.findLikeStatus(userNo, challengeNo);

        if ("COMMON".equals(likeStatus) || likeStatus.equals(currentStatus)) {
            if (currentStatus != null) { 
                challengeDao.deleteLike(userNo, challengeNo);
            }
        } else {
            challengeDao.insertOrUpdateLike(userNo, challengeNo, likeStatus);
        }
    }


    public boolean getLikeStatus(Long challengeNo, Long userNo) {
        String status = challengeDao.findLikeStatus(userNo, challengeNo);
        return "LIKE".equals(status); 
    }

    // 좋아요 개수 조회
    public int getLikesCount(Long challengeNo) {
        Integer count = challengeDao.getLikesCount(challengeNo);
        return count != null ? count : 0;
    }

    // 진행 중 챌린지 조회
    public Optional<List<ChallengeInfoDto>> getActiveChallengeInfo() {
        return Optional.ofNullable(challengeDao.findActiveChallengeInfo());
    }

    public List<ChallengeReplyDto> selectAllRepliesByChallengeId(Long challengeId) {
        List<ChallengeReplyDto> replies = challengeDao.selectAllRepliesByChallengeId(challengeId);

        for (ChallengeReplyDto reply : replies) {
            if (reply.getProfileImageServerName() != null && !reply.getProfileImageServerName().isEmpty()) {

                String fullPath = "profile/" + reply.getUserNo() + "/" + reply.getProfileImageServerName();

                String imageUrl = "/images/" + fullPath;
                reply.setProfileImageServerName(imageUrl);
            }
        }
        return replies;
    }
    
 // 챌린지 등록 가능 기간 확인
    public boolean isRegistrationPeriodValid(Long chInfoNo) {
        ChallengeInfoDto info = challengeDao.findChallengeInfoByNo(chInfoNo);

        Optional<ChallengeInfoDto> infoOptional = Optional.ofNullable(info);

        if (infoOptional.isEmpty()) {
            log.warn("ChallengeInfo not found for chInfoNo: {}", chInfoNo);
            return false;
        }

        ChallengeInfoDto challengeInfo = infoOptional.get();
        LocalDate today = LocalDate.now();

        boolean isValid = (today.isEqual(challengeInfo.getStartDate()) || today.isAfter(challengeInfo.getStartDate())) &&
                          (today.isEqual(challengeInfo.getEndDate()) || today.isBefore(challengeInfo.getEndDate()));

        return isValid;
    }
    
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 초 분 시 일 월 요일
    public void updateExpiredChallengeDeleteStatus() {
        log.info("🗓️ 만료된 챌린지 상태(delete_status) 일괄 업데이트 작업 시작. 기준 날짜: {}", LocalDate.now());

        List<Long> expiredChInfoNos = challengeDao.findExpiredChInfoNos(LocalDate.now());

        if (expiredChInfoNos.isEmpty()) {
            log.info("만료된 챌린지 정보가 없습니다. 상태 변경 작업을 완료합니다.");
            return;
        }
        
        log.info("만료된 챌린지 정보 번호(ch_info_no): {}", expiredChInfoNos);

        int updatedCount = challengeDao.updateChallengesDeleteStatusByInfoNos(expiredChInfoNos, "Y");

        log.info("✅ 총 {}개의 챌린지 정보 번호에 연결된 {}개의 등록 챌린지 상태가 'Y'로 업데이트되었습니다.", 
                 expiredChInfoNos.size(), updatedCount);
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
        if (existing == null || !existing.getUserNo().equals(userNo)) return 0;
        return challengeDao.updateReply(replyDto);
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteReply(Long replyNo, Long userNo) {
        ChallengeReplyDto reply = challengeDao.selectReplyById(replyNo);
        if (reply == null || !reply.getUserNo().equals(userNo)) return false;
        return challengeDao.deleteReply(replyNo, userNo) > 0;
    }

    // 챌린지 신청 등록
    @Transactional
    public int createSuggestion(ChallengeSuggestionDto suggestionDto) {
        return challengeDao.insertSuggestion(suggestionDto);
    }
}