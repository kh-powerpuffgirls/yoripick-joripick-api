package com.kh.ypjp.chat.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

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
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgResDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessageDto;
import com.kh.ypjp.chat.model.service.ChatService;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService chatService;
	
	@MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, MessageDto message) {
		// type이 cclass인지 admin인지 확인해서 db에 저장하는 코드 추가해야 함
		messagingTemplate.convertAndSend("/topic/" + roomId, message);
    }
	
	@GetMapping("/rooms/{userNo}")
    public ResponseEntity<List<ChatRoomDto>> getChatRooms(
    		@PathVariable Long userNo
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
	
	@DeleteMapping("/rooms/{type}/{userNo}")
	public ResponseEntity<Void> deleteChatRooms(
			@PathVariable String type, 
			@PathVariable Long userNo
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
	
	@PostMapping("/messages/{userNo}")
	public ResponseEntity<Void> insertMessage(
			@PathVariable Long userNo,
			@RequestBody FaqMsgResDto message
		){
		if (userNo == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //401
	    }
		message.setUserNo(userNo);
		int result = chatService.insertChatBot(message);
		if (result > 0) {
			URI location = URI.create("");
			return ResponseEntity.created(location).build(); //201
		} else {
			return ResponseEntity.badRequest().build(); //400
		}
	}
}
