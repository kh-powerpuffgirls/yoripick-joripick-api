package com.kh.ypjp.security.model.provider;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTProvider {
	private final Key key;
	private final Key RefreshKey;
	
	public JWTProvider(
			@Value("${jwt.secret}") // 서명에 사용하는 키값 ( application.properties에 값이 저장되어있고 secretBase64에 값이 들어감
			String secretBase64,
			@Value("${jwt.refresh-secret}")
			String refreshSecretBase64
			) {
		byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		
		this.RefreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretBase64));
	}
	
	public String createAccessToken(Long userNo, int minutes) { // id (페이로드에) minutes (만료시간에)
		Date now = new Date();
		return Jwts.builder()
				.setSubject(String.valueOf(userNo)) // 페이로드에 저장할 id
				.setIssuedAt(now) // 토큰 발행시간
				.setExpiration(new Date(now.getTime() + (1000 * 60 * minutes))) // 만료시간
				.signWith(key, SignatureAlgorithm.HS256)  // 서명에 사용할 키 값과, 알고리즘
				.compact(); // 포장해서 전달
		
	}
	
	/*
	 * Refresh Token
	 *  - 유효시간이 짧은 Access Token을 새로 갱신받기 위한 용도의 토큰
	 *  - Access Token보다 훨씬 긴 유효시간을 가지고 있다.
	*/
	
	public String createRefreshToken(Long id, int i) {
		Date now = new Date();
		return Jwts.builder()
				.setSubject(String.valueOf(id)) // 페이로드에 저장할 id
				.setIssuedAt(now) // 토큰 발행시간
				.setExpiration(new Date(System.currentTimeMillis()+(1000*60*60*24*i))) // 만료시간
				.signWith(key, SignatureAlgorithm.HS256)  // 서명에 사용할 키 값과, 알고리즘
				.compact(); // 포장해서 전달
	}
	
	public Long getUserNo(String token) {
		return Long.valueOf(
				Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject()
				);
	}
	
	public Long parseRefresh(String token) {
		return Long.valueOf(
				Jwts.parserBuilder()
					.setSigningKey(RefreshKey)
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject()
				);
	}
}