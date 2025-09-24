package com.kh.ypjp.community.challenge.dao;

import com.kh.ypjp.community.challenge.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChallengeDao {

    List<ChallengeDto> findAll();
    
    ChallengeDto findByIdWithImage(@Param("challengeNo") Long challengeNo);
    
    Long findUserNoById(@Param("challengeNo") Long challengeNo);

    int saveChallenge(ChallengeDto challengeDto);
    int update(ChallengeDto challengeDto);
    int updateDeleteStatus(@Param("challengeNo") Long challengeNo,
                           @Param("deleteStatus") String deleteStatus);
    int incrementViews(@Param("challengeNo") Long challengeNo);
    
    List<ChallengeInfoDto> findActiveChallengeInfo();
    List<ChallengeReplyDto> selectAllRepliesByChallengeId(@Param("challengeNo") Long challengeNo);
    String findLikeStatus(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);

    int insertReply(ChallengeReplyDto replyDto);
    int updateReply(ChallengeReplyDto replyDto);
    ChallengeReplyDto selectReplyById(@Param("replyNo") Long replyNo);
    int deleteReply(@Param("replyNo") Long replyNo, @Param("userNo") Long userNo);

    int insertSuggestion(ChallengeSuggestionDto suggestionDto);

    int checkIfLiked(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    void insertOrUpdateLike(@Param("userNo") Long userNo,
            @Param("challengeNo") Long challengeNo,
            @Param("likeStatus") String likeStatus);
    void deleteLike(@Param("userNo") Long userNo, @Param("challengeNo") Long challengeNo);
    int getLikesCount(@Param("challengeNo") Long challengeNo);
    
    Long findNextChallenge(@Param("challengeNo") Long challengeNo);
    Long findPreviousChallenge(@Param("challengeNo") Long challengeNo);
}
