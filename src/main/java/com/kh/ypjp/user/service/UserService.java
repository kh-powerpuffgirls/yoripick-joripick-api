package com.kh.ypjp.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.model.dto.AllergyDto.AllergyList;
import com.kh.ypjp.security.model.dao.AuthDao;
import com.kh.ypjp.security.model.dto.AuthDto;
import com.kh.ypjp.security.model.dto.AuthDto.User;
import com.kh.ypjp.security.model.dto.AuthDto.UserCredential;
import com.kh.ypjp.user.dao.UserDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UtilService utilService;
	private final UserDao userDao;
	private final AuthDao authDao;
	private final PasswordEncoder passwordEncoder;

	public Map<String, Object> updateUserProfile(MultipartFile file, Long userNo) {
		Map<String, Object> result = new HashMap<>();

		Long oldImageNo = userDao.getUserImageNo(userNo);

		String webPath = "profile/" + userNo;

		if (oldImageNo != 0) {
			utilService.deleteFolderIfExists(webPath);
		}

		String changeName = utilService.getChangeName(file, webPath);

		Map<String, Object> param = new HashMap<>();
		param.put("originName", file.getOriginalFilename());
		param.put("serverName", changeName);
		// IMAGE - IMAGE_NO , ORIGIN_NAME ,SERVER_NAME
		utilService.insertImage(param);
		Long newImageNo = utilService.getImageNo(param);
		userDao.updateUserImage(userNo, newImageNo);
		if (newImageNo == null) {
			throw new RuntimeException("이미지 저장 실패");
		}
		if (oldImageNo != 0) {
			userDao.deleteImage(oldImageNo);
		}

		result.put("imageNo", newImageNo);
		result.put("originName", file.getOriginalFilename());
		result.put("serverName", changeName);

		String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("/images/")
				.path(webPath + "/" + changeName).toUriString();

		result.put("url", imageUrl);

		return result;
	}

	public boolean updateUser(Map<String, String> updatePayload) {
		Long userNo = Long.valueOf(updatePayload.get("userNo"));

		int updatedUser = 0;
		int updatedPassword = 0;

		Map<String, Object> param = new HashMap<>();
		param.put("userNo", userNo);
		if (updatePayload.containsKey("username")) {
			param.put("username", updatePayload.get("username"));
		}
		if (updatePayload.containsKey("email")) {
			param.put("email", updatePayload.get("email"));
		}
		if (param.size() > 1) {
			updatedUser = userDao.updateUser(param);
		}

		if (updatePayload.containsKey("newPassword")) {
			String rawPw = updatePayload.get("newPassword");
			String encodedPw = passwordEncoder.encode(rawPw);

			UserCredential credential = new UserCredential();
			credential.setUserNo(userNo);
			credential.setPassword(encodedPw);

			authDao.updatePassword(credential);
			updatedPassword = 1;
		}

		return updatedUser > 0 || updatedPassword > 0;
	}

	public Map<String, Object> getMypageInfo(Long userNo) {
		return userDao.getMypageInfo(userNo);
	}

	public boolean checkPassword(Long userNo, String currentPassword) {
		if (currentPassword == null || currentPassword.trim().isEmpty()) {
			return false;
		}

		AuthDto.User user = authDao.findUserByUserNo(userNo).orElse(null);

		if (user == null || user.getPassword() == null) {
			return false;
		}

		return passwordEncoder.matches(currentPassword, user.getPassword());
	}

	@Transactional
	public Map<String, Object> updateAlarmSettings(Long userNo, User alarms) {
	    Map<String, Object> param = new HashMap<>();
	    param.put("userNo", userNo);
	    param.put("msgNoti", alarms.getMsgNoti());
	    param.put("replyNoti", alarms.getReplyNoti());
	    param.put("rvwNoti", alarms.getRvwNoti());
	    param.put("expNoti", alarms.getExpNoti());

	    userDao.updateAlarm(param);

	    return userDao.getAlarmSettings(userNo);
	}
		
	public List<AllergyList> getAllergyTree() {
	    List<AllergyList> flatList = userDao.getAllergyList();
	    Map<Integer, AllergyList> map = new HashMap<>();
	    List<AllergyList> roots = new ArrayList<>();

	    for (AllergyList dto : flatList) {
	        dto.setChildren(new ArrayList<>());
	        map.put(dto.getAllergyNo(), dto);
	    }

	    for (AllergyList dto : flatList) {
	        if (dto.getCategory() != null && map.containsKey(dto.getCategory())) {
	        	AllergyList parent = map.get(dto.getCategory());
	            parent.getChildren().add(dto);
	        } else {
	            roots.add(dto);
	        }
	    }

	    return roots;
	}
	
    public List<Long> getUserAllergies(Long userNo) {
        return userDao.getUserAllergies(userNo);
    }
    
    public void updateUserAllergies(Long userNo, List<Long> updatedAllergies) {
        List<Long> existing = userDao.getUserAllergies(userNo);

        for (Long allergyNo : updatedAllergies) {
            if (!existing.contains(allergyNo)) {
                userDao.insertUserAllergy(userNo, allergyNo);
            }
        }

        for (Long allergyNo : existing) {
            if (!updatedAllergies.contains(allergyNo)) {
                userDao.deleteUserAllergy(userNo, allergyNo);
            }
        }
    }

	public boolean inactiveUser(Long userNo) {
		int result = userDao.inactiveUser(userNo);
		
		return result > 0;
	}

	public Optional<User> getUserByUserNo(Long userNo) {
	    User user = userDao.getUserByUserNo(userNo).orElse(null);
	    return Optional.ofNullable(user);
	}

}