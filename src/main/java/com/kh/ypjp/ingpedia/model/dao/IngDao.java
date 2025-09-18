package com.kh.ypjp.ingpedia.model.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.ing.model.dto.IngDto.IngCodeResponse;
import com.kh.ypjp.ing.model.dto.IngDto.IngResponse;

@Repository
public class IngDao {
	@Autowired
	private SqlSessionTemplate session;

	public List<IngResponse> selectIngs(HashMap<String, Object> param) {
		return session.selectList("ing.selectIngs", param);
	}

	public long selectTotalIngs(HashMap<String, Object> param) {
		return session.selectOne("ing.selectTotalIngs", param);
	}

	public List<IngCodeResponse> selectIngCodes() {
		return session.selectList("ing.selectIngCodes");
	}

}
