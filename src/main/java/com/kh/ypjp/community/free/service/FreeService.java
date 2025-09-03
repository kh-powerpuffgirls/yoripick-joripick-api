package com.kh.ypjp.community.free.service;

import com.kh.ypjp.community.free.dao.FreeDao;
import com.kh.ypjp.community.free.dto.FreeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 자유게시판 서비스 클래스입니다.
 * 비즈니스 로직을 담당하며, FreeDao를 통해 게시글 데이터를 처리합니다.
 */
@Service
public class FreeService {

    private final FreeDao freeDao;

    @Autowired
    public FreeService(FreeDao freeDao) {
        this.freeDao = freeDao;
    }

    /**
     * 모든 게시글을 조회합니다.
     * @return 게시글 목록
     */
    public List<FreeDto> findAllPosts() {
        return freeDao.findAll();
    }

    /**
     * 특정 ID의 게시글을 조회하고 조회수를 1 증가시킵니다.
     * @param id 게시글 ID
     * @return 조회된 게시글 (존재하지 않으면 Optional.empty())
     */
    public Optional<FreeDto> findPostById(Integer id) {
        Optional<FreeDto> post = freeDao.findById(id);
        post.ifPresent(p -> p.setViews(p.getViews() + 1));
        return post;
    }

    /**
     * 새로운 게시글을 생성합니다.
     * @param newPost 새로운 게시글 정보
     * @return 생성된 게시글
     */
    public FreeDto createPost(FreeDto newPost) {
        return freeDao.save(newPost);
    }

    /**
     * 기존 게시글을 수정합니다.
     * @param id 수정할 게시글 ID
     * @param updatedPost 수정된 게시글 정보
     * @return 수정된 게시글 (수정 실패 시 Optional.empty())
     */
    public Optional<FreeDto> updatePost(Integer id, FreeDto updatedPost) {
        updatedPost.setId(id);
        return freeDao.update(updatedPost);
    }

    /**
     * 특정 ID의 게시글을 삭제합니다.
     * @param id 삭제할 게시글 ID
     */
    public void deletePost(Integer id) {
        freeDao.delete(id);
    }
}
