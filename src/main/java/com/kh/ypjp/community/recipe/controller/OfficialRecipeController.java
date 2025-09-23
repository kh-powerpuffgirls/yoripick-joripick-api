package com.kh.ypjp.community.recipe.controller;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeDetailResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewPageResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.ReviewResponseDto;
import com.kh.ypjp.community.recipe.model.vo.RcpMethod;
import com.kh.ypjp.community.recipe.model.vo.RcpSituation;
import com.kh.ypjp.community.recipe.service.UserRecipeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recipe")
public class OfficialRecipeController {
	
	private final UserRecipeService recipeService;
	
	 // --- 공식 레시피 상세 조회 API ---
    @GetMapping("/{rcpNo}")
    public ResponseEntity<RecipeDetailResponse> selectOfficialRecipeDetail(
            @PathVariable int rcpNo,
            // 공식 레시피는 사용자 정보가 필요 없으므로 userNo를 받지 않습니다.
            HttpServletRequest req,
            HttpServletResponse res) {
        
        // 조회수 중복 방지 로직 (기존과 동일)
        String cookieName = "readRecipeNo";
        String readRecipeNoCookie = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    readRecipeNoCookie = cookie.getValue();
                    break;
                }
            }
        }
        
        boolean increase = false;
        if (readRecipeNoCookie == null) {
            increase = true;
            readRecipeNoCookie = String.valueOf(rcpNo);
        } else if (!Arrays.asList(readRecipeNoCookie.split("/")).contains(String.valueOf(rcpNo))) {
            increase = true;
            readRecipeNoCookie += "/" + rcpNo;
        }
        
        RecipeDetailResponse recipeDetail = recipeService.selectOfficialRecipeDetail(rcpNo, increase);

        if (recipeDetail == null) {
            return ResponseEntity.notFound().build();
        }

        if (increase) {
            Cookie newCookie = new Cookie(cookieName, readRecipeNoCookie);
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            res.addCookie(newCookie);
        }
        
        return ResponseEntity.ok(recipeDetail);
    }

}
