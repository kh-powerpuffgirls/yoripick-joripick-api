package com.kh.ypjp.admin.model.dao;

import java.util.List;

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

	public List<Recipe> getBestRecipes() {
		return session.selectList("admin.getBestRecipes");
	}

	public List<Report> getAllReports() {
		return session.selectList("admin.getAllReports");
	}

}
