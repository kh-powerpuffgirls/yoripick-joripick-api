package com.kh.ypjp.admin.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.ypjp.admin.model.dao.AdminDao;
import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminDao dao;
	
	public List<ChallengeForm> getAllChallenges() {
		return dao.getAllChallenges();
	}

	public int resolveChallenge(Long formNo) {
		return dao.resolveChallenge(formNo);
	}

	public Long countBestRecipes() {
		return dao.countBestRecipes();
	}
	
	public List<Recipe> getBestRecipes(Map<String, Object> param) {
		return dao.getBestRecipes(param);
	}

	public List<Report> getAllReports() {
		return dao.getAllReports();
	}

}
