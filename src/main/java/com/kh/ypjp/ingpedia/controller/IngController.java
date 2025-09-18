package com.kh.ypjp.ingpedia.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.common.PageInfo;
import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.ing.model.dto.IngDto;
import com.kh.ypjp.ing.model.dto.IngDto.IngCodeResponse;
import com.kh.ypjp.ing.model.dto.IngDto.IngListResponse;
import com.kh.ypjp.ing.model.dto.IngDto.IngResponse;
import com.kh.ypjp.ing.model.service.IngService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ingdata")

public class IngController {

	private final IngService ingService;
	private final UtilService utilService;

	@GetMapping("")
	public ResponseEntity<IngListResponse> ings(
			@RequestParam HashMap<String, Object> param // 검색 파라미터값
			){
		
		long listCount = ingService.selectTotalIngs(param);
		int currentPage = 1;
		
		if(!param.get("page").equals("0")) {
			currentPage = Integer.parseInt(((String)param.get("page")));
		}
		int pageLimit = 5;
		int itemLimit = 10;
		
		PageInfo pi = new PageInfo(); 
		pi = utilService.getPageInfo(listCount, currentPage, pageLimit, itemLimit);
		
		int startRow = (pi.getCurrentPage() - 1) * itemLimit;
		int endRow = startRow + itemLimit;
		
		param.put("startRow", startRow);
		param.put("endRow", endRow);
		
		List<IngDto.IngResponse> list = ingService.selectIngs(param);
		
		IngListResponse response = new IngListResponse(list, pi);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/codes")
	public ResponseEntity<List<IngCodeResponse>> ingCodes(){
		
		List<IngDto.IngCodeResponse> list = ingService.selectIngCodes();
		System.out.println(list);
		
		return ResponseEntity.ok(list);
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
