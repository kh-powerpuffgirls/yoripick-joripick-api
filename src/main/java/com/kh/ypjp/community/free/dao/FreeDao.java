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
    int incrementViews(int boardNo);
    String selectSikBtiByUserNo(int userNo);

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
    
    ReplyDto selectReplyById(@Param("replyNo") Long replyNo);
    int deleteReply(@Param("replyNo") Long replyNo);
    int checkCircularReference(@Param("replyNo") int replyNo, @Param("newRefNo") int newRefNo);
    int updateBoardDeleteStatus(@Param("boardNo") int boardNo, @Param("deleteStatus") String deleteStatus);
}