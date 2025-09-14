package com.kh.ypjp.community.challenge.dao;

import com.kh.ypjp.community.challenge.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChallengeDao {

    List<ChallengeDto> findAll();
    ChallengeDto findById(@Param("challengeNo") Long challengeNo);
    int saveChallenge(ChallengeDto challengeDto);
    int update(ChallengeDto challengeDto);
    int updateDeleteStatus(@Param("challengeNo") Long challengeNo,
                           @Param("deleteStatus") String deleteStatus);
    int incrementViews(@Param("challengeNo") Long challengeNo);
    
    List<ChallengeInfoDto> findActiveChallengeInfo();
    List<ChallengeReplyDto> selectAllRepliesByChallengeId(@Param("challengeNo") Long challengeNo);
    
    int insertReply(ChallengeReplyDto replyDto);
    int updateReply(ChallengeReplyDto replyDto);
    ChallengeReplyDto selectReplyById(@Param("replyNo") Long replyNo);
    int deleteReply(@Param("replyNo") Long replyNo, @Param("userNo") Long userNo);

    int insertSuggestion(ChallengeSuggestionDto suggestionDto);

    int checkIfLiked(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    void insertLike(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    void deleteLike(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    int getLikesCount(@Param("challengeNo") Long challengeNo);
}
