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

	public List<ChallengeForm> getAllChallenges(Map<String, Object> param) {
		return session.selectList("admin.getAllChallenges", param);
	}

	public int resolveChallenge(Long formNo) {
		return session.update("admin.resolveChallenge", formNo);
	}

	public Long countRecipes() {
		return session.selectOne("admin.countRecipes");
	}
	
	public List<Recipe> getRecipes(Map<String, Object> param) {
		return session.selectList("admin.getRecipes", param);
	}

	public List<Report> getReports() {
		return session.selectList("admin.getReports");
	}

}
