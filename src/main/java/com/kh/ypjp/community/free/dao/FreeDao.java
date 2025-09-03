package com.kh.ypjp.community.free.dao;

import com.kh.ypjp.community.free.dto.FreeDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 게시글 데이터를 관리하는 DAO 클래스입니다.
 * 현재는 메모리 내에서 데이터를 저장하고 처리합니다.
 */
@Repository
public class FreeDao {
    private final List<FreeDto> posts = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger();

    public FreeDao() {
        // 초기 데이터 추가
        posts.add(new FreeDto(counter.incrementAndGet(), "첫 번째 게시글", "첫 번째 게시글 내용입니다.", LocalDateTime.now(), 0));
        posts.add(new FreeDto(counter.incrementAndGet(), "두 번째 게시글", "두 번째 게시글 내용입니다.", LocalDateTime.now(), 0));
        posts.add(new FreeDto(counter.incrementAndGet(), "세 번째 게시글", "세 번째 게시글 내용입니다.", LocalDateTime.now(), 0));
    }

    /**
     * 모든 게시글을 최신 순으로 정렬하여 반환합니다.
     * @return 정렬된 게시글 목록
     */
    public List<FreeDto> findAll() {
        return posts.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getId(), p1.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID의 게시글을 찾아 반환합니다.
     * @param id 게시글 ID
     * @return ID에 해당하는 게시글(존재하지 않으면 Optional.empty())
     */
    public Optional<FreeDto> findById(Integer id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }

    /**
     * 새로운 게시글을 저장합니다. ID와 생성 날짜를 자동으로 할당합니다.
     * @param newPost 저장할 게시글 정보
     * @return 저장된 게시글
     */
    public FreeDto save(FreeDto newPost) {
        newPost.setId(counter.incrementAndGet());
        newPost.setCreatedDate(LocalDateTime.now());
        posts.add(newPost);
        return newPost;
    }

    /**
     * 기존 게시글을 업데이트합니다.
     * @param updatedPost 업데이트된 게시글 정보
     * @return 업데이트된 게시글(업데이트 실패 시 Optional.empty())
     */
    public Optional<FreeDto> update(FreeDto updatedPost) {
        return findById(updatedPost.getId())
                .map(existingPost -> {
                    existingPost.setTitle(updatedPost.getTitle());
                    existingPost.setDescription(updatedPost.getDescription());
                    return existingPost;
                });
    }

    /**
     * 특정 ID의 게시글을 삭제합니다.
     * @param id 삭제할 게시글 ID
     */
    public void delete(Integer id) {
        posts.removeIf(post -> post.getId().equals(id));
    }
}
