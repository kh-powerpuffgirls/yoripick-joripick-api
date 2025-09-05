package com.kh.ypjp.community.challenge.service;

import com.kh.ypjp.community.challenge.dao.ChallengeDao;
import com.kh.ypjp.community.challenge.dto.ChallengeDto;
import com.kh.ypjp.community.challenge.dto.ChallengeReplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeDao challengeDao;
    
    // Retrieves all active user challenge posts
    public List<ChallengeDto> getAllPosts() {
        return challengeDao.findAll();
    }

    // Retrieves a specific user challenge post
    public Optional<ChallengeDto> getPost(Long id) {
        return Optional.ofNullable(challengeDao.findById(id));
    }

    // Creates a new user challenge post
    @Transactional
    public ChallengeDto createPost(ChallengeDto challengeDto) {
        challengeDao.saveChallenge(challengeDto);
        return challengeDao.findById(challengeDto.getChallengeNo());
    }

    // Updates a user challenge post
    @Transactional
    public Optional<ChallengeDto> updatePost(Long id, ChallengeDto challengeDto, Long currentUserId) {
        ChallengeDto existingPost = challengeDao.findById(id);

        if (existingPost != null && existingPost.getUserNo().equals(currentUserId)) {
            challengeDto.setChallengeNo(id);
            challengeDao.update(challengeDto);
            return Optional.ofNullable(challengeDao.findById(id));
        }
        return Optional.empty();
    }

    // Deletes a user challenge post
    @Transactional
    public boolean deletePost(Long id, Long currentUserId) {
        ChallengeDto existingPost = challengeDao.findById(id);

        if (existingPost != null && existingPost.getUserNo().equals(currentUserId)) {
            return challengeDao.delete(id) > 0;
        }
        return false;
    }

    // Increments view count
    @Transactional
    public void incrementViews(Long id) {
        challengeDao.incrementViews(id);
    }
    
    // Toggles like status (like or unlike)
    @Transactional
    public void toggleLike(Long userId, Long challengeId) {
        int isLiked = challengeDao.checkIfLiked(userId, challengeId);
        
        if (isLiked > 0) {
            challengeDao.deleteLike(userId, challengeId);
        } else {
            challengeDao.insertLike(userId, challengeId);
        }
    }
    
    // Checks if a user has liked a post
    public boolean checkIfLiked(Long userId, Long challengeId) {
        return challengeDao.checkIfLiked(userId, challengeId) > 0;
    }

    // Retrieves the single currently active challenge info
    public Optional<ChallengeDto> getActiveChallengeInfo() {
        return Optional.ofNullable(challengeDao.findActiveChallengeInfo());
    }

    // Retrieves all replies for a challenge post
    public List<ChallengeReplyDto> getReplies(Long challengeNo) {
        return challengeDao.selectRepliesByRefNo(challengeNo);
    }

    // Creates a new reply
    @Transactional
    public int createReply(ChallengeReplyDto replyDto) {
        return challengeDao.insertReply(replyDto);
    }
    
    // Scheduled task to delete expired challenges
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at 00:00 (midnight)
    @Transactional
    public void cleanupExpiredChallenges() {
        int deletedCount = challengeDao.deleteExpiredChallenges();
        System.out.println("Deleted " + deletedCount + " expired challenges and related posts.");
    }
}