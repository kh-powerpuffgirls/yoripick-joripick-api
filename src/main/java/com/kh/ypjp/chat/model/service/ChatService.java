package com.kh.ypjp.chat.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgResDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessageDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatDao chatDao;

    public List<ChatRoomDto> getUserChatLists(Long userNo) {
    	Map<String, Object> param = new HashMap<>();
        List<ChatRoomDto> rooms = chatDao.findCookingClasses(userNo);
        if (rooms != null) {
            for (ChatRoomDto room : rooms) {
            	param.put("refNo", room.getClassNo());
            	param.put("msgType", "CCLASS");
                List<ChatMsgDto> chatMessages = chatDao.getMessagesByRoom(param);
                room.setMessages(new ArrayList<>(chatMessages));
            }
        } else { rooms = new ArrayList<>(); }
        if (chatDao.findFaqChat(userNo) > 0) {
            List<FaqMsgResDto> faqMessages = chatDao.getFaqByUser(userNo);
            rooms.add(new ChatRoomDto(0L, "FAQ BOT, 요픽", "cservice", new ArrayList<>(faqMessages)));
        }
        Long csNo = chatDao.findAdminChat(userNo);
        if (csNo != null) {
        	param.put("refNo", csNo);
        	param.put("msgType", "CSERVICE");
            List<ChatMsgDto> adminMessages = chatDao.getMessagesByRoom(param);
            rooms.add(new ChatRoomDto(-1L, "관리자 문의하기", "admin", new ArrayList<>(adminMessages)));
        }
        rooms.sort((r1, r2) -> {
            Date r1Latest = r1.getMessages().isEmpty() ? new Date(0) 
                                : r1.getMessages().get(r1.getMessages().size() - 1).getCreatedAt();
            Date r2Latest = r2.getMessages().isEmpty() ? new Date(0) 
                                : r2.getMessages().get(r2.getMessages().size() - 1).getCreatedAt();
            return r2Latest.compareTo(r1Latest);
        });
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

	@Transactional
	public int insertCservice(MessageDto message) {
		Long csNo = chatDao.getCsNoByUserNo(message.getUserNo());
		if (csNo == null) {
			chatDao.insertCservice(message.getUserNo());
			csNo = chatDao.getCsNoByUserNo(message.getUserNo());
			messagingTemplate.convertAndSend("/topic/admin", csNo);
		}
		Map<String, Object> param = new HashMap<>();
		param.put("roomId", csNo);
		param.put("msgType", "CSERVICE");
		param.put("message", message);
		return chatDao.insertMessage(param);
	}

	public int insertCclass(Map<String, Object> param) {
		param.put("msgType", "CCLASS");
		return chatDao.insertMessage(param);
	}


	public List<ChatRoomDto> getAllCserviceRooms() {
		return chatDao.getAllCserviceRooms();
	}

}
