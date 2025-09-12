package com.kh.ypjp.chat.model.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChatDto {

	@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto {
		private Long messageNo;
        private Long userNo;
        private String content;
        private Date createdAt;
        private Long roomNo;
        private String username;
        private Long imageNo;
        private String msgType;
        
        public MessageDto(Long userNo, String content, Date createdAt, Long roomNo, String username) {
        	this.userNo = userNo;
        	this.content = content;
        	this.createdAt = createdAt;
        	this.roomNo = roomNo;
        	this.username = username;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMsgDto extends MessageDto {
        private Long refNo;
        private String hidden;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaqMsgDto extends MessageDto {
        private Button button;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Button {
            private String linkUrl;
        }
        
        public FaqMsgDto(Long userNo, String username, String content, 
        		String linkUrl, Date createdAt, Long roomNo) {
            super(userNo, content, createdAt, roomNo, username);
            this.button = new Button(linkUrl);
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomDto {
        private Long roomNo;
        private String className;
        private String type;
        private List<? extends MessageDto> messages;
        private String notification;
    }

}
