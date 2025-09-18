package com.kh.ypjp.mying.model.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngPost;
import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngPut;
import com.kh.ypjp.mying.model.dto.MyIngDto.MyIngResponse;

@Repository
public class MyIngDao {
	@Autowired
	private SqlSessionTemplate session;

	public List<MyIngResponse> selectMyIngs(HashMap<String, Object> param) {
		return session.selectList("mying.selectMyIngs", param);
	}

	public MyIngResponse selectMyIngDetail(HashMap param) {
		return session.selectOne("mying.selectMyIngDetail", param);
	}

	public int updateMying(MyIngPut mying) {
		return session.update("mying.updateMying", mying);
	}

	public int deleteMying(HashMap param) {
		return session.delete("mying.deleteMying", param);
	}

	public int insertMying(MyIngPost mying) {
		return session.insert("mying.insertMying", mying);
	}

}
