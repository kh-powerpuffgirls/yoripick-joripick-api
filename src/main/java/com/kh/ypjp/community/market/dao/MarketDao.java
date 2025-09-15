package com.kh.ypjp.community.market.dao;

import com.kh.ypjp.community.market.dto.MarketDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MarketDao {

    private static final List<MarketDto> marketPosts = new ArrayList<>();
    private static long postIdCounter = 0;

    // 임시 데이터
    static {
        marketPosts.add(MarketDto.builder()
                .id(++postIdCounter)
                .title("강원도 감자 팝니다!")
                .author("망곰eee")
                .authorProfileUrl("profile_url_1")
                .postDate(LocalDate.now())
                .imageUrl("https://placehold.co/200x200/ffe6b7/000000?text=감자")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusWeeks(1))
                .alwaysOnSale(false)
                .description("직접 강원도에서 가지고 온 감자입니다.")
                .itemName("강원도 감자")
                .price(5000)
                .stock(100)
                .contactNumber("010-1234-5678")
                .bankAccount("123-456-7890")
                .views(100)
                .likes(50)
                .build());
        // 추가 데이터는 필요에 따라 더 넣을 수 있습니다.
    }

    public MarketDto save(MarketDto post) {
        if (post.getId() == null) {
            post.setId(++postIdCounter);
        }
        marketPosts.add(post);
        return post;
    }

    public List<MarketDto> findAll() {
        return new ArrayList<>(marketPosts);
    }
    
    public Optional<MarketDto> findById(Long id) {
        return marketPosts.stream().filter(post -> post.getId().equals(id)).findFirst();
    }
}