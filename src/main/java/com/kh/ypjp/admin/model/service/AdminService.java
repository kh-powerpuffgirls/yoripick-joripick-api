package com.kh.ypjp.admin.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.admin.model.dao.AdminDao;
import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;
import com.kh.ypjp.admin.model.dto.AdminDto.UserInfo;
import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminDao dao;
	private final ChatDao chatDao;
	
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

	public int disproveRecipe(Long rcpNo) {
		return dao.disproveRecipe(rcpNo);
	}

	public int approveRecipe(Long rcpNo) {
		return dao.approveRecipe(rcpNo);
	}

	@Transactional
	public ChatRoomDto getChatRooms(Long userNo) {
		ChatRoomDto chatroom = dao.getChatRooms(userNo);
		if (chatroom == null) {
			if (chatDao.insertCservice(userNo) > 0) {
				chatroom = dao.getChatRooms(userNo);
			}
		}
		return chatroom;
	}

	public List<ChatMsgDto> getChatMessages(Map<String, Object> param) {
		return dao.getChatMessages(param);
	}

	public int banUsers(Map<String, Object> param) {
		return dao.banUsers(param);
	}

	public List<UserInfo> getUsers() {
		return dao.getUsers();
	}

}
