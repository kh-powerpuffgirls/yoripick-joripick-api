package com.kh.ypjp.ingpedia.controller;

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
@RequestMapping("/ingpedia")

public class IngPediaController {

	private final IngPediaService ingPediaService;
	private final UtilService utilService;
	
	@GetMapping("")
	public ResponseEntity<PagedIngListResponse> ingPediaList(
			@RequestParam HashMap<String, Object> param){

		long listCount = ingPediaService.selectTotalIngPedia(param);
		int currentPage = 1;
		
		if(!param.get("page").equals("0")) {
			currentPage = Integer.parseInt(((String)param.get("page")));
		}
		int pageLimit = 5;
		int itemLimit = 48;
		
		PageInfo pi = new PageInfo(); 
		pi = utilService.getPageInfo(listCount, currentPage, pageLimit, itemLimit);
		
		int startRow = (pi.getCurrentPage() - 1) * itemLimit;
		int endRow = startRow + itemLimit;
		
		param.put("startRow", startRow);
		param.put("endRow", endRow);
		
		List<IngListResponse> list = ingPediaService.selectIngPagediaList(param);
		
		PagedIngListResponse response = new PagedIngListResponse(list, pi);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/detail/{ingNo}")
	public ResponseEntity<IngPediaResponse> ingPedia(
			@PathVariable long ingNo
			){
		IngPediaResponse ingPedia = new IngPediaResponse();
		ingPedia.setIngDetail(ingPediaService.selectIngPediaDetail(ingNo));
		
		List<IngPairResponse> ingpair = ingPediaService.selectIngPediaPair(ingNo);
		if(ingpair != null) ingPedia.setPairList(ingpair);
		
		if(ingPedia == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().body(ingPedia);
		}
	}
	
	@PostMapping("")
	public ResponseEntity<Void> insertIngPedia(
			@RequestBody IngPediaPost IngPedia
			){
		int methodResult = ingPediaService.insertIngMethod(IngPedia);
		int pairResult = 1;
		
		System.out.println("ingpair 확인");
		System.out.println(IngPedia.getPairList());
		
		if(IngPedia.getPairList() != null && IngPedia.getPairList().size() > 0) {
			pairResult = ingPediaService.insertIngPair(IngPedia);
		}
		
		if(methodResult > 0 && pairResult > 0) {
			// Post 요청의 경우 응답데이터 header에 이동할 uri 정보를 적는 것이 규칙
			URI location = URI.create("/ingpedia");
			// 201 Created
			return ResponseEntity.created(location).build();
		} else {
			// 400 bad Request
			return ResponseEntity.badRequest().build();
		}
	}
	
	@PutMapping("/detail/{ingNo}")
	public ResponseEntity<IngPediaResponse> updateIngPedia(
			@PathVariable long ingNo,
			@RequestBody IngPediaPut IngPedia
			){

		int methodResult = ingPediaService.updateIngMethod(IngPedia);
		
		int pairResult1 = ingPediaService.deleteIngPairs(IngPedia);
		int pairResult2 = ingPediaService.updateIngPair(IngPedia);
		
		if(methodResult > 0 && pairResult1 > 0 && pairResult2 > 0 ) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/detail/{ingNo}")
	public ResponseEntity<Void> deleteIngPedia(
			@PathVariable long ingNo
			){

		int methodResult = ingPediaService.deleteIngMethod(ingNo);
		int pairResult = ingPediaService.deleteIngPair(ingNo);
		
		if(methodResult > 0 && pairResult > 0) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	
	
	
	
	
//
//	// 상세조회
//	@GetMapping("/detail/{ingNo}/{userNo}")
//	public ResponseEntity<MyIngResponse> myingDetail(
//			@PathVariable long ingNo,
//			@PathVariable long userNo
//			){
//		HashMap param = new HashMap();
//		param.put("ingNo", ingNo);
//		param.put("userNo", userNo);
//		
//		MyIngResponse myIng = myingService.selectMyIngDetail(param);
//		
//		if(myIng == null) {
//			return ResponseEntity.notFound().build();
//		} else {
//			return ResponseEntity.ok().body(myIng);
//		}
//	}
//	
//	// 수정
//	@PutMapping("/detail/{ingNo}/{userNo}")
//	public ResponseEntity<MyIngResponse> updateMying(
//			@RequestBody MyIngPut mying,
//			@PathVariable long ingNo,
//			@PathVariable long userNo
//			){
//		mying.setIngNo(ingNo);
//		mying.setUserNo(userNo);
//		int result = myingService.updateMying(mying);
//		
//		if(result > 0) {
//			return ResponseEntity.noContent().build();
//		} else {
//			return ResponseEntity.notFound().build();
//			
//		}
//	}
//	
//	// 메뉴 삭제
//		@DeleteMapping("/detail/{ingNo}/{userNo}")
//		public ResponseEntity<Void> deleteMying(
//				@PathVariable long ingNo,
//				@PathVariable long userNo
//				){
//			HashMap param = new HashMap();
//			param.put("ingNo", ingNo);
//			param.put("userNo", userNo);
//
//			int result = myingService.deleteMying(param);
//			
//			if(result > 0) {
//				return ResponseEntity.noContent().build();
//			} else {
//				return ResponseEntity.notFound().build();
//				
//			}
//		}
}
