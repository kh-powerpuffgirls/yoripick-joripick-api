package com.kh.ypjp.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;
import com.kh.ypjp.admin.model.service.AdminService;
import com.kh.ypjp.common.PageInfo;
import com.kh.ypjp.common.UtilService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
	
	private final AdminService service;
	private final UtilService utilService;
	
	@GetMapping("/challenges")
	public ResponseEntity<Map<String, Object>> getAllChallenges(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<ChallengeForm> list = service.getAllChallenges(param);
		Long listCount = service.countAllChallenges();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
	@PatchMapping("/resolve/{formNo}")
    public ResponseEntity<Void> resolveChallenge(@PathVariable Long formNo) {
        service.resolveChallenge(formNo);
        return ResponseEntity.ok().build();
    }
	
	@GetMapping("/recipes")
	public ResponseEntity<Map<String, Object>> getRecipes(
			@RequestParam int page, @RequestParam int size) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
	    List<Recipe> list = service.getRecipes(param);
	    Long listCount = service.countRecipes();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/reports/user")
	public ResponseEntity<Map<String, Object>> getUserReports(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
	    List<Report> list = service.getUserReports(param);
	    Long listCount = service.countUserReports();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/reports/comm")
	public ResponseEntity<Map<String, Object>> getCommReports(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
	    List<Report> list = service.getCommReports(param);
	    Long listCount = service.countCommReports();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
}
