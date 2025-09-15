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
	
	public Long countAllChallenges() {
		return dao.countAllChallenges();
	}
	
	public List<ChallengeForm> getAllChallenges(Map<String, Object> param) {
		return dao.getAllChallenges(param);
	}

	public Long countRecipes() {
		return dao.countRecipes();
	}
	
	public List<Recipe> getRecipes(Map<String, Object> param) {
		return dao.getRecipes(param);
	}

	public List<Report> getUserReports(Map<String, Object> param) {
		return dao.getUserReports(param);
	}

	public Long countUserReports() {
		return dao.countUserReports();
	}

	public Long countCommReports() {
		return dao.countCommReports();
	}

	public List<Report> getCommReports(Map<String, Object> param) {
		return dao.getCommReports(param);
	}

	public int resolveChallenge(Long formNo) {
		return dao.resolveChallenge(formNo);
	}
	
	public int resolveReports(Long reportNo) {
		return dao.resolveReports(reportNo);
	}

}
