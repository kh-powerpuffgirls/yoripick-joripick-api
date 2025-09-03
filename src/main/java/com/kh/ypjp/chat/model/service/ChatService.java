package com.kh.ypjp.chat.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;

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
		// TODO Auto-generated method stub
		return 0;
	}

	public int deleteAdminChat(Long userNo) {
		// TODO Auto-generated method stub
		return 0;
	}

}
