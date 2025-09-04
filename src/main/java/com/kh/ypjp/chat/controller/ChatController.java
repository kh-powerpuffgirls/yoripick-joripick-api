package com.kh.ypjp.chat.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessagePost;
import com.kh.ypjp.chat.model.service.ChatService;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") 
public class ChatController {
	
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService chatService;

	public ChatController(
			SimpMessagingTemplate messagingTemplate,
			ChatService chatService
		) {
		this.messagingTemplate = messagingTemplate;
		this.chatService = chatService;
    }
	
	@MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMsgDto message) {
		// cclass save
		messagingTemplate.convertAndSend("/topic/" + roomId, message);
    }
	
	@GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getChatRooms(
    		@CookieValue(name = "userNo", required = false) Long userNo
    	) {
		if (userNo == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //401
	    }
		List<ChatRoomDto> chatRoomList = chatService.getUserChatLists(userNo);
		if (chatRoomList != null) {
			return ResponseEntity.ok().body(chatRoomList); //200
		}
		return ResponseEntity.notFound().build(); //404
    }
	
	@DeleteMapping("/rooms/{type}")
	public ResponseEntity<Void> deleteChatRooms(
			@PathVariable String type, 
			@CookieValue(name = "userNo", required = false) Long userNo
		) {
		if (userNo == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //401
	    }
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
	
	@PostMapping("/messages/{type}/{roomId}")
	public ResponseEntity<Void> insertMessage(
			@PathVariable String type,
			@PathVariable Long roomId,
			@RequestBody MessagePost message,
			@CookieValue(name = "userNo", required = false) Long userNo
		){
		if (userNo == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //401
	    }
		Map<String, Object> param = new HashMap<>();
		param.put("message", message);
		param.put("userNo", userNo);
		param.put("msgType", type);
		int result = chatService.insertMessage(param);
		if (result > 0) {
			URI location = URI.create("");
			return ResponseEntity.created(location).build(); //201
		} else {
			return ResponseEntity.badRequest().build(); //400
		}
	}
}
