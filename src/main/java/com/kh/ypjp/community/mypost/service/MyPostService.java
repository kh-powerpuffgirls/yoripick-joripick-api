package com.kh.ypjp.community.mypost.service;

import com.kh.ypjp.community.mypost.dao.MyPostDao;
import com.kh.ypjp.community.mypost.dto.MyPostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyPostService {

    private final MyPostDao myPostDao;

    @Autowired
    public MyPostService(MyPostDao myPostDao) {
        this.myPostDao = myPostDao;
    }

    // 내 게시물 조회
    public List<MyPostDto> findPostsByUser(Integer userId) {
        return myPostDao.findPostsByUser(userId);
    }

    // 게시글 상세 조회
    public Optional<MyPostDto> findPostDetail(String category, Integer id) {
        // 조회수 증가
        myPostDao.incrementViews(id, category);
        return myPostDao.findPostDetail(id, category);
    }
}
