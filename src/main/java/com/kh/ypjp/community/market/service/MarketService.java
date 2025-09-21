package com.kh.ypjp.community.market.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.market.dao.MarketDao;
import com.kh.ypjp.community.market.dto.MarketDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    
 // 내 판매 목록 조회
    public List<MarketDto> getMyPosts(int userNo) {
        return marketDao.getMyPosts(userNo);
    }
    
    /**
     * 게시글 상세 조회
     * 조회수 증가 로직은 컨트롤러에서 처리합니다.
     * @param productId 조회할 게시글 ID
     * @return 조회된 게시글 DTO
     */
    public Optional<MarketDto> getPost(Long productId) {
        MarketDto post = marketDao.getPostDetail(productId);
        
        if (post != null) {
            // 게시글 작성자의 식BTI를 조회하여 DTO에 설정합니다.
            String sikBti = marketDao.selectSikBtiByUserNo(post.getUserNo().intValue());
            post.setSikBti(sikBti);
        }
        
        return Optional.ofNullable(post);
    }
    
    /**
     * 게시글 조회수 증가
     * @param productId 조회수를 증가시킬 게시글 ID
     */
    @Transactional
    public void incrementViews(Long productId) {
        marketDao.incrementViews(productId);
    }
    
    // 게시글 등록
    @Transactional
    public Long registerPost(MarketDto marketDto, MultipartFile image) throws Exception {
        if (marketDto.getUserNo() == null) {
            throw new IllegalArgumentException("로그인 후 이용할 수 있습니다.");
        }
        
        Long imageNo = null;

        if (image != null && !image.isEmpty()) {
            String webPath = "market/" + marketDto.getUserNo();
            String savedFileName = utilService.getChangeName(image, webPath);
            String serverName = webPath + "/" + savedFileName;

            log.info("이미지 저장 경로: {}", serverName);
            
            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", image.getOriginalFilename());
            
            utilService.insertImage(param);
            imageNo = utilService.getImageNo(param);
            
            log.info("DB에 저장된 imageNo: {}", imageNo);
            
            marketDto.setImageNo(imageNo.intValue());
            marketDto.setServerName(serverName);
            marketDto.setOriginName(image.getOriginalFilename());
        }
        
        marketDao.registerPost(marketDto);

        return marketDto.getProductId();
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
            Long imageNo = utilService.getImageNo(param);

            marketDto.setServerName(serverName);
            marketDto.setOriginName(image.getOriginalFilename());
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