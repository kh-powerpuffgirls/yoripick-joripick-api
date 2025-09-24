package com.kh.ypjp.sbti.model.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.sbti.model.dto.SbtiDto.SikBti;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SbtiDao {
	
	private final SqlSessionTemplate session;

	public List<SikBti> getScoreMap() {
		return session.selectList("sbti.getScoreMap");
	}

	public int eatbtiResult(Map<String, Object> param) {
		return session.update("sbti.eatbtiResult", param);
	}
	
}
