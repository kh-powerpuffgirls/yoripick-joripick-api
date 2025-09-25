package com.kh.ypjp.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.kh.ypjp.common.exception.MypageException;
import com.kh.ypjp.model.dto.AllergyDto;
import com.kh.ypjp.model.dto.AllergyDto.AllergyList;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.service.AuthService;
import com.kh.ypjp.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final AuthService authService;

	private final UserService userService;
	private final UtilService utilService;

	@PostMapping("/profiles")
	public ResponseEntity<String> getProfileImageUrl(@RequestBody User user) {
		Long imageNo = user.getImageNo();
		System.out.println(imageNo);
		String changeName = utilService.getChangeName(imageNo);
		String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/")
				.path("profile/" + user.getUserNo() + "/" + changeName).toUriString();
		return ResponseEntity.ok(imageUrl);
	}

	@GetMapping("/profile/{userNo}")
	public ResponseEntity<?> getUserProfile(@PathVariable Long userNo) {
		return userService.getUserByUserNo(userNo).map(user -> {
			if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
				return ResponseEntity.status(HttpStatus.GONE).body(Map.of("success", false, "message", "탈퇴한 회원입니다."));
			}

			Map<String, Object> body = new HashMap<>();
			body.put("success", true);
			body.put("userNo", user.getUserNo());
			body.put("username", user.getUsername());
			body.put("sikbti", user.getSikbti());
			body.put("profile", user.getProfile());
			body.put("provider", user.getProvider());
			return ResponseEntity.ok(body);
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("success", false, "message", "유저를 찾을 수 없습니다.")));
	}

	@PostMapping("/another-profile")
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
			throw new MypageException("UPLOAD_FAILED");
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

	@PutMapping("/alarm")
	public ResponseEntity<?> updateAlarmSettings(@RequestBody User alarmRequest) {
		try {
			Map<String, Object> updated = userService.updateAlarmSettings(alarmRequest.getUserNo(), alarmRequest);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("알림 설정 업데이트 실패");
		}
	}

	@GetMapping("/allergy-list")
	public ResponseEntity<List<AllergyList>> getAllergyList() {
		try {
			List<AllergyList> allergyTree = userService.getAllergyTree();
			return ResponseEntity.ok(allergyTree);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/allergy")
	public ResponseEntity<List<Long>> getUserAllergyNos(@RequestParam Long userNo) {
		List<Long> allergyNos = userService.getUserAllergies(userNo);
		return ResponseEntity.ok(allergyNos);
	}

	@PostMapping("/update/allergy")
	public ResponseEntity updateUserAllergy(@RequestBody Map<String, Object> body) {
		Long userNo = ((Number) body.get("userNo")).longValue();
		List<Integer> allergyNos = (List<Integer>) body.get("allergyNos");

		userService.updateUserAllergies(userNo, allergyNos.stream().map(Long::valueOf).toList());

		return ResponseEntity.ok().build();
	}

	@PostMapping("/inactive")
	public ResponseEntity<Void> inactiveUser(@RequestBody User user) {
		boolean result = userService.inactiveUser(user.getUserNo());
		if (result) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/{userNo}/recipes")
	public ResponseEntity<?> getUserRecipes(@PathVariable Long userNo) {
		try {
			List<Map<String, Object>> recipes = userService.getUserRecipes(userNo);
			return ResponseEntity.ok(recipes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "레시피 조회 실패"));
		}
	}

	@GetMapping("/{userNo}/likes")
	public ResponseEntity<?> getUserLikedRecipes(@PathVariable Long userNo) {
		try {
			List<Map<String, Object>> likedRecipes = userService.getUserLikedRecipes(userNo);
			return ResponseEntity.ok(likedRecipes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "찜한 레시피 조회 실패"));
		}
	}
}
