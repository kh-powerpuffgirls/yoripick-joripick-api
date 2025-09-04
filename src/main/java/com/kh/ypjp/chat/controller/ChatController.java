package com.kh.ypjp.chat.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import com.kh.ypjp.security.model.provider.JWTProvider;

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

import com.kh.ypjp.chat.model.dto.ChatDto.ChatMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgResDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessageDto;
import com.kh.ypjp.chat.model.service.ChatService;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final JWTProvider jwt;
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
		    for (ChatRoomDto room : chatRoomList) {
		        System.out.println("==== Room ====");
		        System.out.println("classNo=" + room.getClassNo());
		        System.out.println("className=" + room.getClassName());
		        System.out.println("type=" + room.getType());
		        if (room.getMessages() != null) {
		            for (MessageDto m : room.getMessages()) {
		                if (m instanceof FaqMsgResDto) {
		                    FaqMsgResDto fm = (FaqMsgResDto) m;
		                    System.out.println("FAQ -> username=" + fm.getUsername()
		                        + ", content=" + fm.getContent()
		                        + ", createdAt=" + fm.getCreatedAt());
		                } else if (m instanceof ChatMsgDto) {
		                    ChatMsgDto cm = (ChatMsgDto) m;
		                    System.out.println("CHAT -> username=" + cm.getUsername()
		                        + ", content=" + cm.getContent()
		                        + ", time=" + cm.getTime());
		                }
		            }
		        } else {
		            System.out.println("messages=null");
		        }
		    }
		    return ResponseEntity.ok().body(chatRoomList); // ✅ forEach/for문 밖으로 이동
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
			@RequestBody Map<String, Object> param
		){
		if (userNo == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); //401
	    }
		param.put("userNo", userNo);
		int result = chatService.insertChatBot(param);
		if (result > 0) {
			URI location = URI.create("");
			return ResponseEntity.created(location).build(); //201
		} else {
			return ResponseEntity.badRequest().build(); //400
		}
	}
}
