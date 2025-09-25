package com.kh.ypjp.mainpage.model.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.ypjp.ing.model.dao.IngDao;
import com.kh.ypjp.ing.model.dto.IngDto.IngCodeResponse;
import com.kh.ypjp.ing.model.dto.IngDto.IngResponse;
import com.kh.ypjp.ingpedia.model.dao.IngPediaDao;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngDetailResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngListResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPairResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaPost;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaPut;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaResponse;

@Service
public class MainPageService {
	
	@Autowired
	private IngPediaDao dao;

	public IngDetailResponse selectIngPediaDetail(long ingNo) {
		return dao.selectIngPediaDetail(ingNo);
	}

	public List<IngPairResponse> selectIngPediaPair(long ingNo) {
		return dao.selectIngPediaPair(ingNo);
	}

	public List<IngListResponse> selectIngPagediaList(HashMap<String, Object> param) {
		return dao.selectIngPagediaList(param);
	}

	public long selectTotalIngPedia(HashMap<String, Object> param) {
		return dao.selectTotalIngPedia(param);
	}

	public int insertIngMethod(IngPediaPost ingPedia) {
		return dao.insertIngMethod(ingPedia);
	}

	public int insertIngPair(IngPediaPost ingPedia) {
		return dao.insertIngPair(ingPedia);
	}
	
	public int deleteIngMethod(long ingNo) {
		return dao.deleteIngMethod(ingNo);
	}

	public int deleteIngPair(long ingNo) {
		return dao.deleteIngPair(ingNo);
	}

	public int updateIngMethod(IngPediaPut ingPedia) {
		return dao.updateIngMethod(ingPedia);
	}

	public int updateIngPair(IngPediaPut ingPedia) {
		return dao.updateIngPair(ingPedia);
	}

	public int deleteIngPairs(IngPediaPut ingPedia) {
		return dao.deleteIngPairs(ingPedia);
	}

	
}
