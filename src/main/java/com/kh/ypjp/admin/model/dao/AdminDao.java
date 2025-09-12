package com.kh.ypjp.admin.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminDao {
	
	private final SqlSession session;

	public List<ChallengeForm> getAllChallenges() {
		return session.selectList("admin.getAllChallenges");
	}

	public int resolveChallenge(Long formNo) {
		return session.update("admin.resolveChallenge", formNo);
	}

	public Long countBestRecipes() {
		return session.selectOne("admin.countBestRecipes");
	}
	
	public List<Recipe> getBestRecipes(Map<String, Object> param) {
		return session.selectList("admin.getBestRecipes", param);
	}

	public List<Report> getAllReports() {
		return session.selectList("admin.getAllReports");
	}

}
