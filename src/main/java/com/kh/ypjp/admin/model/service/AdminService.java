package com.kh.ypjp.admin.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.ypjp.admin.model.dao.AdminDao;
import com.kh.ypjp.admin.model.dto.AdminDto.Announcement;
import com.kh.ypjp.admin.model.dto.AdminDto.Challenge;
import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.ChatInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.ChatInfo.ChatMsg;
import com.kh.ypjp.admin.model.dto.AdminDto.ClassInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.CommInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.RecipeInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;
import com.kh.ypjp.admin.model.dto.AdminDto.UserInfo;
import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.common.UtilService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminDao dao;
	private final ChatDao chatDao;
	private final UtilService utilService;
	
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

	public List<UserInfo> getUsers(Map<String, Object> param) {
		return dao.getUsers(param);
	}

	@Transactional
	public int insertChallenges(Challenge challenge, MultipartFile upfile) {
		if (!(upfile == null || upfile.isEmpty())) {
			String webPath = "challenges";
			String changeName = utilService.getChangeName(upfile, webPath);
			Map<String, Object> param = new HashMap<>();
			String serverName = webPath + "/" + changeName;
			param.put("serverName", serverName);
			param.put("originName", upfile.getOriginalFilename());
			int result = utilService.insertImage(param);
			if (result > 0) {
				challenge.setImageNo(utilService.getImageNo(param));
			} else {
				return 0;
			}
			return dao.insertChallenges(challenge);
		} else {
			return 0;
		}
	}

	public List<Challenge> getChallenges(Challenge challenge) {
		return dao.getChallenges(challenge);
	}

	public int insertAnnouncements(Announcement announcement) {
		return dao.insertAnnouncements(announcement);
	}

	public List<Announcement> getAnnouncements(Announcement announcement) {
		return dao.getAnnouncements(announcement);
	}

	public Long countAllUsers() {
		return dao.countAllUsers();
	}

	public Long countAllRecipes() {
		return dao.countAllRecipes();
	}

	public List<RecipeInfo> getAllRecipes(Map<String, Object> param) {
		return dao.getAllRecipes(param);
	}

	public Long countCommunities() {
		return dao.countCommunities();
	}

	public List<CommInfo> getCommunities(Map<String, Object> param) {
		return dao.getCommunities(param);
	}

	public Long countClasses() {
		return dao.countClasses();
	}

	public List<ClassInfo> getClasses(Map<String, Object> param) {
		return dao.getClasses(param);
	}

	public ChatInfo getChatRoom(Long roomNo) {
		ChatInfo chatRoom = dao.getChatRoom(roomNo);
		return chatRoom;
	}

}
