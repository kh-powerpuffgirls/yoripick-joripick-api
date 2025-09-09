package com.kh.ypjp.security.model.dao;

import java.util.List;
import java.util.Optional; // Optional을 사용하면 null 처리가 용이해집니다.

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.security.model.dto.AuthDto.Authority;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.AuthDto.UserCredential;
import com.kh.ypjp.security.model.dto.AuthDto.UserIdentities;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthDao {

	private final SqlSessionTemplate session;

	// Optional<User>를 반환하여 Service 계층에서 null 여부를 명시적으로 처리하도록 유도
	public Optional<User> findUserByEmail(String email) {
		// selectOne 대신 selectList를 사용하여 여러 권한을 가진 경우에도 오류가 나지 않도록 함
		List<User> users = session.selectList("auth.findUserByEmail", email);
		// 결과 리스트가 비어있지 않으면 첫 번째 사용자를 반환
		return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
	}

	public Optional<User> findUserByUserNo(Long userNo) {
		List<User> users = session.selectList("auth.findUserByUserNo", userNo);
		return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
	}
	
	// 이하 코드는 기존과 동일하여 생략...
	public void insertUserIdentities(UserIdentities userIdentities) {
		session.insert("auth.insertUserIdentities", userIdentities);
	}
	
    public void insertUser(User user) {
        session.insert("auth.insertUser",user);
    }
    
    public void insertCred(UserCredential cred) {
        session.insert("auth.insertCred",cred);
    }
    
    public void insertUserRole(Authority role) {
        session.insert("auth.insertUserRole", role);
    }

	public void updatePassword(UserCredential credential) {
	    session.update("auth.updatePassword", credential);
	}

	public void updateUserIdentities(UserIdentities userIdentities) {
		session.update("auth.updateUserIdentities", userIdentities);
	}
	
	public Optional<User> findUserByUsername(String username) {
	    List<User> users = session.selectList("auth.findUserByUsername", username);
	    return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
	}

	public String getKakaoAccessToken(Long userNo) {
		return session.selectOne("auth.getKakaoAccessToken", userNo);
	}
}