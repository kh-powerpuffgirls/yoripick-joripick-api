package com.kh.ypjp.community.free.dao;

import com.kh.ypjp.community.free.dto.FreeDto;
import com.kh.ypjp.community.free.dto.LikesDto;
import com.kh.ypjp.community.free.dto.ReplyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FreeDao {
    List<FreeDto> selectAllBoards();
    FreeDto selectBoardByNo(int boardNo);
    int insertBoard(FreeDto freeDto);
    int insertImage(Map<String, Object> imageInfo);
    int updateBoard(FreeDto freeDto);
    
    // 컨트롤러와 서비스 로직에 맞춰 파라미터 제거
    int incrementViews(int boardNo);

    Integer selectImageNoByBoardNo(int boardNo);
    int deleteImageByImageNo(int imageNo);
    int updateBoardImageNo(@Param("boardNo") int boardNo, @Param("imageNo") Integer imageNo);
    int checkUserLiked(LikesDto likesDto);
    int insertLike(LikesDto likesDto);
    int deleteLike(LikesDto likesDto);
    int countLikesByBoardNo(int boardNo);

    List<ReplyDto> selectAllRepliesByBoardNo(int boardNo);
    int insertReply(ReplyDto replyDto);
    int updateReply(ReplyDto replyDto);
    
    // 중복 메서드 제거
    // int deleteImage(Integer imageNo); 

    // Long 타입으로 변경
    ReplyDto selectReplyById(@Param("replyNo") Long replyNo);
    int deleteReply(@Param("replyNo") Long replyNo);

    int checkCircularReference(@Param("replyNo") int replyNo, @Param("newRefNo") int newRefNo);
    
    // 컨트롤러와 서비스 로직에 맞춰 제거
    // boolean checkUserViewed(@Param("boardNo") int boardNo, @Param("userNo") Long tempUserNo); 
    // int insertViewLog(@Param("boardNo") int boardNo, @Param("userNo") Long tempUserNo);

    int updateBoardDeleteStatus(@Param("boardNo") int boardNo, @Param("deleteStatus") String deleteStatus);
}