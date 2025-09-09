package com.kh.ypjp.security.model.service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // 이메일별 인증번호와 생성시간 저장 (코드와 생성시간을 함께 저장)
    private final Map<String, CodeEntry> emailCodeMap = new ConcurrentHashMap<>();

    private static final long CODE_EXPIRATION_MILLIS = 5 * 60 * 1000;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 인증번호 생성 및 이메일 발송
    public String createAndSendCode(String email) {
        String code = generateCode();
        emailCodeMap.put(email, new CodeEntry(code, Instant.now().toEpochMilli()));
        sendEmail(email, code);
        return code;
    }

    // 인증번호 검증
    public boolean verifyCode(String email, String code) {
        CodeEntry entry = emailCodeMap.get(email);
        if (entry == null) return false;

        long now = Instant.now().toEpochMilli();
        if (now - entry.getCreatedAt() > CODE_EXPIRATION_MILLIS) {
            emailCodeMap.remove(email);
            return false;
        }

        if (entry.getCode().equals(code)) {
            emailCodeMap.remove(email);
            return true;
        }
        return false;
    }

    private String generateCode() {
        Random random = new Random();
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);
    }

    private void sendEmail(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("요리픽조리픽 회원가입 인증번호");
            message.setText("인증번호는 [ " + code + " ] 입니다. (5분 이내 사용해주세요)");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void removeExpiredCodes() {
        long now = Instant.now().toEpochMilli();
        emailCodeMap.entrySet().removeIf(entry -> now - entry.getValue().getCreatedAt() > CODE_EXPIRATION_MILLIS);
    }

    private static class CodeEntry {
        private final String code;
        private final long createdAt;

        public CodeEntry(String code, long createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }

        public String getCode() {
            return code;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }
}