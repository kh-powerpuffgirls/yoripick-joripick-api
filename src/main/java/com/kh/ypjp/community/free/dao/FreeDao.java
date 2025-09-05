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
    int deleteBoard(int boardNo);
    void incrementViews(int boardNo);

    Integer selectImageNoByBoardNo(int boardNo);
    int deleteImageByImageNo(int imageNo);
    int updateBoardImageNo(int boardNo, int imageNo);

    int checkUserLiked(LikesDto likesDto);
    int insertLike(LikesDto likesDto);
    int deleteLike(LikesDto likesDto);
    int countLikesByBoardNo(int boardNo);

    // 댓글 관련 메서드
    List<ReplyDto> selectAllRepliesByBoardNo(int boardNo);
    int insertReply(ReplyDto replyDto);
    int updateReply(ReplyDto replyDto); // 추가
    int deleteReply(int replyNo); // 추가
    int deleteImage(Integer imageNo); // 추가

    /**
     * 댓글 수정 시 순환 참조를 확인하는 메서드
     * @param replyNo 수정하려는 댓글 번호
     * @param newRefNo 새롭게 지정하려는 부모 댓글 번호
     * @return 순환 참조가 있으면 1, 없으면 0
     */
    int checkCircularReference(@Param("replyNo") int replyNo, @Param("newRefNo") int newRefNo);

    // --- 추가된 조회수 관련 메서드 시작 ---
    /**
     * 특정 사용자가 특정 게시글을 이미 조회했는지 확인
     * @param boardNo 게시글 번호
     * @param userNo 사용자 번호
     * @return 조회 기록이 있으면 true, 없으면 false
     */
    boolean checkUserViewed(@Param("boardNo") int boardNo, @Param("userNo") int userNo);
    
    /**
     * 특정 사용자의 게시글 조회 기록을 남김
     * @param boardNo 게시글 번호
     * @param userNo 사용자 번호
     * @return 성공 시 1 반환
     */
    int insertViewLog(@Param("boardNo") int boardNo, @Param("userNo") int userNo);
    // --- 추가된 조회수 관련 메서드 끝 ---
}
