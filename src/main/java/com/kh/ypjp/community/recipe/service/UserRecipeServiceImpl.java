package com.kh.ypjp.community.recipe.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.ypjp.common.model.vo.Image;
import com.kh.ypjp.common.model.vo.Nutrient;
import com.kh.ypjp.community.recipe.dao.UserRecipeDao;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.IngredientInfo;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeWriteRequest;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.UserRecipeResponse;
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
    private final ServletContext servletContext; // 파일 저장을 위한 의존성 주입
    
    @Override
    public List<UserRecipeResponse> selectRecipe(HashMap<String, Object> param) {
        // DAO에게 레시피 목록 조회를 요청하고 결과를 그대로 반환합니다.
        return dao.selectRecipe(param);
    }

    @Override
    public List<RcpMethod> selectRcpMethods() {
        // DAO에게 요리 방법 목록 조회를 요청하고 결과를 그대로 반환합니다.
        return dao.selectRcpMethods();
    }

    @Override
    public List<RcpSituation> selectRcpSituations() {
        // DAO에게 요리 종류 목록 조회를 요청하고 결과를 그대로 반환합니다.
        return dao.selectRcpSituations();
    }

    @Override
    public List<IngredientInfo> searchIngredients(String keyword) {
        // DAO에게 재료 검색을 요청하고 결과를 그대로 반환합니다.
        return dao.searchIngredients(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 모든 종류의 예외 발생 시 롤백
    public void createRecipe(RecipeWriteRequest request, long userNo) throws Exception {

        // 1. 대표 이미지 저장
        Image mainImageVo = saveImage(request.getMainImage());
        dao.insertImage(mainImageVo);

        // 2. 총 영양성분 계산 및 NUTRIENT 테이블에 저장
        Nutrient totalNutrient = calculateTotalNutrients(request.getIngredients());
        dao.insertNutrient(totalNutrient);

        // 3. RECIPE 테이블에 저장할 데이터 준비
        Recipe recipe = new Recipe();
        recipe.setUserNo(userNo);
        recipe.setRcpName(request.getRcpName());
        recipe.setRcpInfo(request.getRcpInfo());
        recipe.setRcpMthNo(request.getRcpMthNo());
        recipe.setRcpStaNo(request.getRcpStaNo());
        recipe.setTag(request.getTag());
        recipe.setCategoryNo(1); // "레시피 공유" 카테고리 번호
        recipe.setNutrientNo(totalNutrient.getNutrientNo()); // insert 후 받아온 nutrient pk
        recipe.setImageNo(mainImageVo.getImageNo());     // insert 후 받아온 image pk
        recipe.setApproval("N");
        recipe.setIngredient(formatIngredientString(request.getIngredients()));
        
        dao.insertRecipe(recipe);

        // 4. 요리 순서 및 이미지 저장
        List<String> descriptions = request.getStepDescriptions();
        List<MultipartFile> images = request.getStepImages();

        for (int i = 0; i < descriptions.size(); i++) {
            Image stepImageVo = saveImage(images.get(i));
            dao.insertImage(stepImageVo);

            RecipeStep step = new RecipeStep();
            step.setRcpNo(recipe.getRcpNo()); // insert 후 받아온 recipe pk
            step.setStepOrder(i + 1);
            step.setDescription(descriptions.get(i));
            step.setImageNo(stepImageVo.getImageNo());
            dao.insertRecipeStep(step);
        }
    }

    // ==========================================================
    // Helper Methods (내부에서만 사용할 도우미 기능들)
    // ==========================================================

    /**
     * MultipartFile을 서버에 저장하고 Image VO 객체를 반환하는 메서드
     */
    private Image saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return new Image(0, null, null); // 이미지가 없는 경우
        }
        String savePath = servletContext.getRealPath("/resources/upload/recipe/");
        File uploadDir = new File(savePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String originName = file.getOriginalFilename();
        String serverName = UUID.randomUUID().toString() + "_" + originName;
        file.transferTo(new File(savePath + serverName));

        return new Image(0, originName, serverName);
    }
    
    // JSON 문자열을 파싱하기 위한 임시 데이터 구조
    @Data
    private static class IngredientData {
        private String name;
        private String quantity;
        private Nutrient nutrients;
    }

    /**
     * 재료 정보(JSON 문자열)를 받아 총 영양성분을 계산하는 메서드
     */
    private Nutrient calculateTotalNutrients(String ingredientsJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<IngredientData> ingredientList = mapper.readValue(ingredientsJson, new TypeReference<List<IngredientData>>() {});
        
        Nutrient total = new Nutrient();
        for (IngredientData data : ingredientList) {
            total.setEnergy(total.getEnergy() + data.getNutrients().getEnergy());
            total.setCarb(total.getCarb() + data.getNutrients().getCarb());
            total.setProtein(total.getProtein() + data.getNutrients().getProtein());
            total.setFat(total.getFat() + data.getNutrients().getFat());
            total.setSodium(total.getSodium() + data.getNutrients().getSodium());
        }
        return total;
    }

    /**
     * 재료 정보(JSON 문자열)를 DB에 저장할 하나의 문자열로 포맷팅하는 메서드
     */
    private String formatIngredientString(String ingredientsJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<IngredientData> ingredientList = mapper.readValue(ingredientsJson, new TypeReference<List<IngredientData>>() {});
        
        return ingredientList.stream()
                             .map(ing -> ing.getName() + " " + ing.getQuantity())
                             .collect(Collectors.joining(", ")); // "재료1 양1, 재료2 양2, ..."
    }

   

}