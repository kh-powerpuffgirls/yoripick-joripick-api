package com.kh.ypjp.chat.model.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.chat.model.dao.ChatDao;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgDto;
import com.kh.ypjp.security.model.dao.AuthDao;
import com.kh.ypjp.security.model.dto.AuthDto.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatDao chatDao;
	private final AuthDao authDao;

	public List<ChatRoomDto> getUserChatLists(Long userNo) {
		List<ChatRoomDto> rooms = chatDao.findCookingClasses(userNo);
		if (rooms != null) {
			for (ChatRoomDto room : rooms) {
				Map<String, Object> param = new HashMap<>();
				param.put("userNo", userNo);
				param.put("refNo", room.getRoomNo());
				param.put("msgType", "CCLASS");
				List<ChatMsgDto> chatMessages = chatDao.getMessagesByRoom(param);
				room.setMessages(new ArrayList<>(chatMessages));
			}
		} else {
			rooms = new ArrayList<>();
		}
		Optional<User> userOpt = authDao.findUserByUserId(userNo);
		List<ChatRoomDto> adminRooms = new ArrayList<>();
		userOpt.ifPresent(user -> {
			if (user.getRoles().contains("ROLE_ADMIN")) {
				adminRooms.addAll(chatDao.getAllCserviceRooms());
			}
		});
		if (adminRooms != null) {
			for (ChatRoomDto room : adminRooms) {
				Map<String, Object> param = new HashMap<>();
				param.put("refNo", room.getRoomNo());
				param.put("msgType", "CSERVICE");
				List<ChatMsgDto> chatMessages = chatDao.getMessagesByRoom(param);
				room.setMessages(new ArrayList<>(chatMessages));
			}
		}
		rooms.addAll(adminRooms);
		if (chatDao.findFaqChat(userNo) > 0) {
			List<FaqMsgDto> faqMessages = chatDao.getFaqByUser(userNo);
			rooms.add(new ChatRoomDto(0L, "FAQ BOT, 요픽", "cservice", new ArrayList<>(faqMessages)));
		}
		Long csNo = chatDao.findAdminChat(userNo);
		if (csNo != null) {
			Map<String, Object> param = new HashMap<>();
			param.put("refNo", csNo);
			param.put("msgType", "CSERVICE");
			List<ChatMsgDto> adminMessages = chatDao.getMessagesByRoom(param);
			rooms.add(new ChatRoomDto(csNo, "관리자 문의하기", "admin", new ArrayList<>(adminMessages)));
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
		chatDao.deleteAdminChatMessage(userNo);
		return chatDao.deleteAdminChatSession(userNo);
	}

	public int insertChatBot(FaqMsgDto message) {
		return chatDao.insertChatBot(message);
	}

	public Long newCservice(Long userNo) {
		Long csNo = null;
		if (chatDao.insertCservice(userNo) > 0) {
			csNo = chatDao.getCsNoByUserNo(userNo);
		}
		return csNo;
	}

	@Transactional
	public int insertCservice(Map<String, Object> param) {
		return chatDao.insertMessage(param);
	}

	public int insertCclass(Map<String, Object> param) {
		return chatDao.insertMessage(param);
	}

}
