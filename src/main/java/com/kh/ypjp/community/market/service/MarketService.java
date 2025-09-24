package com.kh.ypjp.community.market.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.market.dao.MarketDao;
import com.kh.ypjp.community.market.dto.MarketBuyDto;
import com.kh.ypjp.community.market.dto.MarketSellDto;
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

    public List<MarketSellDto> getAllPosts() {
        return marketDao.getAllPosts();
    }
    
    public List<MarketSellDto> getPopularPosts() {
        return marketDao.getPopularPosts();
    }
    
    public List<MarketSellDto> getRecentPosts() {
        return marketDao.getRecentPosts();
    }
    
    public List<MarketSellDto> getMyPostsWithForms(Long userNo) {

        return marketDao.findMyPostsWithForms(userNo);
    }
    
    public Optional<MarketSellDto> getPost(Long productId) {
        MarketSellDto post = marketDao.getPostDetail(productId);
        
        if (post != null) {
            String sikBti = marketDao.selectSikBtiByUserNo(post.getUserNo().intValue());
            post.setSikBti(sikBti);
        }
        
        return Optional.ofNullable(post);
    }

    @Transactional
    public void incrementViews(Long productId) {
        marketDao.incrementViews(productId);
    }
    
    @Transactional
    public Long registerPost(MarketSellDto marketSellDto, MultipartFile image) throws Exception {
        if (marketSellDto.getUserNo() == null) {
            throw new IllegalArgumentException("로그인 후 이용할 수 있습니다.");
        }
        
        Long imageNo = null;

        if (image != null && !image.isEmpty()) {
            String webPath = "market/" + marketSellDto.getUserNo();
            String savedFileName = utilService.getChangeName(image, webPath);
            String serverName = webPath + "/" + savedFileName;

            log.info("이미지 저장 경로: {}", serverName);
            
            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", image.getOriginalFilename());
            
            utilService.insertImage(param);
            imageNo = utilService.getImageNo(param);
            
            log.info("DB에 저장된 imageNo: {}", imageNo);
            
            marketSellDto.setImageNo(imageNo.intValue());
            marketSellDto.setServerName(serverName);
            marketSellDto.setOriginName(image.getOriginalFilename());
        }
        
        marketDao.registerPost(marketSellDto);

        return marketSellDto.getProductId();
    }
    
    @Transactional
    public Optional<MarketSellDto> updatePost(Long productId, MarketSellDto marketSellDto, MultipartFile image, Long userNo, boolean isAdmin) throws Exception {
        Long authorNo = marketDao.findUserNoById(productId);

        if (authorNo == null || (!authorNo.equals(userNo) && !isAdmin)) {
            log.warn("게시글 수정 권한 없음: productId={}, userNo={}", productId, userNo);
            return Optional.empty();
        }

        marketSellDto.setProductId(productId);

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

            marketSellDto.setServerName(serverName);
            marketSellDto.setOriginName(image.getOriginalFilename());
            marketSellDto.setImageNo(imageNo.intValue());
        }
        
        marketDao.updatePost(marketSellDto);
        return Optional.of(marketDao.getPostDetail(productId));
    }

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
    public void registerPurchaseForm(MarketBuyDto marketBuyDto) {
        marketDao.registerPurchaseForm(marketBuyDto);
        marketDao.decreaseQuantity(marketBuyDto.getProductId(), marketBuyDto.getCount());
    }

    public boolean checkQuantity(Long productId, int count) {
        int currentQuantity = marketDao.getProductQuantity(productId);
        return currentQuantity >= count;
    }
    
    // 판매자용 구매 신청 폼 상세 조회 메서드
    public Optional<MarketBuyDto> getSellBuyFormById(Long formId, Long userNo) {
        // 폼과 연결된 상품의 판매자가 현재 로그인한 사용자인지 확인
        Long sellerId = marketDao.findSellerByFormId(formId);

        if (sellerId != null && sellerId.equals(userNo)) {
            // 판매자가 맞으면 폼 정보 조회
            MarketBuyDto buyForm = marketDao.findPurchaseForm(formId);
            return Optional.ofNullable(buyForm);
        }
        // 판매자가 아니거나 폼을 찾을 수 없으면 Optional.empty() 반환
        return Optional.empty();
    }

    // 🔥 구매 폼 삭제 메서드
    @Transactional
    public boolean deleteBuyForm(Long formId, Long userNo) {
        // 폼과 연결된 상품의 판매자가 현재 로그인한 사용자인지 확인
        Long sellerId = marketDao.findSellerByFormId(formId);

        if (sellerId != null && sellerId.equals(userNo)) {
            // 판매자가 맞으면 삭제 상태로 업데이트
            int rowsAffected = marketDao.updateBuyFormDeleteStatus(formId, "Y");
            return rowsAffected > 0;
        }
        
        // 판매자가 아니면 권한 없음 로그를 남기고 실패 반환
        log.warn("구매 폼 삭제 권한 없음: formId={}, userNo={}", formId, userNo);
        return false;
    }
}