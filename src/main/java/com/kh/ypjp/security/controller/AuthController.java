package com.kh.ypjp.security.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.security.model.dto.AuthDto.AuthResult;
import com.kh.ypjp.security.model.dto.AuthDto.LoginRequest;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.UserNotiDto;
import com.kh.ypjp.security.model.provider.JWTProvider;
import com.kh.ypjp.security.model.service.AuthService;
import com.kh.ypjp.security.model.service.EmailService;
import com.kh.ypjp.security.model.service.KakaoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final KakaoService kakaoService;
	private final AuthService authService;
	private final JWTProvider jwt;
	private final EmailService emailService;
	public static final String REFRESH_COOKIE = "REFRESH_TOKEN";
	
	@GetMapping("/noti/{userNo}")
	public ResponseEntity<UserNotiDto> getUserNotificationSettings(@PathVariable String userNo) {
		if (userNo == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		UserNotiDto settings = authService.getNotiByUserNo(userNo);
		if (settings != null) {
			return ResponseEntity.ok(settings);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
	    try {
	        AuthResult result = authService.login(req.getEmail(), req.getPassword());

	        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, result.getRefreshToken())
	                .httpOnly(true)
	                .secure(false)
	                .path("/")
	                .sameSite("Lax")
	                .maxAge(Duration.ofDays(7))
	                .build();

	        return ResponseEntity.ok()
	                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
	                .body(result);

	    } catch (BadCredentialsException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of(
	                        "errorCode", "LOGIN_FAILED",
	                        "message", e.getMessage()
	                ));
	    }
	}

	@PostMapping("/enroll")
	public ResponseEntity<?> enroll(@RequestBody LoginRequest req) {
	    try {
	        AuthResult result = authService.enroll(req.getEmail(), req.getUsername(), req.getPassword());
	        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, result.getRefreshToken())
	                .httpOnly(true)
	                .secure(false)
	                .path("/")
	                .sameSite("Lax")
	                .maxAge(Duration.ofDays(7))
	                .build();

	        return ResponseEntity.status(HttpStatus.CREATED)
	                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
	                .body(result);

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body(Map.of("errorCode", "DUPLICATE", "message", e.getMessage()));

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("errorCode", "SERVER_ERROR", "message", "회원가입 처리 중 서버 오류가 발생했습니다."));
	    }
	}

	@PostMapping("/enroll/social")
	public ResponseEntity<AuthResult> enrollSocial(@RequestBody Map<String, String> req) {
		String email = req.get("email");
		String username = req.get("username");
		String provider = req.get("provider");
		String providerUserId = req.get("providerUserId");
		String accessToken = req.get("accessToken");

		AuthResult result = authService.enrollSocial(email, username, provider, providerUserId);

		ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, result.getRefreshToken()).httpOnly(true)
				.secure(false).path("/").sameSite("Lax").maxAge(Duration.ofDays(7)).build();
		return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body(result);
	}
	
	@GetMapping("/check-username")
	public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
	    Optional<User> userOpt = authService.findUserByUsername(username);
	    boolean available = userOpt.isEmpty();
	    Map<String, Boolean> response = new HashMap<>();
	    response.put("available", available);
	    return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResult> refresh(
			@CookieValue(name = REFRESH_COOKIE, required = false) String refreshCookie) {
		System.out.println(refreshCookie);
		if (refreshCookie == null || refreshCookie.isBlank()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		AuthResult result = authService.refreshByCookie(refreshCookie);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		String accessToken = resolveAccessToken(request);
		if (accessToken != null) {
			try {
				Long userNo = jwt.getUserNo(accessToken);
				String kakaoAccessToken = authService.getKakaoAccessToken(userNo);
				if (kakaoAccessToken != null) {
					kakaoService.logout(kakaoAccessToken);
				}
			} catch (Exception e) {
			}
		}

		ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, "").httpOnly(true).secure(false).path("/")
				.sameSite("Lax").maxAge(0).build();
		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).build();
	}

	@GetMapping("/me")
	public ResponseEntity<Optional<User>> getUserInfo(HttpServletRequest req) {
		String jwtToken = resolveAccessToken(req);
		if (jwtToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Long userNo = jwt.getUserNo(jwtToken);
		Optional<User> user = authService.findUserByUserNo(userNo);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}

	private String resolveAccessToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	@PostMapping("/send-code/enroll")
	public ResponseEntity<Map<String, Object>> sendEnrollCode(@RequestBody Map<String, String> req) {
	    String email = req.get("email");
	    Map<String, Object> response = new HashMap<>();

	    if (email == null || email.isEmpty()) {
	        response.put("success", false);
	        response.put("message", "이메일을 입력하세요.");
	        return ResponseEntity.badRequest().body(response);
	    }

	    if (authService.findUserByEmail(email).isPresent()) {
	        response.put("success", false);
	        response.put("message", "중복된 이메일입니다.");
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	    }

	    emailService.createAndSendCode(email);

	    response.put("success", true);
	    response.put("message", "인증번호 전송 완료");
	    return ResponseEntity.ok(response);
	}

	@PostMapping("/send-code/reset")
	public ResponseEntity<Map<String, Object>> sendResetCode(@RequestBody Map<String, String> req) {
	    String email = req.get("email");
	    Map<String, Object> response = new HashMap<>();

	    if (email == null || email.isEmpty()) {
	        response.put("success", false);
	        response.put("message", "이메일을 입력하세요.");
	        return ResponseEntity.badRequest().body(response);
	    }

	    if (authService.findUserByEmail(email).isEmpty()) {
	        response.put("success", false);
	        response.put("message", "존재하지 않는 이메일입니다.");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	    emailService.createAndSendCode(email);

	    response.put("success", true);
	    response.put("message", "인증번호 전송 완료");
	    return ResponseEntity.ok(response);
	}

	@PostMapping("/verify-code")
	public ResponseEntity<Map<String, Boolean>> verifyEmailCode(@RequestBody Map<String, String> req) {
		String email = req.get("email");
		String code = req.get("code");
		boolean verified = emailService.verifyCode(email, code);
		Map<String, Boolean> res = new HashMap<>();
		res.put("verified", verified);
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
	    String email = req.get("email");
	    String newPassword = req.get("password");

	    if (email == null || newPassword == null) {
	        return ResponseEntity.badRequest().body("이메일 또는 비밀번호가 누락되었습니다.");
	    }

	    boolean result = authService.resetPassword(email, newPassword);
	    if (result) {
	        return ResponseEntity.ok("비밀번호 변경 완료");
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 이메일의 사용자를 찾을 수 없습니다.");
	    }
	}
	
}