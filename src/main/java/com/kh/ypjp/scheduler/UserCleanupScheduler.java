package com.kh.ypjp.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.ypjp.user.dao.UserDao;

@Component
public class UserCleanupScheduler {

    private final UserDao userDao;

    public UserCleanupScheduler(UserDao userDao) {
        this.userDao = userDao;
    }

//    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteInactiveUsers() {
        int deleted = userDao.deleteInactiveUsersOver90Days();
        System.out.println("삭제된 비활성 유저 수: " + deleted);
    }
}
