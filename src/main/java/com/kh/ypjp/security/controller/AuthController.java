package com.kh.ypjp.security.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.ypjp.security.model.dto.AuthDto.AuthResult;
import com.kh.ypjp.security.model.dto.AuthDto.LoginRequest;
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
   
    @PostMapping("/login")
    public ResponseEntity<AuthResult> login(@RequestBody LoginRequest req) {
        try {
        	System.out.println(req.getEmail());
        	System.out.println(req.getPassword());
            AuthResult result = authService.login(req.getEmail(), req.getPassword());
            ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, result.getRefreshToken())
                .httpOnly(true).secure(false).path("/").sameSite("Lax")
                .maxAge(Duration.ofDays(7)).build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(result);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/enroll")
    public ResponseEntity<AuthResult> enroll(@RequestBody LoginRequest req) {
        AuthResult result = authService.enroll(req.getEmail(),req.getNickname() ,req.getPassword());
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, result.getRefreshToken())
                .httpOnly(true).secure(false).path("/").sameSite("Lax")
                .maxAge(Duration.ofDays(7)).build();
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResult> refresh(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshCookie) {
    	
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
                Long userId = jwt.getUserId(accessToken);
                String kakaoAccessToken = authService.getKakaoAccessToken(userId);
                if (kakaoAccessToken != null) {
                    kakaoService.logout(kakaoAccessToken);
                }
            } catch (Exception e) {
                // 토큰 만료 등 예외 발생 시에도 로그아웃은 계속 진행
            }
        }
        
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, "")
            .httpOnly(true).secure(false).path("/").sameSite("Lax")
            .maxAge(0).build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).build();
    }

//    @GetMapping("/me")
//    public ResponseEntity<User> getUserInfo(HttpServletRequest req) {
//        String jwtToken = resolveAccessToken(req);
//        if (jwtToken == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        Long userId = jwt.getUserId(jwtToken);
//        User user = authService.findUserByUserId(userId);
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(user);
//    }
    
    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PostMapping("/send-code")
    public ResponseEntity<String> sendEmailCode(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일을 입력하세요.");
        }
        emailService.createAndSendCode(email);
        return ResponseEntity.ok("인증번호 전송 완료");
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
}