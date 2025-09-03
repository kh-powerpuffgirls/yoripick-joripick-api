package com.kh.ypjp.chat.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;

@Repository
public class ChatDao {
	
	@Autowired
	private SqlSessionTemplate session;

	public List<ChatRoomDto> findCookingClasses(Long userNo) {
		return session.selectList("chat.findCookingClasses", userNo);
	}

	public int findFaqChat(Long userNo) {
		return session.selectOne("chat.findFaqChat", userNo);
	}

	public int findAdminChat(Long userNo) {
		return session.selectOne("chat.findAdminChat", userNo);
	}

}
