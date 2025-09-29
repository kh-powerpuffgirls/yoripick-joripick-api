package com.kh.ypjp.security.model.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Optional을 사용하면 null 처리가 용이해집니다.

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.security.model.dto.AuthDto.Authority;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.AuthDto.UserCredential;
import com.kh.ypjp.security.model.dto.AuthDto.UserIdentities;
import com.kh.ypjp.security.model.dto.UserNotiDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthDao {

	private final SqlSessionTemplate session;
	
	public UserNotiDto getNotiByUserNo(String userNo) {
		return session.selectOne("auth.getNotiByUserNo", userNo);
	}

	public Optional<User> findUserByEmail(String email) {
		List<User> users = session.selectList("auth.findUserByEmail", email);
		return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
	}

	public Optional<User> findUserByUserNo(Long userNo) {
		List<User> users = session.selectList("auth.findUserByUserNo", userNo);
		return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
	}

	public void insertUserIdentities(UserIdentities userIdentities) {
		session.insert("auth.insertUserIdentities", userIdentities);
	}

	public void insertUser(User user) {
		session.insert("auth.insertUser", user);
	}

	public void insertCred(UserCredential cred) {
		session.insert("auth.insertCred", cred);
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
	
	public void updateUserStatus(Long userNo, String status) {
	    session.update("auth.updateUserStatus", 
	        Map.of("userNo", userNo, "status", status));
	}

	public Date checkBannedStatus(User user) {
		return session.selectOne("auth.checkBannedStatus", user);
	}
}