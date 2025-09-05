package com.kh.ypjp.community.challenge.dao;

import com.kh.ypjp.community.challenge.dto.ChallengeDto;
import com.kh.ypjp.community.challenge.dto.ChallengeReplyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChallengeDao {

    // Retrieves all active user challenge posts (filtered by date)
    List<ChallengeDto> findAll();

    // Retrieves a specific user challenge post
    ChallengeDto findById(Long id);

    // Updates a user challenge post
    int update(ChallengeDto dto);

    // Deletes a user challenge post
    int delete(Long id);

    // Increments view count
    void incrementViews(Long id);

    // Checks if a user has liked a post
    int checkIfLiked(@Param("userId") Long userId, @Param("challengeId") Long challengeId);

    // Inserts a like record
    int insertLike(@Param("userId") Long userId, @Param("challengeId") Long challengeId);

    // Deletes a like record
    int deleteLike(@Param("userId") Long userId, @Param("challengeId") Long challengeId);

    // Retrieves the single currently active challenge (based on date)
    ChallengeDto findActiveChallengeInfo();

    // Saves a new challenge info record (for admins)
    int saveChallengeInfo(ChallengeDto dto);

    // Saves a new user challenge post
    int saveChallenge(@Param("dto") ChallengeDto dto);

    // Deletes expired challenge info and user posts
    int deleteExpiredChallenges();

    // Retrieves all replies for a challenge post
    List<ChallengeReplyDto> selectRepliesByRefNo(Long refNo);

    // Inserts a new reply
    int insertReply(ChallengeReplyDto replyDto);
    
    // Deletes an image record
    int deleteImage(@Param("imageNo") Integer imageNo);
}