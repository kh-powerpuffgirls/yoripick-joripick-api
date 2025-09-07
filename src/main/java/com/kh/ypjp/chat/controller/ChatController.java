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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgResDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessageDto;
import com.kh.ypjp.chat.model.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService chatService;
	
	@MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, MessageDto message) {
	    int result = 0;
		if (roomId == -1) {
			result = chatService.insertCservice(message);
		} else {
			Map<String, Object> param = new HashMap<>();
			param.put("roomId", roomId);
			param.put("message", message);
			result = chatService.insertCclass(param);
		}
		if (result > 0) {
			messagingTemplate.convertAndSend("/topic/" + roomId, message);
		}
    }
	
	@GetMapping("/admin/{userNo}")
    public ResponseEntity<List<ChatRoomDto>> getAdminCserviceRooms(@PathVariable Long userNo) {
		// userNo가 관리자인지 체크
		if (userNo == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //401
	    }
		List<ChatRoomDto> chatRoomList = chatService.getAllCserviceRooms();
		System.out.println(chatRoomList);
		if (chatRoomList != null) {
		    return ResponseEntity.ok().body(chatRoomList); //200
		}
		return ResponseEntity.notFound().build(); //404
    }
	
	@GetMapping("/rooms/{userNo}")
    public ResponseEntity<List<ChatRoomDto>> getChatRooms(@PathVariable Long userNo) {
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
		if (type.equals("admin")) {
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
