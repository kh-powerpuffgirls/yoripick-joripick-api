package com.kh.ypjp.sbti.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.ypjp.sbti.model.dao.SbtiDao;
import com.kh.ypjp.sbti.model.dto.SbtiDto.SikBti;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SbtiService {
	
	private final SbtiDao dao;
	
	public List<SikBti> getScoreMap() {
		return dao.getScoreMap();
	}

	public int eatbtiResult(Map<String, Object> param) {
		return dao.eatbtiResult(param);
	}

}
