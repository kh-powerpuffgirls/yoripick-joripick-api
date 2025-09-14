package com.kh.ypjp.community.free.service;

import com.kh.ypjp.common.UtilService;
import com.kh.ypjp.community.free.dao.FreeDao;
import com.kh.ypjp.community.free.dto.FreeDto;
import com.kh.ypjp.community.free.dto.ReplyDto;
import com.kh.ypjp.community.free.dto.LikesDto;
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

    // 모든 게시글 목록을 조회
    public List<FreeDto> selectAllBoards() {
        return freeDao.selectAllBoards();
    }

    // 게시글 조회+식bti
    public FreeDto selectBoardByNo(int boardNo) {
        FreeDto board = freeDao.selectBoardByNo(boardNo);
        if (board != null) {
            board.setSik_bti(freeDao.selectSikBtiByUserNo(board.getUserNo()));
        }
        return board;
    }

    // 게시글 등록 및 이미지 저장 처리
    @Transactional
    public int insertBoard(FreeDto freeDto, MultipartFile file) {
        int result = freeDao.insertBoard(freeDto);

        // 파일이 있을 경우 이미지 저장 및 게시글에 이미지 번호 업데이트
        if (file != null && !file.isEmpty()) {
            String webPath = "messages/" + freeDto.getUserNo();
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

    // 게시글 수정 및 이미지 업데이트 처리
    @Transactional
    public int updateBoard(FreeDto freeDto, MultipartFile file) {
        int result = freeDao.updateBoard(freeDto);

        // 파일이 있을 경우 기존 이미지 삭제 후 새 이미지 저장 및 게시글에 이미지 번호 업데이트
        if (file != null && !file.isEmpty()) {
            Integer oldImageNo = freeDao.selectImageNoByBoardNo(freeDto.getBoardNo());
            if (oldImageNo != null) {
                freeDao.deleteImageByImageNo(oldImageNo);
            }

            String webPath = "messages/" + freeDto.getUserNo();
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

    // 삭제 상태로 변경
    @Transactional
    public boolean softDeleteBoard(int boardNo, int userNo) {
        FreeDto existingPost = freeDao.selectBoardByNo(boardNo);
        // 작성자 본인인지 확인
        if (existingPost != null && existingPost.getUserNo() == userNo) {
            return freeDao.updateBoardDeleteStatus(boardNo, "Y") > 0;
        }
        return false;
    }

    // 게시글 조회수 증가
    @Transactional
    public int incrementViews(int boardNo) {
        return freeDao.incrementViews(boardNo);
    }

    // 좋아요를 추가하거나 취소
    @Transactional
    public boolean toggleLike(int boardNo, int userNo) {
        LikesDto likesDto = new LikesDto();
        likesDto.setBoardNo(boardNo);
        likesDto.setUserNo(userNo);

        // 좋아요 기록이 있는지 확인 후 토글
        if (freeDao.checkUserLiked(likesDto) > 0) {
            freeDao.deleteLike(likesDto);
            return false;
        } else {
            freeDao.insertLike(likesDto);
            return true;
        }
    }

    //  좋아요 개수
    public int getLikesCount(int boardNo) {
        return freeDao.countLikesByBoardNo(boardNo);
    }

    // 댓글 조회+식bti
    public List<ReplyDto> selectAllRepliesByBoardNo(int boardNo) {
        List<ReplyDto> replies = freeDao.selectAllRepliesByBoardNo(boardNo);
        for (ReplyDto reply : replies) {
            reply.setSik_bti(freeDao.selectSikBtiByUserNo(reply.getUserNo()));
        }
        return replies;
    }

    // 새로운 댓글 등록
    @Transactional
    public int insertReply(ReplyDto replyDto) {
        return freeDao.insertReply(replyDto);
    }

    // 댓글 수정
    @Transactional
    public int updateReply(ReplyDto replyDto) {
        // 대댓글인 경우 순환 참조를 방지
        if ("REPLY".equals(replyDto.getCategory())) {
            int newRefNo = replyDto.getRefNo();
            int replyNo = replyDto.getReplyNo();
            if (freeDao.checkCircularReference(replyNo, newRefNo) > 0) {
                throw new IllegalStateException("댓글 계층 구조에 순환 참조가 발생하여 댓글을 수정할 수 없습니다.");
            }
        }
        return freeDao.updateReply(replyDto);
    }

    // 좋아요 확인
    public boolean isLiked(int boardNo, int userNo) {
        LikesDto likesDto = new LikesDto();
        likesDto.setBoardNo(boardNo);
        likesDto.setUserNo(userNo);
        return freeDao.checkUserLiked(likesDto) > 0;
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteReply(Long replyNo, int userNo) {
        ReplyDto reply = freeDao.selectReplyById(replyNo);
        // 작성자 본인인지 확인 후 삭제
        if (reply == null || reply.getUserNo() != userNo) return false;
        return freeDao.deleteReply(replyNo) > 0;
    }
}