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
    
    // 커뮤니티,보드,유저 등에서 삭제요청을 할 때 루트 디렉토리 내 폴더삭제 메서드.
    
    public boolean deleteFolderIfExists(String webPath) {
        String projectRoot = System.getProperty("user.dir");
        File folder = new File(projectRoot, "resources/" + webPath);
        return deleteRecursively(folder);
    }

    // 위의 폴더 내 파일들을 삭제하는 메서드(위의 폴더삭제만 요청하면 폴더 내 파일이 있을시 삭제가 안됨)
    
    private boolean deleteRecursively(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File child : files) {
                        deleteRecursively(child);
                    }
                }
            }
            return file.delete();
        }
        return false;
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
	
	public String getChangeName(Long imageNo) {
		return session.selectOne("util.getChangeName", imageNo);
	}

}
