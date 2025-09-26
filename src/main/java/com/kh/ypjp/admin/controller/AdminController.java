package com.kh.ypjp.admin.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import com.kh.ypjp.admin.model.service.AdminService;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.common.PageInfo;
import com.kh.ypjp.common.UtilService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
	
	private final AdminService service;
	private final UtilService utilService;
	
	@GetMapping("/challenges")
	public ResponseEntity<Map<String, Object>> getAllChallenges(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<ChallengeForm> list = service.getAllChallenges(param);
		Long listCount = service.countAllChallenges();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
	@PatchMapping("/recipes/disprove/{rcpNo}")
    public ResponseEntity<Void> disproveRecipe(@PathVariable Long rcpNo) {
        service.disproveRecipe(rcpNo);
        return ResponseEntity.ok().build();
    }
	
	@PatchMapping("/recipes/approve/{rcpNo}")
    public ResponseEntity<Void> approveRecipe(@PathVariable Long rcpNo) {
        service.approveRecipe(rcpNo);
        return ResponseEntity.ok().build();
    }
	
	@GetMapping("/recipes")
	public ResponseEntity<Map<String, Object>> getRecipes(
			@RequestParam int page, @RequestParam int size) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
	    List<Recipe> list = service.getRecipes(param);
	    Long listCount = service.countRecipes();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
	@PatchMapping("/challenges/{formNo}")
    public ResponseEntity<Void> resolveChallenge(@PathVariable Long formNo) {
        service.resolveChallenge(formNo);
        return ResponseEntity.ok().build();
    }
	
	@GetMapping("/reports/user")
	public ResponseEntity<Map<String, Object>> getUserReports(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
	    List<Report> list = service.getUserReports(param);
	    Long listCount = service.countUserReports();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/reports/comm")
	public ResponseEntity<Map<String, Object>> getCommReports(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
	    List<Report> list = service.getCommReports(param);
	    Long listCount = service.countCommReports();
	    PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
        return ResponseEntity.ok(response);
    }
	
	@PatchMapping("/reports/{reportNo}")
    public ResponseEntity<Void> resolveReports(@PathVariable Long reportNo) {
        service.resolveReports(reportNo);
        return ResponseEntity.ok().build();
    }
	
	@GetMapping("/chatRooms/{userNo}")
	public ResponseEntity<ChatRoomDto> getChatRooms(@PathVariable Long userNo) {
		ChatRoomDto chatRoom = service.getChatRooms(userNo);
		Map<String, Object> param = new HashMap<>();
		param.put("refNo", chatRoom.getRoomNo());
		param.put("msgType", "CSERVICE");
		List<ChatMsgDto> chatMessages = service.getChatMessages(param);
		chatRoom.setMessages(new ArrayList<>(chatMessages));
        return ResponseEntity.ok(chatRoom);
    }
	
	@PostMapping("/users/{userNo}/{banDur}")
	public ResponseEntity<Void> banUsers(@PathVariable Long userNo, @PathVariable int banDur) {
		Map<String, Object> param = new HashMap<>();
		Date startDate = Calendar.getInstance().getTime();
		Date endDate = new Date(startDate.getTime() + (1000L * 60 * 60 * 24 * banDur));
		param.put("userNo", userNo);
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		service.banUsers(param);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping(value = "/challenges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> insertChallenges(
			@RequestPart("title") String title,
	        @RequestPart("startDate") String startDate,
	        @RequestPart("endDate") String endDate,
			@RequestPart(value = "upfile", required = false) MultipartFile upfile
			) {
		Challenge challenge = new Challenge(null, title, startDate, endDate, null, null);
		List<Challenge> challengeList = service.getChallenges(challenge);
		if (!challengeList.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		service.insertChallenges(challenge, upfile);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/announcements")
	public ResponseEntity<Void> insertAnnouncements(
			@RequestBody Announcement announcement) {
		List<Announcement> ancmtList = service.getAnnouncements(announcement);
		if (!ancmtList.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		service.insertAnnouncements(announcement);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/users")
	public ResponseEntity<Map<String, Object>> getUsers(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<UserInfo> list = service.getUsers(param);
		Long listCount = service.countAllUsers();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/all-recipes")
	public ResponseEntity<Map<String, Object>> getAllRecipes(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<RecipeInfo> list = service.getAllRecipes(param);
		Long listCount = service.countAllRecipes();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/communities")
	public ResponseEntity<Map<String, Object>> getCommunities(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<CommInfo> list = service.getCommunities(param);
		Long listCount = service.countCommunities();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/classes")
	public ResponseEntity<Map<String, Object>> getClasses(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<ClassInfo> list = service.getClasses(param);
		Long listCount = service.countClasses();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/classes/{roomNo}")
	public ResponseEntity<ChatInfo> getClassInfo(@PathVariable Long roomNo) {
		ChatInfo chatroom = service.getChatRoom(roomNo);
		return ResponseEntity.ok(chatroom);
	}
	
	@GetMapping("/cservices")
	public ResponseEntity<Map<String, Object>> getCustomerServices(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<CSinfo> list = service.getCustomerServices(param);
		Long listCount = service.countCustomerServices();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/cservices/{roomNo}")
	public ResponseEntity<ChatInfo> getCSinfo(@PathVariable Long roomNo) {
		ChatInfo chatroom = service.getCSinfo(roomNo);
		return ResponseEntity.ok(chatroom);
	}
	
	@GetMapping("/announcements")
	public ResponseEntity<Map<String, Object>> getAnnouncements(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<Announcement> list = service.getAllAnnouncements(param);
		Long listCount = service.countAnnouncements();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/announcements/{ancmtNo}")
	public ResponseEntity<Void> deleteAnnouncements(@RequestParam Long ancmtNo) {
		service.deleteAnnouncements(ancmtNo);
		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/announcements")
	public ResponseEntity<Void> editAnnouncements(@RequestBody Announcement announcement) {
		service.editAnnouncements(announcement);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/challenges/info")
	public ResponseEntity<Map<String, Object>> getChallengeInfos(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<Challenge> list = service.getChallengeInfos(param);
		Long listCount = service.countChallengeInfos();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(value = "/challenges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> editChallenges(
			@RequestParam("chInfoNo") Long chInfoNo,
			@RequestParam("title") String title,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestPart(value = "upfile", required = false) MultipartFile upfile
			) {
		Challenge challenge = new Challenge(chInfoNo, title, startDate, endDate, null, null);
		List<Challenge> challengeList = service.getChallengesExcept(challenge);
		if (!challengeList.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		service.editChallenges(challenge, upfile);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/challenges/{chInfoNo}")
	public ResponseEntity<Void> deleteChallenges(@PathVariable Long chInfoNo) {
		service.deleteChallenges(chInfoNo);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/ingredients")
	public ResponseEntity<Map<String, Object>> getIngredients(
			@RequestParam int page, @RequestParam int size) {
		Map<String, Object> param = new HashMap<>();
	    param.put("offset", (page - 1) * size);
	    param.put("limit", size);
		List<Ingredient> list = service.getIngredients(param);
		Long listCount = service.countIngredients();
		PageInfo pageInfo = utilService.getPageInfo(listCount, page, 10, size);
	    Map<String, Object> response = new HashMap<>();
	    response.put("list", list);
	    response.put("pageInfo", pageInfo);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/reports/{reportNo}")
    public ResponseEntity<ReportTargetDto> getParentRep(@PathVariable Long reportNo) {
		ReportTargetDto parentReport = service.getParentRep(reportNo);
        return ResponseEntity.ok(parentReport);
    }
	
}
