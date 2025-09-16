package com.kh.ypjp.community.market.service;

import com.kh.ypjp.community.market.dao.MarketDao;
import com.kh.ypjp.community.market.dto.MarketDto; // import 수정
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketDao marketDao; // 변수 이름 수정

    public MarketDto createMarketPost(MarketDto postDTO) {
        // 비즈니스 로직 추가 가능 (예: 데이터 유효성 검사)
        return marketDao.save(postDTO);
    }

    public List<MarketDto> getPopularPosts() {
        // 조회수/좋아요 기반으로 인기 글을 가져오는 로직
        return marketDao.findAll().stream()
                .sorted(Comparator.comparing(MarketDto::getViews).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<MarketDto> getRecentPosts() {
        // 최신순으로 글을 가져오는 로직
        return marketDao.findAll().stream()
                .sorted(Comparator.comparing(MarketDto::getPostDate).reversed())
                .collect(Collectors.toList());
    }
    
    public MarketDto getPostById(Long id) {
        return marketDao.findById(id).orElse(null);
    }
}