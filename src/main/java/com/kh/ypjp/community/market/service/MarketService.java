package com.kh.ypjp.community.market.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.market.dao.MarketDao;
import com.kh.ypjp.community.market.dto.MarketDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketService {

    private final MarketDao marketDao;
    private final UtilService utilService;

    // 모든 마켓 게시글 조회 (삭제되지 않은 게시글만 조회)
    public List<MarketDto> getAllPosts() {
        return marketDao.getRecentPosts();
    }
    
    public List<MarketDto> getPopularPosts() {
        return marketDao.getPopularPosts();
    }
    
    // 최신 게시글 조회 메서드 추가
    public List<MarketDto> getRecentPosts() {
        return marketDao.getRecentPosts();
    }

    // 게시글 상세 조회 및 조회수 증가
    @Transactional
    public Optional<MarketDto> getPostAndIncrementViews(Long productId, Long userNo) {
        MarketDto post = marketDao.getPostDetail(productId);

        if (post == null) {
            return Optional.empty();
        }
        
        // 작성자 본인이 아니면 조회수 증가
        if (userNo == null || !userNo.equals(post.getUserNo())) {
            marketDao.incrementViews(productId);
            // DTO에 조회수 반영 (클라이언트에게 최신 정보 전달)
            // post.setViews(post.getViews() + 1); // DTO에 views가 있다면 사용
        }

        return Optional.of(post);
    }

    @Transactional
    public void registerPost(MarketDto marketDto, MultipartFile image) throws Exception {
        if (marketDto.getUserNo() == null) {
            throw new IllegalArgumentException("로그인 후 이용할 수 있습니다.");
        }
        
        // 이미지 업로드 로직을 개선하여 IMAGE_NO를 먼저 받아옴
        if (image != null && !image.isEmpty()) {
            String webPath = "market/" + marketDto.getUserNo();
            String savedFileName = utilService.getChangeName(image, webPath);
            String serverName = webPath + "/" + savedFileName;

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", image.getOriginalFilename());
            
            // 1. IMAGE 테이블에 이미지 정보를 먼저 저장하고,
            utilService.insertImage(param);
            
            // 2. 저장된 이미지의 고유 번호(imageNo)를 가져와 DTO에 설정합니다.
            Long imageNo = utilService.getImageNo(param);
            
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(serverName)
                .toUriString();
            
            marketDto.setImageUrl(imageUrl);
            marketDto.setImageNo(imageNo.intValue()); // Long을 int로 변환하여 할당
        }
        
        marketDao.registerPost(marketDto);
    }
    
    @Transactional
    public Optional<MarketDto> updatePost(Long productId, MarketDto marketDto, MultipartFile image, Long userNo, boolean isAdmin) throws Exception {
        Long authorNo = marketDao.findUserNoById(productId);

        if (authorNo == null || (!authorNo.equals(userNo) && !isAdmin)) {
            log.warn("게시글 수정 권한 없음: productId={}, userNo={}", productId, userNo);
            return Optional.empty();
        }

        marketDto.setProductId(productId);

        if (image != null && !image.isEmpty()) {
            // 새 이미지로 업데이트
            String webPath = "market/" + userNo;
            String savedFileName = utilService.getChangeName(image, webPath);
            String serverName = webPath + "/" + savedFileName;

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", image.getOriginalFilename());
            
            utilService.insertImage(param);
            Long imageNo = utilService.getImageNo(param); // Long 타입으로 변경

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(serverName)
                .toUriString();

            marketDto.setImageUrl(imageUrl);
            marketDto.setImageNo(imageNo.intValue());
        }
        
        marketDao.updatePost(marketDto);
        return Optional.of(marketDao.getPostDetail(productId));
    }

    // 게시글 삭제 (소프트 삭제)
    @Transactional
    public boolean deletePost(Long productId, Long userNo, boolean isAdmin) {
        Long authorNo = marketDao.findUserNoById(productId);

        if (authorNo == null || (!authorNo.equals(userNo) && !isAdmin)) {
            log.warn("게시글 삭제 권한 없음: productId={}, userNo={}", productId, userNo);
            return false;
        }

        return marketDao.updateDeleteStatus(productId, "Y") > 0;
    }

    @Transactional
    public void registerPurchaseForm(MarketDto marketDto) {
        marketDao.registerPurchaseForm(marketDto);
        marketDao.decreaseQuantity(marketDto.getProductId(), marketDto.getCount());
    }

    public boolean checkQuantity(Long productId, int count) {
        int currentQuantity = marketDao.getProductQuantity(productId);
        return currentQuantity >= count;
    }
}