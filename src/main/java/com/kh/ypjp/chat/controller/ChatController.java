package com.kh.ypjp.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.service.ChatService;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") 
public class ChatController {
	
	private final ChatService chatService;

	public ChatController(ChatService chatService) {
    	this.chatService = chatService;
    }
	
	@GetMapping("/rooms/{userNo}")
    public ResponseEntity<List<ChatRoomDto>> getChatRooms(@PathVariable Long userNo) {
		List<ChatRoomDto> chatRoomList = chatService.getUserChatLists(userNo);
		if (chatRoomList != null) {
			return ResponseEntity.ok().body(chatRoomList); //200
		}
		return ResponseEntity.notFound().build(); //404
    }
	
	@DeleteMapping("/rooms/{type}/{userNo}")
	public ResponseEntity<Void> deleteChatRooms(@PathVariable String type, @PathVariable Long userNo) {
		int result = 0;
		if (type.equals("cservice")) {
			result = chatService.deleteFaqChat(userNo);
		}
		if (type.equals("chat")) {
			result = chatService.deleteAdminChat(userNo);
		}
		if (result > 0) {
			return ResponseEntity.noContent().build(); //204
		}
		return ResponseEntity.notFound().build(); //404
	}
}
