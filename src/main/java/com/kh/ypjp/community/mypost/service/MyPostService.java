package com.kh.ypjp.community.mypost.service;

import com.kh.ypjp.community.mypost.dao.MyPostDao;
import com.kh.ypjp.community.mypost.dto.MyPostDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyPostService {

    private final MyPostDao myPostDao;

    public MyPostService(MyPostDao myPostDao) {
        this.myPostDao = myPostDao;
    }

    // 내 게시물 조회
    public List<MyPostDto> findPostsByUser(Integer userId) {
        return myPostDao.findPostsByUser(userId);
    }

    // 게시글 상세 조회
    public Optional<MyPostDto> findPostDetail(String category, Integer id) {
        // 조회수 증가 (일단 상세조회니까 본인 글도 조회 증가 되어야함...)
        myPostDao.incrementViews(id, category);
        return myPostDao.findPostDetail(id, category);
    }
}
