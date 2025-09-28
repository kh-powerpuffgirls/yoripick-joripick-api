package com.kh.ypjp.common.model.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.admin.model.dto.AdminDto.Announcement;
import com.kh.ypjp.admin.model.dto.AdminDto.Challenge;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommonDao {
	
	private final SqlSession session;
	
	public Announcement getTodayAnnouncement() {
		return session.selectOne("common.getTodayAnnouncement");
	}

	public Challenge getTodayChallenge() {
		return session.selectOne("common.getTodayChallenge");
	}

}
