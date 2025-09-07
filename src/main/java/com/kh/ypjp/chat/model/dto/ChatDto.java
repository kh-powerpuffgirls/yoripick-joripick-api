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
        private Long userNo;
        private String content;
        private Date createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMsgDto extends MessageDto {
        private Long messageNo;
        private String msgType;
        private Long refNo;
        private Date createdAt;
        private String hidden;
        private Long imageNo;
        private Long userNo;
        private String username;
        private String content;
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
        
        public FaqMsgDto(Long userNo, String content, String linkUrl, Date createdAt) {
            super(userNo, content, createdAt);
            this.button = new Button(linkUrl);
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaqMsgResDto extends FaqMsgDto {
        private String username;
        
        public FaqMsgResDto(Long userNo, String username, String content, String linkUrl, Date createdAt) {
            super(userNo, content, linkUrl, createdAt);
            this.username = username;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomDto {
        private Long classNo;
        private String className;
        private String type;
        private List<? extends MessageDto> messages;
    }

}
