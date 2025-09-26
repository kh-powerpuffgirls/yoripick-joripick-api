package com.kh.ypjp.admin.model.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

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
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminDao {
	
	private final SqlSessionTemplate session;

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

	public List<UserInfo> getUsers(Map<String, Object> param) {
		return session.selectList("admin.getUsers", param);
	}

	public int insertChallenges(Challenge challenge) {
		return session.insert("admin.insertChallenges", challenge);
	}

	public List<Challenge> getChallenges(Challenge challenge) {
		return session.selectList("admin.getChallenges", challenge);
	}

	public int insertAnnouncements(Announcement announcement) {
		return session.insert("admin.insertAnnouncements", announcement);
	}

	public List<Announcement> getAnnouncements(Announcement announcement) {
		return session.selectList("admin.getAnnouncements", announcement);
	}

	public Long countAllUsers() {
		return session.selectOne("admin.countAllUsers");
	}

	public Long countAllRecipes() {
		return session.selectOne("admin.countAllRecipes");
	}

	public List<RecipeInfo> getAllRecipes(Map<String, Object> param) {
		return session.selectList("admin.getAllRecipes", param);
	}

	public Long countCommunities() {
		return session.selectOne("admin.countCommunities");
	}

	public List<CommInfo> getCommunities(Map<String, Object> param) {
		return session.selectList("admin.getCommunities", param);
	}

	public Long countClasses() {
		return session.selectOne("admin.countClasses");
	}

	public List<ClassInfo> getClasses(Map<String, Object> param) {
		return session.selectList("admin.getClasses", param);
	}

	public ChatInfo getChatRoom(Long roomNo) {
		return session.selectOne("admin.getChatRoom", roomNo);
	}

	public Long countCustomerServices() {
		return session.selectOne("admin.countCustomerServices");
	}

	public List<CSinfo> getCustomerServices(Map<String, Object> param) {
		return session.selectList("admin.getCustomerServices", param);
	}

	public ChatInfo getCSinfo(Long roomNo) {
		return session.selectOne("admin.getCSinfo", roomNo);
	}

	public Long countAnnouncements() {
		return session.selectOne("admin.countAnnouncements");
	}

	public List<Announcement> getAllAnnouncements(Map<String, Object> param) {
		return session.selectList("admin.getAllAnnouncements", param);
	}

	public int deleteAnnouncements(Long ancmtNo) {
		return session.delete("admin.deleteAnnouncements", ancmtNo);
	}

	public int editAnnouncements(Announcement announcement) {
		return session.update("admin.editAnnouncements", announcement);
	}

	public Long countChallengeInfos() {
		return session.selectOne("admin.countChallengeInfos");
	}

	public List<Challenge> getChallengeInfos(Map<String, Object> param) {
		return session.selectList("admin.getChallengeInfos", param);
	}

	public Long getChallengeImageNo(Long chInfoNo) {
		return session.selectOne("admin.getChallengeImageNo", chInfoNo);
	}

	public int editChallenges(Challenge challenge) {
		return session.update("admin.editChallenges", challenge);
	}

	public List<Challenge> getChallengesExcept(Challenge challenge) {
		return session.selectList("admin.getChallengesExcept", challenge);
	}

	public int deleteChallenges(Long chInfoNo) {
		return session.delete("admin.deleteChallenges", chInfoNo);
	}

	public Long countIngredients() {
		return session.selectOne("admin.countIngredients");
	}

	public List<Ingredient> getIngredients(Map<String, Object> param) {
		return session.selectList("admin.getIngredients", param);
	}

	public String findReportCategory(Long reportNo) {
		return session.selectOne("admin.findReportCategory", reportNo);
	}

	public Long findRecipeTarget(Long reportNo) {
		return session.selectOne("admin.findRecipeTarget", reportNo);
	}

	public ReportTargetDto findReplyTarget(Long reportNo) {
		return session.selectOne("admin.findReplyTarget", reportNo);
	}

}
