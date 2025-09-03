package com.kh.ypjp.community.mypost.dao;

import com.kh.ypjp.community.mypost.dto.MyPostDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class MyPostDao {
    private final List<MyPostDto> posts = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger();

    public MyPostDao() {
        // 초기 데이터 추가
        posts.add(new MyPostDto(counter.incrementAndGet(), "첫 번째 게시글", "첫 번째 게시글 내용입니다.", LocalDateTime.now(), 0));
        posts.add(new MyPostDto(counter.incrementAndGet(), "두 번째 게시글", "두 번째 게시글 내용입니다.", LocalDateTime.now(), 0));
        posts.add(new MyPostDto(counter.incrementAndGet(), "세 번째 게시글", "세 번째 게시글 내용입니다.", LocalDateTime.now(), 0));
    }

    public List<MyPostDto> findAll() {
        return posts.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getId(), p1.getId()))
                .collect(Collectors.toList());
    }

    public Optional<MyPostDto> findById(Integer id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }

    public MyPostDto save(MyPostDto newPost) {
        newPost.setId(counter.incrementAndGet());
        newPost.setCreatedDate(LocalDateTime.now());
        posts.add(newPost);
        return newPost;
    }

    public Optional<MyPostDto> update(MyPostDto updatedPost) {
        return findById(updatedPost.getId())
                .map(existingPost -> {
                    existingPost.setTitle(updatedPost.getTitle());
                    existingPost.setDescription(updatedPost.getDescription());
                    return existingPost;
                });
    }

    public void delete(Integer id) {
        posts.removeIf(post -> post.getId().equals(id));
    }
}
