package com.kh.ypjp.mainpage.model.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.ingpedia.model.dto.IngPediaDto;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngDetailResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngListResponse;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaPost;
import com.kh.ypjp.ingpedia.model.dto.IngPediaDto.IngPediaPut;

@Repository
public class MainPageDao {
	@Autowired
	private SqlSessionTemplate session;

	public IngDetailResponse selectIngPediaDetail(long ingNo) {
		return session.selectOne("ingpedia.selectIngPediaDetail", ingNo);
	}
	
	public List<IngPediaDto.IngPairResponse> selectIngPediaPair(long ingNo) {
		return session.selectList("ingpedia.selectIngPediaPair", ingNo);
	}
	
	public List<IngListResponse> selectIngPagediaList(HashMap<String, Object> param) {
		return session.selectList("ingpedia.selectIngPagediaList", param);
	}
	
	public long selectTotalIngPedia(HashMap<String, Object> param) {
		return session.selectOne("ingpedia.selectTotalIngPedia", param);
	}
	
	public int insertIngMethod(IngPediaPost ingPedia) {
		return session.insert("ingpedia.insertIngMethod", ingPedia);
	}
	public int insertIngPair(IngPediaPost ingPedia) {
		return session.insert("ingpedia.insertIngPair", ingPedia);
	}
	
	@Transactional
	public int updateIngMethod(IngPediaPut ingPedia) {
		
		int result1 = session.update("ingpedia.updateIng", ingPedia);
		int result2 = session.update("ingpedia.updateNutrient", ingPedia);
		int result3 = session.update("ingpedia.updateIngMethod", ingPedia);
		
		return (result1 > 0) && (result2 > 0) && (result3 > 0) ? 1 : 0;
	}

	public int updateIngPair(IngPediaPut ingPedia) {
		return session.update("ingpedia.updateIngPair", ingPedia);
	}

	public int deleteIngPairs(IngPediaPut ingPedia) {
		int result = 1;
		System.out.println("ok...");
		if (session.selectList("ingpedia.selectIngPediaPair", ingPedia.getIngDetail().getIngNo()).size() > 0) {
			result = session.insert("ingpedia.deleteIngPairs", ingPedia);
			System.out.println("작동하면 안 됨");
		}
		return result;
	}
	
	public int deleteIngPair(long ingNo) {
		return session.insert("ingpedia.deleteIngPair", ingNo);
	}

	public int deleteIngMethod(long ingNo) {
		return session.insert("ingpedia.deleteIngMethod", ingNo);
	}
}
