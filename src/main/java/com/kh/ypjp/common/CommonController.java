package com.kh.ypjp.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.admin.model.dto.AdminDto.Announcement;
import com.kh.ypjp.admin.model.dto.AdminDto.Challenge;
import com.kh.ypjp.common.model.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
	
	private final CommonService service;

	@GetMapping("/announcements")
	public ResponseEntity<Announcement> getTodayAnnouncement() {
		Announcement todayAnn = service.getTodayAnnouncement();
		if (todayAnn != null) {
			return ResponseEntity.ok().body(todayAnn); // 200
		}
		return ResponseEntity.notFound().build(); // 404
	}
	
	@GetMapping("/challenges")
	public ResponseEntity<Challenge> getTodayChallenge() {
		Challenge todayChallenge = service.getTodayChallenge();
		if (todayChallenge != null) {
			return ResponseEntity.ok().body(todayChallenge); // 200
		}
		return ResponseEntity.notFound().build(); // 404
	}
}
