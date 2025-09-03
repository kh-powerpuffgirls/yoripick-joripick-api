package com.kh.ypjp.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChatDto {
    
    @Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatMsgDto {
    	private String text;
    	private String sender;
	}
    
    @Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChatRoomDto {
    	private Long classNo;
        private String className;
        private String type;
	}
}
