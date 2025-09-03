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

    public List<MyPostDto> findAllPosts() {
        return myPostDao.findAll();
    }

    public Optional<MyPostDto> findPostById(Integer id) {
        Optional<MyPostDto> post = myPostDao.findById(id);
        post.ifPresent(p -> p.setViews(p.getViews() + 1));
        return post;
    }

    public MyPostDto createPost(MyPostDto newPost) {
        return myPostDao.save(newPost);
    }

    public Optional<MyPostDto> updatePost(Integer id, MyPostDto updatedPost) {
        updatedPost.setId(id);
        return myPostDao.update(updatedPost);
    }

    public void deletePost(Integer id) {
        myPostDao.delete(id);
    }
}
