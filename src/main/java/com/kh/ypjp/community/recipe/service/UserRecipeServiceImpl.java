package com.kh.ypjp.community.recipe.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.ypjp.common.model.vo.Image;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dao.UserRecipeDao;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientInfo;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientJsonDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeWriteRequest;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;
import com.kh.ypjp.community.recipe.model.vo.RcpDetail;
import com.kh.ypjp.community.recipe.model.vo.RcpIngredient;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.model.vo.Recipe;
import com.kh.ypjp.community.recipe.model.vo.RecipeStep;

import jakarta.servlet.ServletContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRecipeServiceImpl implements UserRecipeService {

	private final UserRecipeDao dao;
    private final ServletContext servletContext;
    private final ObjectMapper objectMapper;

    @Override
    public RecipePage selectRecipePage(HashMap<String, Object> params) {
        // 1. 페이지당 보여줄 게시글 수 설정 (예: 8개)
        int pageSize = 12;
        params.put("pageSize", pageSize);
        
        // 2. 전체 게시글 수 조회 (필터링 조건 포함)
        long totalElements = dao.selectRecipeCount(params);
        
        // 3. 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        
        // 4. 현재 페이지의 게시글 목록 조회
        List<UserRecipeResponse> recipes = dao.selectRecipeList(params);
        
        // 5. RecipePage 객체로 포장하여 반환
        return new RecipePage(recipes, totalPages, totalElements);
    }

    @Override
    public List<UserRecipeResponse> selectRankingRecipes() {
        return dao.selectRankingRecipes();
    }

    @Override
    public List<RcpMethod> selectRcpMethods() {
        return dao.selectRcpMethods();
    }

    @Override
    public List<RcpSituation> selectRcpSituations() {
        return dao.selectRcpSituations();
    }

    @Override
    public List<IngredientInfo> searchIngredients(String keyword) {
        return dao.searchIngredients(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRecipe(RecipeWriteRequest request, long userNo) throws Exception {
        // 1. 대표 이미지 저장
        Image mainImage = saveImage(request.getMainImage());
        dao.insertImage(mainImage);
        
        //영양성분 저장
        Nutrient totalNutrient = calculateTotalNutrients(request.getIngredients());
        dao.insertNutrient(totalNutrient);

        // 2. RECIPE 테이블 저장
        Recipe recipe = new Recipe();
        recipe.setUserNo(userNo); // long 타입 userNo 저장
        recipe.setRcpName(request.getRcpName());
        recipe.setRcpInfo(request.getRcpInfo());
        recipe.setTag(request.getTag());
        recipe.setRcpMthNo(request.getRcpMthNo());
        recipe.setRcpStaNo(request.getRcpStaNo());
        recipe.setImageNo(mainImage.getImageNo());
        
        recipe.setNutrientNo(totalNutrient.getNutrientNo()); 
        
        dao.insertRecipe(recipe);
        int rcpNo = recipe.getRcpNo();

        // 3. 재료 정보 저장 (rcp_ingredient)
        List<IngredientJsonDto> ingredients = objectMapper.readValue(request.getIngredients(), new TypeReference<>() {});
        for (IngredientJsonDto dto : ingredients) {
            RcpIngredient ing = new RcpIngredient();
            ing.setRcpNo(rcpNo);
            ing.setIngNo(dto.getIngNo());
            ing.setQuantity(dto.getQuantity());
            ing.setWeight(dto.getWeight());
            dao.insertRcpIngredient(ing);
        }

        // 4. 요리 순서 저장 (rcp_detail)
        List<String> descriptions = request.getStepDescriptions();
        List<MultipartFile> images = request.getStepImages();
        for (int i = 0; i < descriptions.size(); i++) {
            RcpDetail detail = new RcpDetail();
            detail.setRcpNo(rcpNo);
            detail.setRcpOrder(i + 1);
            detail.setDescription(descriptions.get(i));

            MultipartFile stepImgFile = images.get(i);
            if (stepImgFile != null && !stepImgFile.isEmpty()) {
                Image stepImage = saveImage(stepImgFile);
                dao.insertImage(stepImage);
                detail.setImageNo(stepImage.getImageNo());
            }
            dao.insertRcpDetail(detail);
        }
        
    }
    
    private Nutrient calculateTotalNutrients(String ingredientsJson) throws IOException {
        // 1. 프론트에서 보낸 JSON을 IngredientJsonDto 리스트로 변환
        List<IngredientJsonDto> ingredientList = objectMapper.readValue(ingredientsJson, new TypeReference<>() {});
        
        Nutrient total = new Nutrient();
        total.setEnergy(0);
        total.setCarb(0);
        total.setProtein(0);
        total.setFat(0);
        total.setSodium(0);

        // 2. 각 재료에 대해 루프를 돌면서 영양 정보 계산
        for (IngredientJsonDto dto : ingredientList) {
            // 3. DAO를 통해 DB에서 100g 기준 영양 정보를 조회
            Nutrient baseNutrient = dao.findNutrientsByIngNo(dto.getIngNo());
            
            if (baseNutrient != null) {
                // 4. 사용자가 입력한 중량(weight)에 맞춰 영양성분 환산 및 누적
                double ratio = (double) dto.getWeight() / 100.0;
                total.setEnergy(total.getEnergy() + baseNutrient.getEnergy() * ratio);
                total.setCarb(total.getCarb() + baseNutrient.getCarb() * ratio);
                total.setProtein(total.getProtein() + baseNutrient.getProtein() * ratio);
                total.setFat(total.getFat() + baseNutrient.getFat() * ratio);
                total.setSodium(total.getSodium() + baseNutrient.getSodium() * ratio);
            }
        }
        return total;
    }

    /**
     * MultipartFile을 서버에 저장하고 Image VO 객체를 반환하는 private 헬퍼 메서드
     */
    private Image saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String savePath = servletContext.getRealPath("/resources/upload/recipe/");
        File uploadDir = new File(savePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String originName = file.getOriginalFilename();
        String serverName = UUID.randomUUID().toString() + "_" + originName;
        
        file.transferTo(new File(savePath + serverName));

        Image image = new Image();
        image.setOriginName(originName);
        image.setServerName(serverName);
        return image;
    }

   

}