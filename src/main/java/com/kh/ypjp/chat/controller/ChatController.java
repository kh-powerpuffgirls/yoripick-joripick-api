package com.kh.ypjp.chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.ypjp.chat.model.dto.ChatDto.ChatRoomDto;
import com.kh.ypjp.chat.model.dto.ChatDto.FaqMsgDto;
import com.kh.ypjp.chat.model.dto.ChatDto.MessageDto;
import com.kh.ypjp.chat.model.service.ChatService;
import com.kh.ypjp.common.UtilService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final UtilService utilService;
	private final ChatService chatService;

	@MessageMapping("/{roomNo}")
	public void sendMessage(@DestinationVariable Long roomNo, MessageDto message) {
		log.debug("mess {} ", message);
		messagingTemplate.convertAndSend("/topic/" + roomNo, message);
	}
	
	@PatchMapping("/reads")
	public ResponseEntity<Void> updateLastRead(
			@RequestParam Long userNo, @RequestParam Long roomNo, @RequestParam Long messageNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		Map <String, Object> param = new HashMap<>();
		param.put("userNo", userNo);
		param.put("roomNo", roomNo);
		param.put("messageNo", messageNo);
		if (chatService.updateLastRead(param) > 0) {
			return ResponseEntity.ok().build(); // 201
		}
		return ResponseEntity.badRequest().build(); // 400
	}
	
	@GetMapping("/reads/{userNo}/{roomNo}")
	public ResponseEntity<Long> getLastRead(
			@PathVariable Long userNo, @PathVariable Long roomNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		Map <String, Object> param = new HashMap<>();
		param.put("userNo", userNo);
		param.put("roomNo", roomNo);
		Long messageNo = chatService.getLastRead(param);
		if (messageNo == null) {
			return ResponseEntity.badRequest().build(); // 400
		}
		return ResponseEntity.ok(messageNo); // 200
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
			chatRoom = new ChatRoomDto(0L, "FAQ BOT, 요픽", "cservice", new ArrayList<>(), null);
		}
		if (type.equals("admin")) {
			chatService.deleteAdminChat(userNo);
			Long csNo = chatService.newCservice(userNo);
			chatRoom = new ChatRoomDto(csNo, "관리자 문의하기", "admin", new ArrayList<>(), null);
		}
		if (chatRoom != null) {
			return ResponseEntity.ok().body(chatRoom); // 201
		}
		return ResponseEntity.badRequest().build(); // 400
	}

	@PostMapping(value = "/messages/{type}/{classNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<? extends MessageDto> insertMessage(@PathVariable Long classNo, @PathVariable String type,
			@RequestPart("message") MessageDto message, @RequestPart(value = "selectedFile", required = false) MultipartFile upfile
			) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
		System.out.println(json);
		
		Long userNo = message.getUserNo();
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401
		}
		if (!(upfile == null || upfile.isEmpty())) {
			String webPath = "messages/" + userNo;
			String changeName = utilService.getChangeName(upfile, webPath);
			Map<String, Object> param = new HashMap<>();
			String serverName = webPath + "/" + changeName;
			param.put("serverName", serverName);
			param.put("originName", upfile.getOriginalFilename());
			int result = utilService.insertImage(param);
			if (result > 0) {
				message.setImageNo(utilService.getImageNo(param));
				String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
		                .path("/images/")
		                .path(serverName)
		                .toUriString();
				message.setContent(imageUrl);
			} else {
				return ResponseEntity.badRequest().build(); // 400
			}
		}
		int result = 0;
		message.setUserNo(userNo);
		message.setRoomNo(classNo);
		if (type.equals("cservice")) {
			if (message.getUsername().equals("요픽")) {
				message.setUsername("BOT");
			} else {
				message.setUsername("USER");
			}
			FaqMsgDto faqMessage = new FaqMsgDto();
			faqMessage.setUserNo(message.getUserNo());
			faqMessage.setUsername(message.getUsername());
			faqMessage.setContent(message.getContent());
			result = chatService.insertChatBot(faqMessage);
		} else if (type.equals("admin")) {
			message.setMsgType("CSERVICE");
			result = chatService.insertMessage(message);
		} else {
			message.setMsgType("CCLASS");
			result = chatService.insertMessage(message);
		}
		if (result > 0) {
			return ResponseEntity.ok().body(message); // 201
		} else {
			return ResponseEntity.badRequest().build(); // 400
		}
	}
}
