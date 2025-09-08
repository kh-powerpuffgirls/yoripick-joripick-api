package com.kh.ypjp.chat.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessageDto;
import com.kh.ypjp.chat.model.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService chatService;

	@MessageMapping("/{roomNo}")
	public void sendMessage(@DestinationVariable Long roomNo, MessageDto message) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
		System.out.println(json);
		messagingTemplate.convertAndSend("/topic/" + roomNo, message);
	}

	@GetMapping("/rooms/{userNo}")
	public ResponseEntity<List<ChatRoomDto>> getChatRooms(@PathVariable Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		List<ChatRoomDto> chatRoomList = chatService.getUserChatLists(userNo);
		if (chatRoomList != null) {
			return ResponseEntity.ok().body(chatRoomList); // 200
		}
		return ResponseEntity.notFound().build(); // 404
	}

	@DeleteMapping("/rooms/{type}/{userNo}")
	public ResponseEntity<ChatRoomDto> deleteChatRooms(@PathVariable String type, @PathVariable Long userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		ChatRoomDto chatRoom = null;
		if (type.equals("cservice")) {
			chatService.deleteFaqChat(userNo);
			chatRoom = new ChatRoomDto(0L, "FAQ BOT, 요픽", "cservice", new ArrayList<>());
		}
		if (type.equals("admin")) {
			chatService.deleteAdminChat(userNo);
			Long csNo = chatService.newCservice(userNo);
			chatRoom = new ChatRoomDto(csNo, "관리자 문의하기", "admin", new ArrayList<>());
		}
		if (chatRoom != null) {
			return ResponseEntity.ok().body(chatRoom); // 201
		}
		return ResponseEntity.badRequest().build(); // 400
	}

	@PostMapping("/messages/{type}/{classNo}")
	public ResponseEntity<Void> insertMessage(@PathVariable Long classNo, @PathVariable String type,
			@RequestBody FaqMsgDto message) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
		System.out.println(json);
		
		Long userNo = message.getUserNo();
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		message.setUserNo(userNo);
		int result = 0;
		if (type.equals("cservice")) {
			if (message.getUsername().equals("요픽")) {
				message.setUsername("BOT");
			} else {
				message.setUsername("USER");
			}
			result = chatService.insertChatBot(message);
		} else if (type.equals("admin")) {
			Map<String, Object> param = new HashMap<>();
			param.put("roomId", classNo);
			param.put("msgType", "CSERVICE");
			param.put("message", message);
			result = chatService.insertCservice(param);
		} else {
			Map<String, Object> param = new HashMap<>();
			param.put("roomId", classNo);
			param.put("msgType", "CCLASS");
			param.put("message", message);
			result = chatService.insertCclass(param);
		}
		if (result > 0) {
			URI location = URI.create("");
			return ResponseEntity.created(location).build(); // 201
		} else {
			return ResponseEntity.badRequest().build(); // 400
		}
	}
}
