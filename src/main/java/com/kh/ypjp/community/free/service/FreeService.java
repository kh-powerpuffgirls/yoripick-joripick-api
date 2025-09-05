package com.kh.ypjp.community.free.service;

import com.kh.ypjp.community.free.dao.FreeDao;
import com.kh.ypjp.community.free.dto.FreeDto;
import com.kh.ypjp.community.free.dto.ReplyDto;
import com.kh.ypjp.community.free.dto.LikesDto;
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
public class FreeService {

    private final FreeDao freeDao;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FreeService(FreeDao freeDao) {
        this.freeDao = freeDao;
    }

    // 게시글 관련 메서드
    public List<FreeDto> selectAllBoards() {
        return freeDao.selectAllBoards();
    }

    public FreeDto selectBoardByNo(int boardNo) {
        return freeDao.selectBoardByNo(boardNo);
    }
    
    @Transactional
    public int insertBoard(FreeDto freeDto, MultipartFile file) throws Exception {
        int result = freeDao.insertBoard(freeDto);

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
            Integer imageNo = (Integer) imageInfo.get("imageNo");
            freeDao.updateBoardImageNo(freeDto.getBoardNo(), imageNo);
        }

        return result;
    }

    // 게시글 수정 로직
    @Transactional
    public int updateBoard(FreeDto freeDto, MultipartFile file) throws Exception {
        int result = freeDao.updateBoard(freeDto);

        if (result > 0 && file != null && !file.isEmpty()) {
            Integer oldImageNo = freeDao.selectImageNoByBoardNo(freeDto.getBoardNo());
            if (oldImageNo != null) {
                freeDao.deleteImageByImageNo(oldImageNo);
            }
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

    public int deleteBoard(int boardNo) {
        return freeDao.deleteBoard(boardNo);
    }
    
    // --- 수정된 부분 시작 ---
    // 조회수 증가 로직을 사용자별 1회만 가능하도록 수정
    @Transactional
    public void incrementViews(int boardNo, int userNo) {
        // 1. 게시글 정보를 가져와서 작성자 ID를 확인합니다.
        FreeDto post = freeDao.selectBoardByNo(boardNo);
        if (post != null && post.getUserNo() == userNo) {
            // 게시글 작성자가 자신의 글을 본 경우에는 조회수를 올리지 않습니다.
            return;
        }
        
        // 2. 해당 사용자가 이미 이 게시글을 본 기록이 있는지 확인합니다.
        // 이 부분은 FreeDao와 DB에 새로운 메서드 및 테이블이 필요합니다.
        // 예를 들어 `views_log` 같은 테이블을 만들어 {boardNo, userNo}를 기록합니다.
        boolean hasViewed = freeDao.checkUserViewed(boardNo, userNo);
        
        if (!hasViewed) {
            // 3. 기록이 없다면 조회수를 1 증가시키고 기록을 남깁니다.
            freeDao.incrementViews(boardNo);
            freeDao.insertViewLog(boardNo, userNo);
        }
    }
    // --- 수정된 부분 끝 ---

    // 좋아요 관련 메서드
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
        // 새 댓글 등록은 순환 참조를 일으키지 않으므로, 바로 등록
        return freeDao.insertReply(replyDto);
    }

    @Transactional
    public int updateReply(ReplyDto replyDto) {
        // 대댓글(category가 'REPLY')인 경우에만 순환 참조 확인
        if ("REPLY".equals(replyDto.getCategory())) {
            int newRefNo = replyDto.getRefNo();
            int replyNo = replyDto.getReplyNo();

            // 새 부모(newRefNo)가 현재 댓글(replyNo)의 자손인지 확인
            // 이 검사는 댓글 "수정" 시에만 의미가 있음
            int circularCount = freeDao.checkCircularReference(replyNo, newRefNo);

            if (circularCount > 0) {
                // 순환 참조가 발생하면 예외를 던져 작업을 중단
                throw new IllegalStateException("댓글 계층 구조에 순환 참조가 발생하여 댓글을 수정할 수 없습니다.");
            }
        }

        // 검증 통과 시 댓글 수정 진행
        return freeDao.updateReply(replyDto);
    }
    
    public boolean isLiked(int boardNo, int userNo) {
        LikesDto likesDto = new LikesDto();
        likesDto.setBoardNo(boardNo);
        likesDto.setUserNo(userNo);

        int count = freeDao.checkUserLiked(likesDto);
        return count > 0;
    }
}
