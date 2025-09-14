package com.kh.ypjp.community.mypost.dao;

import com.kh.ypjp.community.mypost.dto.MyPostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MyPostDao {
    List<MyPostDto> findPostsByUser(@Param("userId") Integer userId);

    Optional<MyPostDto> findPostDetail(@Param("id") Integer id, @Param("category") String category);

    void incrementViews(@Param("id") Integer id, @Param("category") String category);
}
