package com.kh.ypjp.common.model.service;

import org.springframework.stereotype.Service;

import com.kh.ypjp.admin.model.dao.AdminDao;
import com.kh.ypjp.admin.model.dto.AdminDto.Announcement;
import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.common.model.dao.CommonDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonService {
	
	private final CommonDao dao;
	
	public Announcement getTodayAnnouncement() {
		return dao.getTodayAnnouncement();
	}

}
