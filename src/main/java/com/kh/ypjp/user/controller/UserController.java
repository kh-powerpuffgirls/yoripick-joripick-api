package com.kh.ypjp.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.common.exception.AuthException;
import com.kh.ypjp.model.dto.AllergyDto;
import com.kh.ypjp.model.dto.AllergyDto.AllergyList;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.service.AuthService;
import com.kh.ypjp.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class UserController {

	private final AuthService authService;

	private final UserService userService;
	private final UtilService utilService;

	@PostMapping("/users/profiles")
	public ResponseEntity<String> getProfileImageUrl(@RequestBody User user) {
		Long imageNo = user.getImageNo();
		System.out.println(imageNo);
		String changeName = utilService.getChangeName(imageNo);
		String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/")
				.path("profile/" + user.getUserNo() + "/" + changeName).toUriString();
		return ResponseEntity.ok(imageUrl);
	}

	@PostMapping("/profile")
	public ResponseEntity<Map<String, Object>> updateProfile(@RequestParam MultipartFile file,
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

	@PutMapping("/update")
	public ResponseEntity<?> updateUserInfo(@RequestBody Map<String, String> updatePayload) {

		try {
			String username = updatePayload.get("username");
			String email = updatePayload.get("email");
			String newPassword = updatePayload.get("newPassword");
			String currentPassword = updatePayload.get("currentPassword");
			String userNo = updatePayload.get("userNo");

			if (username != null) {
				authService.validateUsername(username);
			}
			if (email != null) {
				authService.validateEmail(email);
			}
			if (newPassword != null) {
				authService.validatePassword(newPassword);

				boolean valid = userService.checkPassword(Long.parseLong(userNo), currentPassword);
				if (!valid) {
					return ResponseEntity.badRequest().body(Map.of("errorCode", "INVALID_CURRENT_PASSWORD"));
				}
			}

			boolean success = userService.updateUser(updatePayload);

			if (success) {
				return ResponseEntity.ok(Map.of("message", "회원정보가 성공적으로 수정되었습니다."));
			} else {
				return ResponseEntity.badRequest().body(Map.of("errorCode", "UPDATE_FAILED"));
			}
		} catch (AuthException e) {
			return ResponseEntity.badRequest().body(Map.of("errorCode", e.getErrorCode()));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Map.of("errorCode", "INTERNAL_SERVER_ERROR"));
		}
	}
	
	@PutMapping("/users/alarm")
	public ResponseEntity<?> updateAlarmSettings(@RequestBody User alarmRequest) {
	    try {
	        Map<String, Object> updated = userService.updateAlarmSettings(alarmRequest.getUserNo(), alarmRequest);
	        return ResponseEntity.ok(updated);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("알림 설정 업데이트 실패");
	    }
	}
	
    @GetMapping("/users/allergy-list")
    public ResponseEntity<List<AllergyList>> getAllergyList() {
        try {
            List<AllergyList> allergyTree = userService.getAllergyTree();
            return ResponseEntity.ok(allergyTree);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/users/allergy")
    public ResponseEntity<List<Long>> getUserAllergyNos(@RequestParam Long userNo) {
        List<Long> allergyNos = userService.getUserAllergies(userNo);
        return ResponseEntity.ok(allergyNos);
    }
    
    @PostMapping("/update/users/allergy")
    public ResponseEntity updateUserAllergy(@RequestBody Map<String, Object> body) {
        Long userNo = ((Number) body.get("userNo")).longValue();
        List<Integer> allergyNos = (List<Integer>) body.get("allergyNos");

        userService.updateUserAllergies(userNo, allergyNos.stream()
                .map(Long::valueOf)
                .toList());

        return ResponseEntity.ok().build();
    }
}
