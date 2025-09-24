package com.kh.ypjp.community.recipe.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.community.recipe.dto.UserRecipeDto;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.OfficialRecipePage;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipeDetailResponse;
import com.kh.ypjp.community.recipe.dto.UserRecipeDto.RecipePage;
import com.kh.ypjp.community.recipe.service.UserRecipeService;
import com.kh.ypjp.mealplan.controller.MealplanController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recipe")
public class OfficialRecipeController {


	private final UserRecipeService recipeService;

	@GetMapping
	@CrossOrigin(origins = "http://localhost:5173/", exposedHeaders = "Location")
	public ResponseEntity<OfficialRecipePage> selectOfficialList(
			@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "bookmarks_desc") String sort,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String rcpMthNo,
            @RequestParam(required = false) String rcpStaNo,
            @PathVariable int userNo // 북마크 여부 확인을 위한 userNo
			){
		
		HashMap<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("sort", sort);
        params.put("ingredients", ingredients);
        params.put("rcpMthNo", rcpMthNo);
        params.put("rcpStaNo", rcpStaNo);
        params.put("loginUserNo", userNo);
        
        log.info("나오나?:{}",params);
        
        OfficialRecipePage recipePage = recipeService.selectOfficialRecipePage(params);
        
		return ResponseEntity.ok(recipePage);
	}

	@GetMapping("/{rcpNo}")
	public ResponseEntity<RecipeDetailResponse> selectOfficialRecipeDetailForGuest(@PathVariable int rcpNo,
			HttpServletRequest req, HttpServletResponse res) {

		// userNo가 없으므로 null을 전달하여 기존 메소드를 호출합니다.
		return selectOfficialRecipeDetail(rcpNo, null, req, res);
	}

	// --- 공식 레시피 상세 조회 API ---
	@GetMapping("/{rcpNo}/{userNo}")
	public ResponseEntity<RecipeDetailResponse> selectOfficialRecipeDetail(
			@PathVariable int rcpNo,
			@PathVariable(required = false) Long userNo, HttpServletRequest req, HttpServletResponse res) {

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

		RecipeDetailResponse recipeDetail = recipeService.selectOfficialRecipeDetail(rcpNo, userNo, increase);

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

	// 북마크 토글 API
	@PostMapping("/{rcpNo}/bookmark/{userNo}")
	public ResponseEntity<UserRecipeDto.BookmarkResponse> toggleBookmark(@PathVariable int rcpNo,
			@PathVariable long userNo) {

		UserRecipeDto.BookmarkResponse response = recipeService.toggleBookmark(rcpNo, userNo);
		return ResponseEntity.ok(response);
	}
	
}
