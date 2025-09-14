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

    public List<FreeDto> selectAllBoards() {
        return freeDao.selectAllBoards();
    }

    public FreeDto selectBoardByNo(int boardNo) {
        FreeDto board = freeDao.selectBoardByNo(boardNo);
        if (board != null) {
            board.setSik_bti(freeDao.selectSikBtiByUserNo(board.getUserNo()));
        }
        return board;
    }

    @Transactional
    public int insertBoard(FreeDto freeDto, MultipartFile file) {
        int result = freeDao.insertBoard(freeDto);

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

    @Transactional
    public int updateBoard(FreeDto freeDto, MultipartFile file) {
        int result = freeDao.updateBoard(freeDto);

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

    @Transactional
    public boolean softDeleteBoard(int boardNo, int userNo) {
        FreeDto existingPost = freeDao.selectBoardByNo(boardNo);
        if (existingPost != null && existingPost.getUserNo() == userNo) {
            return freeDao.updateBoardDeleteStatus(boardNo, "Y") > 0;
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

        if (freeDao.checkUserLiked(likesDto) > 0) {
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
        List<ReplyDto> replies = freeDao.selectAllRepliesByBoardNo(boardNo);
        for (ReplyDto reply : replies) {
            reply.setSik_bti(freeDao.selectSikBtiByUserNo(reply.getUserNo()));
        }
        return replies;
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
            if (freeDao.checkCircularReference(replyNo, newRefNo) > 0) {
                throw new IllegalStateException("댓글 계층 구조에 순환 참조가 발생하여 댓글을 수정할 수 없습니다.");
            }
        }
        return freeDao.updateReply(replyDto);
    }

    public boolean isLiked(int boardNo, int userNo) {
        LikesDto likesDto = new LikesDto();
        likesDto.setBoardNo(boardNo);
        likesDto.setUserNo(userNo);
        return freeDao.checkUserLiked(likesDto) > 0;
    }

    @Transactional
    public boolean deleteReply(Long replyNo, int userNo) {
        ReplyDto reply = freeDao.selectReplyById(replyNo);
        if (reply == null || reply.getUserNo() != userNo) return false;
        return freeDao.deleteReply(replyNo) > 0;
    }
}
