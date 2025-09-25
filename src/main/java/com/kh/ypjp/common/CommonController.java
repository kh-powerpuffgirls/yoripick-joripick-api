package com.kh.ypjp.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.admin.model.dto.AdminDto.Announcement;
import com.kh.ypjp.admin.model.service.AdminService;
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
		System.out.println(todayAnn);
		return ResponseEntity.ok(todayAnn);
	}
}
