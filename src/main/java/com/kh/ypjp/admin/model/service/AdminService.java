package com.kh.ypjp.admin.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.admin.model.dao.AdminDao;
import com.kh.ypjp.admin.model.dto.AdminDto.Announcement;
import com.kh.ypjp.admin.model.dto.AdminDto.CSinfo;
import com.kh.ypjp.admin.model.dto.AdminDto.Challenge;
import com.kh.ypjp.admin.model.dto.AdminDto.ChallengeForm;
import com.kh.ypjp.admin.model.dto.AdminDto.ChatInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.ClassInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.CommInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.Ingredient;
import com.kh.ypjp.admin.model.dto.AdminDto.Recipe;
import com.kh.ypjp.admin.model.dto.AdminDto.RecipeInfo;
import com.kh.ypjp.admin.model.dto.AdminDto.Report;
import com.kh.ypjp.admin.model.dto.AdminDto.ReportTargetDto;
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
//			String serverName = webPath + "/" + changeName;
			param.put("serverName", changeName);
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
		return dao.getChatRoom(roomNo);
	}

	public Long countCustomerServices() {
		return dao.countCustomerServices();
	}

	public List<CSinfo> getCustomerServices(Map<String, Object> param) {
		return dao.getCustomerServices(param);
	}

	public ChatInfo getCSinfo(Long roomNo) {
		return dao.getCSinfo(roomNo);
	}

	public Long countAnnouncements() {
		return dao.countAnnouncements();
	}

	public List<Announcement> getAllAnnouncements(Map<String, Object> param) {
		return dao.getAllAnnouncements(param);
	}

	public int deleteAnnouncements(Long ancmtNo) {
		return dao.deleteAnnouncements(ancmtNo);
	}

	public int editAnnouncements(Announcement announcement) {
		return dao.editAnnouncements(announcement);
	}

	public Long countChallengeInfos() {
		return dao.countChallengeInfos();
	}

	public List<Challenge> getChallengeInfos(Map<String, Object> param) {
		List<Challenge> list = dao.getChallengeInfos(param);
		for (Challenge c : list) {
			String serverName = utilService.getChangeName(c.getImageNo());
			String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/images/challenges/" + serverName).toUriString();
			c.setImageUrl(imageUrl);
		}
		return list;
	}
	
	@Transactional
	public int editChallenges(Challenge challenge, MultipartFile upfile) {
		if (upfile == null || upfile.isEmpty()) {
			challenge.setImageNo(dao.getChallengeImageNo(challenge.getChInfoNo()));
		} else {
			String webPath = "challenges";
			String changeName = utilService.getChangeName(upfile, webPath);
			Map<String, Object> param = new HashMap<>();
//			String serverName = webPath + "/" + changeName;
			param.put("serverName", changeName);
			param.put("originName", upfile.getOriginalFilename());
			if (utilService.insertImage(param) > 0) {
				challenge.setImageNo(utilService.getImageNo(param));
			} else {
				return 0;
			}
		}
		return dao.editChallenges(challenge);
	}

	public List<Challenge> getChallengesExcept(Challenge challenge) {
		return dao.getChallengesExcept(challenge);
	}

	public int deleteChallenges(Long chInfoNo) {
		return dao.deleteChallenges(chInfoNo);
	}

	public Long countIngredients() {
		return dao.countIngredients();
	}

	public List<Ingredient> getIngredients(Map<String, Object> param) {
		return dao.getIngredients(param);
	}

	public ReportTargetDto getParentRep(Long reportNo) {
		String category = dao.findReportCategory(reportNo);
	    if ("REVIEW".equals(category)) {
	    	return dao.findRecipeTarget(reportNo);
	    } else if ("REPLY".equals(category)) {
	    	ReportTargetDto reply = dao.findReplyTarget(reportNo);
	        return new ReportTargetDto(reply.getCategory(), reply.getTargetNo());
	    }
	    return null;
	}

	public int findActiveBanByUser(Map<String, Object> param) {
		return dao.findActiveBanByUser(param);
	}

	public int extendBan(Map<String, Object> param) {
		return dao.extendBan(param);
	}

}
