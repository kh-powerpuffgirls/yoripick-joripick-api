package com.kh.ypjp.community.free.dao;

import com.kh.ypjp.community.free.dto.FreeDto;
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

 // Likes 관련
    String findLikeStatus(@Param("userNo") int userNo, @Param("boardNo") int boardNo);
    int checkIfLiked(@Param("userNo") int userNo, @Param("boardNo") int boardNo);
    void insertOrUpdateLike(@Param("userNo") int userNo,
                            @Param("boardNo") int boardNo,
                            @Param("likeStatus") String likeStatus);
    void deleteLike(@Param("userNo") int userNo, @Param("boardNo") int boardNo);
    int getLikesCount(@Param("boardNo") int boardNo);

    // 댓글 관련
    List<ReplyDto> selectAllRepliesByBoardNo(int boardNo);
    int insertReply(ReplyDto replyDto);
    int updateReply(ReplyDto replyDto);
    ReplyDto selectReplyById(@Param("replyNo") Long replyNo);
    int deleteReply(@Param("replyNo") Long replyNo);
    int checkCircularReference(@Param("replyNo") int replyNo, @Param("newRefNo") int newRefNo);

    int updateBoardDeleteStatus(@Param("boardNo") int boardNo, @Param("deleteStatus") String deleteStatus);
}
