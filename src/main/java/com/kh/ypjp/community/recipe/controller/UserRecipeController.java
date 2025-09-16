package com.kh.ypjp.community.recipe.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
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
    public ResponseEntity<RecipePage> selectList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String rcpMthNo,
            @RequestParam(required = false) String rcpStaNo) 
   	{        
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sort", sort);
        params.put("ingredients", ingredients);
        params.put("rcpMthNo", rcpMthNo);
        params.put("rcpStaNo", rcpStaNo);
        
        RecipePage recipePage = recipeService.selectRecipePage(params);
        return ResponseEntity.ok(recipePage);
    }
    @GetMapping("/ranking")
    public ResponseEntity<List<UserRecipeDto.UserRecipeResponse>> selectRankingList() {
        List<UserRecipeDto.UserRecipeResponse> list = recipeService.selectRankingRecipes();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/options/methods")
    public ResponseEntity<List<RcpMethod>> getRcpMethods() {
        return ResponseEntity.ok(recipeService.selectRcpMethods());
    }

    @GetMapping("/options/situations")
    public ResponseEntity<List<RcpSituation>> getRcpSituations() {
        return ResponseEntity.ok(recipeService.selectRcpSituations());
    }

    @GetMapping("/ingredients/search")
    public ResponseEntity<List<UserRecipeDto.IngredientInfo>> searchIngredients(@RequestParam String keyword) {
        return ResponseEntity.ok(recipeService.searchIngredients(keyword));
    }

 // --- 레시피 등록 메소드 ---
    @PostMapping(value = "/community/recipe/{userNo}", consumes = "multipart/form-data")
    public ResponseEntity<Void> createRecipe(
            @ModelAttribute UserRecipeDto.RecipeWriteRequest request,
            // ✨ 바로 이 부분입니다! @AuthenticationPrincipal로 CustomOAuth2User 객체를 직접 받습니다.
            @PathVariable Long userNo 
    ) {
    	System.out.println(userNo);
    	System.out.println(request.toString());
        // Security 설정으로 인해 인증된 사용자만 이 메소드에 접근 가능하므로,
        // customUser는 null이 아니라고 가정할 수 있습니다.
        if (userNo == null) {
            // 혹시 모를 비인증 접근에 대한 방어 코드
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // ✨ CustomOAuth2User 객체에서 사용자 번호를 가져옵니다.
//            long userNo = customUser.getUserNo(); // 또는 getNo(), getId() 등 실제 메소드명 사용
            
            // 서비스에 사용자 번호와 요청 데이터를 전달합니다.
            recipeService.createRecipe(request, userNo);
            
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}