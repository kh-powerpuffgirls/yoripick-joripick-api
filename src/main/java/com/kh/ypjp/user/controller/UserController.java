package com.kh.ypjp.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.ypjp.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;

        @PostMapping("/profile")
        public ResponseEntity<Map<String, Object>> updateProfile(
                @RequestParam MultipartFile file,
                @RequestParam Long userNo) {

            Map<String, Object> res = new HashMap<>();
            try {
                if (file == null || file.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일이 비어있습니다."));
                }
                if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일형식이 옳지 않습니다."));
                }

                Map<String, Object> out = userService.updateUserProfile(file, userNo);

                res.put("success", true);
                res.put("message", "프로필이 변경되었습니다.");
                res.putAll(out);
                return ResponseEntity.ok(res);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "업데이트 실패: " + e.getMessage()));
            }
        }


}
