package com.kh.ypjp.community.challenge.dao;

import com.kh.ypjp.community.challenge.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChallengeDao {

    List<ChallengeDto> findAll();
    ChallengeDto findById(Long id);
    int update(ChallengeDto challengeDto);
    int updateDeleteStatus(@Param("challengeNo") Long challengeNo, @Param("deleteStatus") String deleteStatus);
    int incrementViews(Long id);
    int saveChallenge(ChallengeDto challengeDto);

    List<ChallengeInfoDto> findActiveChallengeInfo();
    int checkIfLiked(ChallengeLikesDto likesDto);
    int insertLike(ChallengeLikesDto likesDto);
    int deleteLike(ChallengeLikesDto likesDto);
    int insertImage(Map<String, Object> imageInfo);
    int updateImage(Map<String, Object> imageInfo);
    int updateChallengeImageNo(@Param("challengeNo") Long challengeNo, @Param("imageNo") int imageNo);
    int selectImageNoByChallengeNo(Long challengeNo);
    int insertSuggestion(ChallengeSuggestionDto suggestionDto);

    List<ChallengeReplyDto> selectAllRepliesByChallengeId(@Param("challengeId") Long challengeId);
    int insertReply(ChallengeReplyDto replyDto);
    int updateReply(ChallengeReplyDto replyDto);
    int checkCircularReference(@Param("replyNo") Long replyNo, @Param("newRefNo") Long newRefNo);

    ChallengeReplyDto selectReplyById(@Param("replyNo") Long replyNo);
    // userNo를 매개변수에 추가했습니다.
    int deleteReply(@Param("replyNo") Long replyNo, @Param("userNo") Long userNo);
}