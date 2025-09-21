package com.kh.ypjp.admin.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;
import com.kh.ypjp.admin.model.dto.AdminDto.UserInfo;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminDao {
	
	private final SqlSession session;

	public Long countAllChallenges() {
		return session.selectOne("admin.countAllChallenges");
	}
	
	public List<ChallengeForm> getAllChallenges(Map<String, Object> param) {
		return session.selectList("admin.getAllChallenges", param);
	}

	public Long countRecipes() {
		return session.selectOne("admin.countRecipes");
	}
	
	public List<Recipe> getRecipes(Map<String, Object> param) {
		return session.selectList("admin.getRecipes", param);
	}

	public Long countUserReports() {
		return session.selectOne("admin.countUserReports");
	}
	
	public List<Report> getUserReports(Map<String, Object> param) {
		return session.selectList("admin.getUserReports", param);
	}

	public Long countCommReports() {
		return session.selectOne("admin.countCommReports");
	}

	public List<Report> getCommReports(Map<String, Object> param) {
		return session.selectList("admin.getCommReports", param);
	}
	
	public int resolveChallenge(Long formNo) {
		return session.update("admin.resolveChallenge", formNo);
	}

	public int resolveReports(Long reportNo) {
		return session.update("admin.resolveReports", reportNo);
	}

	public int disproveRecipe(Long rcpNo) {
		return session.update("admin.disproveRecipe", rcpNo);
	}

	public int approveRecipe(Long rcpNo) {
		return session.update("admin.approveRecipe", rcpNo);
	}

	public ChatRoomDto getChatRooms(Long userNo) {
		return session.selectOne("admin.getChatRooms", userNo);
	}

	public List<ChatMsgDto> getChatMessages(Map<String, Object> param) {
		return session.selectList("admin.getChatMessages", param);
	}

	public int banUsers(Map<String, Object> param) {
		return session.insert("admin.banUsers", param);
	}

	public List<UserInfo> getUsers() {
		return session.selectList("admin.getUsers");
	}

}
