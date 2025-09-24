package com.kh.ypjp.common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilService {
	
    private final SqlSession session;
    
    public PageInfo getPageInfo(Long listCount, int currentPage, int pageLimit, int itemLimit) {
    	PageInfo pi = new PageInfo();
		pi.setListCount(listCount);
		pi.setCurrentPage(currentPage);
		pi.setPageLimit(pageLimit);
		pi.setItemLimit(itemLimit);
		int maxPage = (int)Math.ceil((double)listCount / (double)itemLimit);
		int startPage = (currentPage - 1) / pageLimit * pageLimit + 1;
		int endPage = startPage + pageLimit - 1;
		if (endPage > maxPage) {
			endPage = maxPage;
		}
		pi.setStartPage(startPage);
		pi.setEndPage(endPage);
		pi.setMaxPage(maxPage);
		return pi;
    }
	
	public String getChangeName(MultipartFile upfile, String webPath) {
		// webPath 예시: ".../{userNo}"
		String projectRoot = System.getProperty("user.dir");
		File dir = new File(projectRoot, "resources/"+webPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String originName = upfile.getOriginalFilename();
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		int random = (int)(Math.random() * 90000 + 10000);
		String ext = originName.substring(originName.lastIndexOf("."));
		String changeName = currentTime + random + ext;
		try {
			upfile.transferTo(new File(dir, changeName));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return changeName;
	}
	
	public int insertImage(Map<String, Object> param) {
		return session.insert("util.insertImage", param);
	}

	public Long getImageNo(Map<String, Object> param) {
		return session.selectOne("util.getImageNo", param);
	}
	
	public String getServerName(Long imageNo) {
		return session.selectOne("util.getServerName", imageNo);
	}

}
