package com.kh.ypjp.mainpage.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.common.PageInfo;
import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngListResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPairResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaPost;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaPut;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.PagedIngListResponse;
import com.kh.ypjp.ingpedia.model.service.IngPediaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/main")

public class MainPageController {

	@GetMapping("")
	public ResponseEntity<PagedIngListResponse> main(
			@RequestParam Long userNo){
		PagedIngListResponse response = new PagedIngListResponse();
		
		recipeMain();
		pickRecipeMain();
		ingPediaMain();
		
		return ResponseEntity.ok(response);
	}
	
	public void recipeMain () {
		
	}
	
	public void pickRecipeMain () {
		
	}
	
	public void ingPediaMain () {
		
	}
}
