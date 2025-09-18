package com.kh.ypjp.mying.controller;

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

import com.kh.ypjp.mying.model.dto.MyIngDto;
import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngPost;
import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngPut;
import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngResponse;
import com.kh.ypjp.mying.model.service.MyIngService;
import com.kh.ypjp.security.model.provider.JWTProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inglist")
public class MyIngController {

	private final MyIngService myingService;

	@GetMapping("/{userNo}")
	public ResponseEntity<List<MyIngResponse>> myings(
//			@Parameter(description = "검색 필터 (type, taste)")
			@PathVariable long userNo,
			@RequestParam HashMap<String, Object> param // 검색 파라미터값
			){
		
		List<MyIngDto.MyIngResponse> list = myingService.selectMyIngs(param);
		
		
		return ResponseEntity.ok(list);
	}

	// 상세조회
	@GetMapping("/detail/{ingNo}/{userNo}")
	public ResponseEntity<MyIngResponse> myingDetail(
			@PathVariable long ingNo,
			@PathVariable long userNo
			){
		HashMap param = new HashMap();
		param.put("ingNo", ingNo);
		param.put("userNo", userNo);
		
		MyIngResponse myIng = myingService.selectMyIngDetail(param);
		
		if(myIng == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().body(myIng);
		}
	}
	
	// 등록
	@PostMapping("/detail")
	public ResponseEntity<MyIngResponse> insertMying(
			@RequestBody MyIngPost mying
			){
		
		int result = myingService.insertMying(mying);
		
		if(result > 0) {
//			URI location = URI.create("/detail/"+mying.getIngNo()+"/"+mying.getUserNo());
			URI location = URI.create("/"+mying.getUserNo());
			// 201 Created
			return ResponseEntity.created(location).build();
		} else {
			// 400 bad Request
			return ResponseEntity.badRequest().build();
		}
	}
	
	// 수정
	@PutMapping("/detail/{ingNo}/{userNo}")
	public ResponseEntity<MyIngResponse> updateMying(
			@RequestBody MyIngPut mying,
			@PathVariable long ingNo,
			@PathVariable long userNo
			){
		mying.setIngNo(ingNo);
		mying.setUserNo(userNo);
		int result = myingService.updateMying(mying);
		
		if(result > 0) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
			
		}
	}
	
	// 메뉴 삭제
		@DeleteMapping("/detail/{ingNo}/{userNo}")
		public ResponseEntity<Void> deleteMying(
				@PathVariable long ingNo,
				@PathVariable long userNo
				){
			HashMap param = new HashMap();
			param.put("ingNo", ingNo);
			param.put("userNo", userNo);

			int result = myingService.deleteMying(param);
			
			if(result > 0) {
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
				
			}
		}
}
