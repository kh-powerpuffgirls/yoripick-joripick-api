package com.kh.ypjp.security.controller;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.common.exception.AuthException;
import com.kh.ypjp.security.model.dto.AuthDto.AuthResult;
import com.kh.ypjp.security.model.dto.AuthDto.LoginRequest;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.UserNotiDto;
import com.kh.ypjp.security.model.provider.JWTProvider;
import com.kh.ypjp.security.model.service.AuthService;
import com.kh.ypjp.security.model.service.EmailService;
import com.kh.ypjp.security.model.service.KakaoService;
import com.kh.ypjp.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

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
	    String email = req.getEmail();
	    String password = req.getPassword();

	    if (email == null || email.isBlank()) {
	        throw new AuthException("INVALID_EMAIL");
	    }
	    if (password == null || password.isBlank()) {
	        throw new AuthException("INVALID_PASSWORD");
	    }

	    AuthResult result = authService.login(email, password);

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
	}
	
	
	@PostMapping("/users")
	public ResponseEntity<?> enroll(@RequestBody LoginRequest req) {
	    String email = req.getEmail();
	    String username = req.getUsername();
	    String password = req.getPassword();

	    if (email == null || email.isBlank()) {
	        throw new AuthException("INVALID_EMAIL");
	    }
	    if (username == null || username.isBlank()) {
	        throw new AuthException("INVALID_USERNAME");
	    }
	    if (password == null || password.isBlank()) {
	        throw new AuthException("INVALID_PASSWORD");
	    }

	    AuthResult result = authService.enroll(email, username, password);


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
	}

	@PostMapping("/users/social")
	public ResponseEntity<Map<String, String>> enrollSocial(@RequestBody Map<String, String> req,
	                                                        HttpServletResponse response) {
	    String email = req.get("email");
	    String username = req.get("username");
	    String provider = req.get("provider");
	    String providerUserId = req.get("providerUserId");

	    if (email == null || email.isBlank()) {
	        throw new AuthException("INVALID_EMAIL");
	    }
	    if (username == null || username.isBlank()) {
	        throw new AuthException("INVALID_USERNAME");
	    }
	    if (provider == null || provider.isBlank()) {
	        throw new AuthException("INVALID_INPUT");
	    }
	    if (providerUserId == null || providerUserId.isBlank()) {
	        throw new AuthException("INVALID_INPUT");
	    }

	    AuthResult result = authService.enrollSocial(email, username, provider, providerUserId);
	    
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("accessToken", result.getAccessToken()));
	}

	@GetMapping("/users/check")
	public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
	    if (username == null || username.isBlank()) {
	        throw new AuthException("INVALID_USERNAME");
	    }
	    
	    authService.validateUsername(username);
	    
	    Optional<User> userOpt = authService.findUserByUsername(username);
	    boolean available = userOpt.isEmpty();
	    return ResponseEntity.ok(Map.of("available", available));
	}

	@PostMapping("/tokens/refresh")
	public ResponseEntity<AuthResult> refresh(
			@CookieValue(name = REFRESH_COOKIE, required = false) String refreshCookie) {
		System.out.println(refreshCookie);
		if (refreshCookie == null || refreshCookie.isBlank()) {
			throw new AuthException("UNAUTHORIZED");
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
            } catch (Exception ignored) {
            }
        }

		ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, "").httpOnly(true).secure(false).path("/")
				.sameSite("Lax").maxAge(0).build();
		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).build();
	}

    @GetMapping("/users/me")
    public ResponseEntity<User> getUserInfo(HttpServletRequest req) {
        String jwtToken = resolveAccessToken(req);
        if (jwtToken == null) {
            throw new AuthException("UNAUTHORIZED");
        }


	    Long userNo = jwt.getUserNo(jwtToken);
	    return authService.findUserByUserNo(userNo)
	            .map(ResponseEntity::ok)
	            .orElseThrow(() -> new AuthException("EMAIL_NOT_FOUND"));
	}
	
	private String resolveAccessToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}


	@PostMapping("/email-codes")
	public ResponseEntity<Map<String, Object>> sendEnrollCode(@RequestBody Map<String, String> req) {
	    String email = req.get("email");

	    if (email == null || email.isEmpty()) {
	        throw new AuthException("INVALID_EMAIL");
	    }

	    boolean available = authService.findUserByEmail(email).isEmpty();
	    if (!available) {
	        throw new AuthException("EMAIL_ALREADY_EXISTS");
	    }

	    emailService.createAndSendCode(email);

	    return ResponseEntity.ok(Map.of(
	            "success", true,
	            "message", "인증번호 전송 완료",
	            "available", available
	    ));
	}

    @PostMapping("/email-codes/reset")
    public ResponseEntity<Map<String, Object>> sendResetCode(@RequestBody Map<String, String> req) {
        String email = req.get("email");

        if (email == null || email.isEmpty()) {
            throw new AuthException("INVALID_EMAIL");
        }

        if (authService.findUserByEmail(email).isEmpty()) {
            throw new AuthException("EMAIL_NOT_FOUND");
        }

        emailService.createAndSendCode(email);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "인증번호 전송 완료"
        ));
    }

    @PostMapping("/email-codes/verify")
    public ResponseEntity<Map<String, Boolean>> verifyEmailCode(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String code = req.get("code");
        boolean verified = emailService.verifyCode(email, code);
        return ResponseEntity.ok(Map.of("verified", verified));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String newPassword = req.get("password");

        if (email == null || email.isBlank()) {
            throw new AuthException("INVALID_EMAIL");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new AuthException("INVALID_PASSWORD");
        }

        boolean result = authService.resetPassword(email, newPassword);
        if (!result) {
            throw new AuthException("EMAIL_NOT_FOUND");
        }

        return ResponseEntity.ok("비밀번호 변경 완료");
    }

}