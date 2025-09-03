package com.kh.ypjp.community.challenge.service;

import com.kh.ypjp.community.challenge.dao.ChallengeDao;
import com.kh.ypjp.community.challenge.dto.ChallengeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeDao challengeDao;

    public List<ChallengeDto> getAllPosts() {
        return challengeDao.findAll();
    }

    public Optional<ChallengeDto> getPost(Long id) {
        return challengeDao.findById(id);
    }

    public ChallengeDto createPost(ChallengeDto challengeDto) {
        return challengeDao.save(challengeDto);
    }

    public Optional<ChallengeDto> updatePost(Long id, ChallengeDto challengeDto) {
        if (challengeDao.findById(id).isPresent()) {
            challengeDao.update(id, challengeDto);
            return challengeDao.findById(id);
        }
        return Optional.empty();
    }

    public boolean deletePost(Long id) {
        if (challengeDao.findById(id).isPresent()) {
            challengeDao.delete(id);
            return true;
        }
        return false;
    }

    public void incrementViews(Long id) {
        challengeDao.incrementViews(id);
    }

    public void incrementLikes(Long id) {
        challengeDao.incrementLikes(id);
    }
}