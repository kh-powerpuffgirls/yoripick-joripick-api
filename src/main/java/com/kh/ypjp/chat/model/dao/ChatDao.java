package com.kh.ypjp.chat.model.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgResDto;

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

	public Long findAdminChat(Long userNo) {
		return session.selectOne("chat.findAdminChat", userNo);
	}

	public int deleteFaqChat(Long userNo) {
		return session.delete("chat.deleteFaqChat", userNo);
	}

	public int deleteAdminChatMessage(Long userNo) {
		return session.delete("chat.deleteAdminChatMessage", userNo);
	}
	
	public int deleteAdminChatSession(Long userNo) {
		return session.delete("chat.deleteAdminChatSession", userNo);
	}

	public int insertChatBot(FaqMsgResDto message) {
		return session.insert("chat.insertChatBot", message);
	}

	public List<ChatMsgDto> getMessagesByRoom(Map<String, Object> param) {
		return session.selectList("chat.getMessagesByRoom", param);
	}

	public List<FaqMsgResDto> getFaqByUser(Long userNo) {
		return session.selectList("chat.getFaqByUser", userNo);
	}

	public Long getCsNoByUserNo(Long userNo) {
		return session.selectOne("chat.getCsNoByUserNo", userNo);
	}

	public void insertCservice(Long userNo) {
		session.insert("chat.insertCservice", userNo);
	}

	public int insertMessage(Map<String, Object> param) {
		return session.insert("chat.insertMessage", param);
	}

	public List<ChatRoomDto> getAllCserviceRooms() {
		return session.selectList("chat.getAllCserviceRooms");
	}

}
