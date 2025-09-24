package com.kh.ypjp.sbti.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.sbti.model.dto.SbtiDto.SikBti;
import com.kh.ypjp.sbti.model.service.SbtiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SbtiController {
	
	private final SbtiService service;
	
	@GetMapping("/sbti")
	public ResponseEntity<List<SikBti>> getScoreMap() {
		List<SikBti> scoreMap = service.getScoreMap();
		return ResponseEntity.ok(scoreMap);
	}
	
	@PatchMapping("/eatbti/{userNo}")
	public ResponseEntity<Void> eatbtiResult(
			@PathVariable Long userNo, @RequestParam String sikBti) {
		Map<String, Object> param = new HashMap<>();
		param.put("userNo", userNo);
		param.put("sikBti", sikBti);
		service.eatbtiResult(param);
		return ResponseEntity.ok().build();
	}
}
