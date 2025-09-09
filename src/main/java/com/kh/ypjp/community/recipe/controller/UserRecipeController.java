package com.kh.ypjp.community.recipe.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.service.UserRecipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserRecipeController {
	private final UserRecipeService recipeService;
	
	@GetMapping("/community/recipe")
	@CrossOrigin(origins = "http://localhost:5173/", exposedHeaders = "Location")
	public ResponseEntity<List<UserRecipeDto.UserRecipeResponse>> selectList(
				@RequestParam HashMap<String,Object> param
			){
		List<UserRecipeDto.UserRecipeResponse> list = recipeService.selectRecipe(param);
		
		return ResponseEntity.ok(list);
	}
	
	 // 2. 요리 방법 목록 조회 (추가)
    @GetMapping("/options/methods")
    public ResponseEntity<List<RcpMethod>> getRcpMethods() {
        return ResponseEntity.ok(recipeService.selectRcpMethods());
    }

    // 3. 요리 종류 목록 조회 (추가)
    @GetMapping("/options/situations")
    public ResponseEntity<List<RcpSituation>> getRcpSituations() {
        return ResponseEntity.ok(recipeService.selectRcpSituations());
    }

    // 4. 재료 검색 (추가)
    @GetMapping("/ingredients/search")
    public ResponseEntity<List<UserRecipeDto.IngredientInfo>> searchIngredients(@RequestParam String keyword) {
        return ResponseEntity.ok(recipeService.searchIngredients(keyword));
    }
    
    // 5. 레시피 등록 (추가)
    @PostMapping(value = "/community/recipe", consumes = "multipart/form-data")
    public ResponseEntity<Void> createRecipe(@ModelAttribute UserRecipeDto.RecipeWriteRequest request) {
        try {
            // userNo는 임시로 하드코딩 (Security 적용 후 수정)
            long userNo = 1; 
            recipeService.createRecipe(request, userNo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // 로그 출력 및 에러 응답
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
	
}
