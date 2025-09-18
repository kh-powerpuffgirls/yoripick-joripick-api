package com.kh.ypjp.ingpedia.model.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.ypjp.ing.model.dao.IngDao;
import com.kh.ypjp.ing.model.dto.IngDto.IngCodeResponse;
import com.kh.ypjp.ing.model.dto.IngDto.IngResponse;

@Service
public class IngService {
	
	@Autowired
	private IngDao dao;

	public List<IngResponse> selectIngs(HashMap<String, Object> param) {
		return dao.selectIngs(param);
	}

	public long selectTotalIngs(HashMap<String, Object> param) {
		return dao.selectTotalIngs(param);
	}

	public List<IngCodeResponse> selectIngCodes() {
		return dao.selectIngCodes();
	}


}
