package com.kh.ypjp.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.kh.ypjp.chat.model.dto.ChatDto;

@Controller
public class ChatMsgController {
	
	private final SimpMessagingTemplate messagingTemplate;
	
	public ChatMsgController(SimpMessagingTemplate messagingTemplate) {
    	this.messagingTemplate = messagingTemplate;
    }
	
	@MessageMapping("/cclass/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatDto.ChatMsgDto message) {
		messagingTemplate.convertAndSend("/topic/cclass/" + roomId, message);
    }
}
