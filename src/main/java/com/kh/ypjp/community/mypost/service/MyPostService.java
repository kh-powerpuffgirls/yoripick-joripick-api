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

    public List<MyPostDto> findPostsByUser(Integer userId) {
        return myPostDao.findPostsByUser(userId);
    }

    public Optional<MyPostDto> findPostDetail(String category, Integer id) {
        myPostDao.incrementViews(id, category);
        return myPostDao.findPostDetail(id, category);
    }
}
