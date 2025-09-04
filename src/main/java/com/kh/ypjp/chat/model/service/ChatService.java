package com.kh.ypjp.chat.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessagePost;

@Service
public class ChatService {
	
	private final ChatDao chatDao;

    public ChatService(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    public List<ChatRoomDto> getUserChatLists(Long userNo) {
        List<ChatRoomDto> rooms = chatDao.findCookingClasses(userNo);
        if (chatDao.findFaqChat(userNo) > 0) {
            rooms.add(new ChatRoomDto(0L, "FAQ BOT, 요픽", "cservice"));
        }
        if (chatDao.findAdminChat(userNo) > 0) {
            rooms.add(new ChatRoomDto(-1L, "관리자 문의하기", "chat"));
        }
        return rooms;
    }

	public int deleteFaqChat(Long userNo) {
		return chatDao.deleteFaqChat(userNo);
	}

	@Transactional
	public int deleteAdminChat(Long userNo) {
		if (chatDao.deleteAdminChatMessage(userNo) > 0) {
			return chatDao.deleteAdminChatSession(userNo);
		}
		return 0;
	}

	public int insertMessage(Map<String, Object> param) {
		return chatDao.insertMessage(param);
	}

}
