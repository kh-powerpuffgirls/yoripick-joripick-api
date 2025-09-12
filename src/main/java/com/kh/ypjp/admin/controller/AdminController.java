package com.kh.ypjp.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;
import com.kh.ypjp.admin.model.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
	
	private final AdminService service;
	
	@GetMapping("/challenges")
	public ResponseEntity<List<ChallengeForm>> getAllChallenges() {
        return ResponseEntity.ok(service.getAllChallenges());
    }
	
	@PatchMapping("/resolve/{formNo}")
    public ResponseEntity<Void> resolveChallenge(@PathVariable Long formNo) {
        service.resolveChallenge(formNo);
        return ResponseEntity.ok().build();
    }
	
	@GetMapping("/recipes")
	public ResponseEntity<List<Recipe>> getBestRecipes() {
        return ResponseEntity.ok(service.getBestRecipes());
    }
	
	@GetMapping("/reports")
	public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(service.getAllReports());
    }
	
}
