package com.kh.ypjp.common.model.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.admin.model.dto.AdminDto.Announcement;
import com.kh.ypjp.admin.model.dto.AdminDto.Challenge;
import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.common.model.dao.CommonDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonService {
	
	private final CommonDao dao;
	private final UtilService utilService;
	
	public Announcement getTodayAnnouncement() {
		return dao.getTodayAnnouncement();
	}

	public Challenge getTodayChallenge() {
		Challenge challenge = dao.getTodayChallenge();
		String serverName = utilService.getChangeName(challenge.getImageNo());
		String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/images/challenges/" + serverName).toUriString();
		challenge.setImageUrl(imageUrl);
		return challenge;
	}

}
