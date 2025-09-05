package com.kh.ypjp.chat.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgResDto;

@Service
public class ChatService {
	
	private final ChatDao chatDao;

    public ChatService(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    public List<ChatRoomDto> getUserChatLists(Long userNo) {
        List<ChatRoomDto> rooms = chatDao.findCookingClasses(userNo);
        if (rooms != null) {
            for (ChatRoomDto room : rooms) {
                List<ChatMsgDto> chatMessages = chatDao.getMessagesByRoom(room.getClassNo());
                room.setMessages(new ArrayList<>(chatMessages));
            }
        } else { rooms = new ArrayList<>(); }
        if (chatDao.findFaqChat(userNo) > 0) {
            List<FaqMsgResDto> faqMessages = chatDao.getFaqByUser(userNo);
            rooms.add(new ChatRoomDto(0L, "FAQ BOT, 요픽", "cservice", new ArrayList<>(faqMessages)));
        }
        Long csNo = chatDao.findAdminChat(userNo);
        if (csNo != null) {
            List<ChatMsgDto> adminMessages = chatDao.getMessagesByRoom(csNo);
            rooms.add(new ChatRoomDto(-1L, "관리자 문의하기", "chat", new ArrayList<>(adminMessages)));
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

	public int insertChatBot(FaqMsgResDto message) {
		return chatDao.insertChatBot(message);
	}

}
