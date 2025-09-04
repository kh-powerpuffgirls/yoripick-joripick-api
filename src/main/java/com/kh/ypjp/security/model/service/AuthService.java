package com.kh.ypjp.security.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kh.ypjp.config.SecurityConfig;
import com.kh.ypjp.security.model.dao.AuthDao;
import com.kh.ypjp.security.model.dto.AuthDto.AuthResult;
import com.kh.ypjp.security.model.dto.AuthDto.Authority;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.AuthDto.UserCredential;
import com.kh.ypjp.security.model.provider.JWTProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecurityConfig securityConfig;

    private final AuthDao authDao;
    private final PasswordEncoder encoder;
    private final JWTProvider jwt;
    

    public AuthResult login(String email, String password) {
        User user = authDao.findUserByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("존재하지 않는 이메일입니다."));
        System.out.println(user.getPassword());
        System.out.println(encoder.encode(password));
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        
        String accessToken = jwt.createAccessToken(user.getUserNo(), 30);
        String refreshToken = jwt.createRefreshToken(user.getUserNo(), 7);
        
		User userNoPassword = User.builder()
				.userNo(user.getUserNo())
				.email(user.getEmail())
				.username(user.getUsername())
				.imageNo(user.getImageNo())
				.roles(user.getRoles())
				.build();
        
        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userNoPassword)
                .build();
    }
    
    @Transactional
    public AuthResult enroll(String email, String nickname, String password) {
    	
        authDao.findUserByEmail(email).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        });
        
        authDao.findUserByNickname(nickname).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        });
        String encodedPassword = encoder.encode(password);
        User user = User.builder()
                        .email(email)
                        .username(nickname)  
                        .build();
        authDao.insertUser(user); 

        UserCredential cred = UserCredential.builder()
                                            .userNo(user.getUserNo())
                                            .password(encodedPassword)
                                            .build();
        authDao.insertCred(cred);
        Authority role = Authority.builder()
                .userNo(user.getUserNo())
                .roles(List.of("ROLE_USER")) // 기본 권한 지정
                .build();
        authDao.insertUserRole(role);
        User savedUser = authDao.findUserByUserId(user.getUserNo())
                                .orElseThrow(() -> new IllegalStateException("회원가입 직후 사용자 조회 실패"));

        String accessToken = jwt.createAccessToken(savedUser.getUserNo(), 30);
        String refreshToken = jwt.createRefreshToken(savedUser.getUserNo(), 7);

        return AuthResult.builder()
                         .accessToken(accessToken)
                         .refreshToken(refreshToken)
                         .user(user)
                         .build();
    }
    
    public AuthResult refreshByCookie(String refreshToken) {
        Long userId;
        try {
            userId = jwt.parseRefresh(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.");
        }

        User user = authDao.findUserByUserId(userId)
                .orElseThrow(() -> new BadCredentialsException("사용자 조회 실패"));

        String newAccessToken = jwt.createAccessToken(user.getUserNo(), 30);

        return AuthResult.builder()
                .accessToken(newAccessToken)
                .user(user)
                .build();
    }
    
	public String getKakaoAccessToken(Long userId) {
		return authDao.getKakaoAccessToken(userId);
	}
}