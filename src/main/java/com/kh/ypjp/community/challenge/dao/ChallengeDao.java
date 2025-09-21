package com.kh.ypjp.community.challenge.dao;

import com.kh.ypjp.community.challenge.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChallengeDao {

    List<ChallengeDto> findAll();
    
    ChallengeDto findByIdWithImage(@Param("challengeNo") Long challengeNo);
    
    public Long findUserNoById(@Param("challengeNo") Long challengeNo);

    int saveChallenge(ChallengeDto challengeDto);
    int update(ChallengeDto challengeDto);
    int updateDeleteStatus(@Param("challengeNo") Long challengeNo,
                           @Param("deleteStatus") String deleteStatus);
    int incrementViews(@Param("challengeNo") Long challengeNo);
    
    List<ChallengeInfoDto> findActiveChallengeInfo();
    List<ChallengeReplyDto> selectAllRepliesByChallengeId(@Param("challengeNo") Long challengeNo);
    
    
 // 신고 등록
    int insertReport(ChallengeReportDto reportDto);

    // 신고 전체 조회
    List<ChallengeReportDto> selectAllReports();

    
    int insertReply(ChallengeReplyDto replyDto);
    int updateReply(ChallengeReplyDto replyDto);
    ChallengeReplyDto selectReplyById(@Param("replyNo") Long replyNo);
    int deleteReply(@Param("replyNo") Long replyNo, @Param("userNo") Long userNo);

    int insertSuggestion(ChallengeSuggestionDto suggestionDto);

    int checkIfLiked(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    void insertLike(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    void deleteLike(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    int getLikesCount(@Param("challengeNo") Long challengeNo);
    
    // 다음 게시글 번호 찾기 (현재 번호보다 크면서 가장 작은 번호)
    Long findNextChallenge(@Param("challengeNo") Long challengeNo);

    // 이전 게시글 번호 찾기 (현재 번호보다 작으면서 가장 큰 번호)
    Long findPreviousChallenge(@Param("challengeNo") Long challengeNo);
}