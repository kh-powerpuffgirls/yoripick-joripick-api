package com.kh.ypjp.mying.model.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.ypjp.mying.model.dao.MyIngDao;
import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngPut;
import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngResponse;

@Service
public class MyIngService {
	
	@Autowired
	private MyIngDao dao;

	public List<MyIngResponse> selectMyIngs(HashMap<String, Object> param) {
		return dao.selectMyIngs(param);
	}

	public MyIngResponse selectMyIngDetail(HashMap param) {
		return dao.selectMyIngDetail(param);
	}

	public int updateMying(MyIngPut mying) {
		return dao.updateMying(mying);
	}

	public int deleteMying(HashMap param) {
		return dao.deleteMying(param);
	}

}
