package com.kh.ypjp.community.free.service;

import com.kh.ypjp.community.free.dao.FreeDao;
import com.kh.ypjp.community.free.dto.FreeDto;
import com.kh.ypjp.community.free.dto.ReplyDto;
import com.kh.ypjp.community.free.dto.LikesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FreeService {

    private final FreeDao freeDao;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<FreeDto> selectAllBoards() {
        return freeDao.selectAllBoards();
    }

    public FreeDto selectBoardByNo(int boardNo) {
        return freeDao.selectBoardByNo(boardNo);
    }
    
    @Transactional
    public int insertBoard(FreeDto freeDto, MultipartFile file) throws Exception {
        int result = freeDao.insertBoard(freeDto);
        // 이미지 처리 로직은 그대로 유지
        if (result > 0 && file != null && !file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String savedFileName = UUID.randomUUID().toString() + extension;
            String filePath = uploadDir + savedFileName;

            File dest = new File(filePath);
            file.transferTo(dest);

            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("originName", originalFileName);
            imageInfo.put("serverName", savedFileName);
            freeDao.insertImage(imageInfo);
            Integer newImageNo = (Integer) imageInfo.get("imageNo");
            freeDao.updateBoardImageNo(freeDto.getBoardNo(), newImageNo);
        }
        return result;
    }

    @Transactional
    public int updateBoard(FreeDto freeDto, MultipartFile file) {
        int result = freeDao.updateBoard(freeDto);
        // 이미지 처리 로직은 그대로 유지
        try {
            if (file != null && !file.isEmpty()) {
                Integer oldImageNo = freeDao.selectImageNoByBoardNo(freeDto.getBoardNo());
                if (oldImageNo != null) {
                    freeDao.deleteImageByImageNo(oldImageNo);
                }
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null) originalFileName = "unknown_file";
                String extension = originalFileName.contains(".") ?
                        originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
                String savedFileName = UUID.randomUUID().toString() + extension;
                String filePath = uploadDir + File.separator + savedFileName;
                File dest = new File(filePath);
                dest.getParentFile().mkdirs();
                file.transferTo(dest);
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("originName", originalFileName);
                imageInfo.put("serverName", savedFileName);
                freeDao.insertImage(imageInfo);
                Integer newImageNo = (Integer) imageInfo.get("imageNo");
                if (newImageNo != null) {
                    freeDao.updateBoardImageNo(freeDto.getBoardNo(), newImageNo);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("이미지 처리 실패", e);
        }
        return result;
    }

    @Transactional
    public boolean softDeleteBoard(int boardNo, int userNo) {
        FreeDto existingPost = freeDao.selectBoardByNo(boardNo);
          if (existingPost != null && existingPost.getUserNo() == userNo) {
            int result = freeDao.updateBoardDeleteStatus(boardNo, "Y");
            return result > 0;
        }
        return false;
    }

    @Transactional
    public int incrementViews(int boardNo) {
        return freeDao.incrementViews(boardNo);
    }

    @Transactional
    public boolean toggleLike(int boardNo, int userNo) {
        LikesDto likesDto = new LikesDto();
        likesDto.setBoardNo(boardNo);
        likesDto.setUserNo(userNo);
        
        int isLiked = freeDao.checkUserLiked(likesDto);
        
        if (isLiked > 0) {
            freeDao.deleteLike(likesDto);
            return false;
        } else {
            freeDao.insertLike(likesDto);
            return true;
        }
    }

    public int getLikesCount(int boardNo) {
        return freeDao.countLikesByBoardNo(boardNo);
    }

    public List<ReplyDto> selectAllRepliesByBoardNo(int boardNo) {
        return freeDao.selectAllRepliesByBoardNo(boardNo);
    }

    @Transactional
    public int insertReply(ReplyDto replyDto) {
        return freeDao.insertReply(replyDto);
    }

    @Transactional
    public int updateReply(ReplyDto replyDto) {
        if ("REPLY".equals(replyDto.getCategory())) {
            int newRefNo = replyDto.getRefNo();
            int replyNo = replyDto.getReplyNo();
            int circularCount = freeDao.checkCircularReference(replyNo, newRefNo);
            if (circularCount > 0) {
                throw new IllegalStateException("댓글 계층 구조에 순환 참조가 발생하여 댓글을 수정할 수 없습니다.");
            }
        }
        return freeDao.updateReply(replyDto);
    }
    
    public boolean isLiked(int boardNo, int userNo) {
        LikesDto likesDto = new LikesDto();
        likesDto.setBoardNo(boardNo);
        likesDto.setUserNo(userNo);
        int count = freeDao.checkUserLiked(likesDto);
        return count > 0;
    }

    @Transactional
    public boolean deleteReply(Long replyNo, int userNo) {
        ReplyDto reply = freeDao.selectReplyById(replyNo);
        if (reply == null || reply.getUserNo() != userNo) {
            return false;
        }
        int result = freeDao.deleteReply(replyNo);
        return result > 0;
    }
}