package com.kh.ypjp.community.free.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.free.dao.FreeDao;
import com.kh.ypjp.community.free.dto.FreeDto;
import com.kh.ypjp.community.free.dto.ReplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FreeService {

    private final FreeDao freeDao;
    private final UtilService utilService;

    // 게시글 전체 조회
    public List<FreeDto> selectAllBoards() {
        List<FreeDto> boardList = freeDao.selectAllBoards();
        for (FreeDto board : boardList) {
            board.setSik_bti(freeDao.selectSikBtiByUserNo(board.getUserNo())); 
        }
        return boardList;
    }

    // 게시글 조회
    public FreeDto selectBoardByNo(int boardNo) {
        FreeDto board = freeDao.selectBoardByNo(boardNo);
        if (board != null) {
            board.setSik_bti(freeDao.selectSikBtiByUserNo(board.getUserNo()));
        }
        return board;
    }

    // 게시글 등록 + 이미지 처리
    @Transactional
    public int insertBoard(FreeDto freeDto, MultipartFile file) {
        int result = freeDao.insertBoard(freeDto);

        if (file != null && !file.isEmpty()) {
            String webPath = "free/" + freeDto.getUserNo();
            String changeName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + changeName;

            freeDto.setServerName(serverName);
            freeDto.setOriginName(file.getOriginalFilename());

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", freeDto.getOriginName());

            utilService.insertImage(param);
            Long imageNo = utilService.getImageNo(param);
            freeDao.updateBoardImageNo(freeDto.getBoardNo(), imageNo.intValue());
        }

        return result;
    }

    @Transactional
    public int updateBoard(FreeDto freeDto, MultipartFile file, int userNo, boolean isAdmin, boolean isImageDeleted) {
        FreeDto existingPost = freeDao.selectBoardByNo(freeDto.getBoardNo());
        if (existingPost == null || (existingPost.getUserNo() != userNo && !isAdmin)) return 0;

        int result = freeDao.updateBoard(freeDto);

        // 이미지 삭제 요청이 있을 때
        if (isImageDeleted) {
            Integer oldImageNo = freeDao.selectImageNoByBoardNo(freeDto.getBoardNo());
            if (oldImageNo != null) {
                freeDao.deleteImageByImageNo(oldImageNo);
                freeDao.updateBoardImageNoNull(freeDto.getBoardNo());
            }
        }

        // 새로운 이미지 업로드 시
        if (file != null && !file.isEmpty()) {
            Integer oldImageNo = freeDao.selectImageNoByBoardNo(freeDto.getBoardNo());
            if (oldImageNo != null) freeDao.deleteImageByImageNo(oldImageNo);

            String webPath = "free/" + freeDto.getUserNo();
            String changeName = utilService.getChangeName(file, webPath);
            String serverName = webPath + "/" + changeName;

            freeDto.setServerName(serverName);
            freeDto.setOriginName(file.getOriginalFilename());

            Map<String, Object> param = new HashMap<>();
            param.put("serverName", serverName);
            param.put("originName", freeDto.getOriginName());

            utilService.insertImage(param);
            Long newImageNo = utilService.getImageNo(param);
            freeDao.updateBoardImageNo(freeDto.getBoardNo(), newImageNo.intValue());
        }

        return result;
    }

    // 게시글 soft delete
    @Transactional
    public boolean softDeleteBoard(int boardNo, int userNo, boolean isAdmin) {
        FreeDto existingPost = freeDao.selectBoardByNo(boardNo);
        if (existingPost != null && (existingPost.getUserNo() == userNo || isAdmin)) {
            return freeDao.updateBoardDeleteStatus(boardNo, "Y") > 0;
        }
        return false;
    }

    // 조회수 증가
    @Transactional
    public int incrementViews(int boardNo) {
        return freeDao.incrementViews(boardNo);
    }

    @Transactional
    public void setLikeStatus(int boardNo, int userNo, String likeStatus) {
        String currentStatus = freeDao.findLikeStatus(userNo, boardNo);

        if ("COMMON".equals(likeStatus) || (likeStatus != null && likeStatus.equals(currentStatus))) {
            freeDao.deleteLike(userNo, boardNo);
        } else {
            freeDao.insertOrUpdateLike(userNo, boardNo, likeStatus);
        }
    }


    // 좋아요 상태 조회
    public boolean getLikeStatus(int boardNo, int userNo) {
        String status = freeDao.findLikeStatus(userNo, boardNo);
        return "LIKE".equals(status);
    }

    // 좋아요 개수 조회
    public int getLikesCount(int boardNo) {
        Integer count = freeDao.getLikesCount(boardNo);
        return count != null ? count : 0;
    }
    public List<ReplyDto> selectAllRepliesByBoardNo(int boardNo) {
        List<ReplyDto> replies = freeDao.selectAllRepliesByBoardNo(boardNo);
        
        for (ReplyDto reply : replies) {
            String profileImageServerName = reply.getProfileImageServerName();
            if (profileImageServerName != null && !profileImageServerName.isEmpty()) {
                String fullPath = "profile/" + reply.getUserNo() + "/" + profileImageServerName;
                reply.setProfileImageServerName(fullPath);
            }
        }
        return replies;
    }
    // 댓글 등록
    @Transactional
    public int insertReply(ReplyDto replyDto) {
        return freeDao.insertReply(replyDto);
    }

    // 댓글 수정
    @Transactional
    public int updateReply(ReplyDto replyDto) {
        if ("REPLY".equals(replyDto.getCategory())) {
            int newRefNo = replyDto.getRefNo();
            int replyNo = replyDto.getReplyNo();
            if (freeDao.checkCircularReference(replyNo, newRefNo) > 0) {
                throw new IllegalStateException("댓글 계층 구조에 순환 참조가 발생하여 댓글을 수정할 수 없습니다.");
            }
        }
        return freeDao.updateReply(replyDto);
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteReply(Long replyNo, int userNo) {
        ReplyDto reply = freeDao.selectReplyById(replyNo);
        if (reply == null || reply.getUserNo() != userNo) return false;
        return freeDao.deleteReply(replyNo) > 0;
    }
}