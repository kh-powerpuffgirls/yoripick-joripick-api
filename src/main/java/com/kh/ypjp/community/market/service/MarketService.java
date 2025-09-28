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

    private void setProfileInfoForPosts(List<MarketSellDto> posts) {
    	if (posts == null) return;
        
        for (MarketSellDto post : posts) {
            if (post.getUserNo() != null) {
                
                String profileFileName = marketDao.selectProfileFileNameByUserNo(post.getUserNo()); 
                
                if (profileFileName != null && !profileFileName.isEmpty()) {
                    String fullPath = "/images/profile/" + post.getUserNo() + "/" + profileFileName;
                    post.setAuthorProfileUrl(fullPath); 
                }
            }
        }
    }
    
    public List<MarketSellDto> getAllPosts() {
        List<MarketSellDto> posts = marketDao.getAllPosts();
        setProfileInfoForPosts(posts); 
        return posts;
    }
    
    public List<MarketSellDto> getPopularPosts() {
        List<MarketSellDto> posts = marketDao.getPopularPosts();
        setProfileInfoForPosts(posts); 
        return posts;
    }
    
    public List<MarketSellDto> getRecentPosts() {
        List<MarketSellDto> posts = marketDao.getRecentPosts();
        setProfileInfoForPosts(posts); 
        return posts;
    }
    
    public List<MarketSellDto> getMyPostsWithForms(Long userNo) {
        return marketDao.findMyPostsWithForms(userNo);
    }
    
    public Optional<MarketSellDto> getPost(Long productId) {
        MarketSellDto post = marketDao.getPostDetail(productId);

        if (post != null) {
            String sikBti = marketDao.selectSikBtiByUserNo(post.getUserNo().intValue());
            post.setSikBti(sikBti);
            
            if (post.getUserNo() != null) {
                 String profileFileName = marketDao.selectProfileFileNameByUserNo(post.getUserNo());
                 if (profileFileName != null && !profileFileName.isEmpty()) {
                    String fullPath = "/images/profile/" + post.getUserNo() + "/" + profileFileName;
                    post.setAuthorProfileUrl(fullPath);
                 }
            }
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
    
    public Optional<MarketBuyDto> getSellBuyFormById(Long formId) {
        MarketBuyDto buyForm = marketDao.findPurchaseForm(formId);
        return Optional.ofNullable(buyForm);
    }


    @Transactional
    public boolean deleteBuyForm(Long formId, Long userNo) {
        Long sellerId = marketDao.findSellerByFormId(formId);

        if (sellerId != null && sellerId.equals(userNo)) {
            int rowsAffected = marketDao.updateBuyFormDeleteStatus(formId, "Y");
            return rowsAffected > 0;
        }
        
        log.warn("구매 폼 삭제 권한 없음: formId={}, userNo={}", formId, userNo);
        return false;
    }
}