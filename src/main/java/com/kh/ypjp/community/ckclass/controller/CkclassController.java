package com.kh.ypjp.community.ckclass.controller;

import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import com.kh.ypjp.community.ckclass.service.CkclassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/community/ckclass")
@RequiredArgsConstructor
public class CkclassController {
    private final CkclassService ckclassService;

    // 나의 클래스 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<CkclassDto>> getMyClasses(@AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<CkclassDto> myClasses = ckclassService.findMyClasses(userNo.intValue());
        return ResponseEntity.ok(myClasses);
    }

    // 참여중인 클래스 목록 조회
    @GetMapping("/joined")
    public ResponseEntity<List<CkclassDto>> getJoinedClasses(@AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<CkclassDto> joinedClasses = ckclassService.findJoinedClasses(userNo.intValue());
        return ResponseEntity.ok(joinedClasses);
    }
    
    // 클래스 상세 정보 조회
    @GetMapping("/{roomNo}")
    public ResponseEntity<CkclassDto> getById(@PathVariable int roomNo) {
        CkclassDto ckclassDto = ckclassService.findById(roomNo);
        if (ckclassDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ckclassDto);
    }
    
    // 클래스 등록
    @PostMapping
    public ResponseEntity<String> createClass(
            @ModelAttribute CkclassDto ckclassDto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userNo) {
        
        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }
        
        ckclassDto.setUserNo(userNo.intValue());

        try {
            ckclassService.saveClass(ckclassDto, file);
            return new ResponseEntity<>("클래스가 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("파일 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 클래스 수정
    @PutMapping
    public ResponseEntity<String> updateClass(
            @ModelAttribute CkclassDto ckclassDto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userNo) {
                
        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }
        
        ckclassDto.setUserNo(userNo.intValue());

        try {
            int result = ckclassService.updateClass(ckclassDto, file, userNo.intValue());

            if (result > 0) {
                return new ResponseEntity<>("클래스가 성공적으로 수정되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("클래스 수정에 실패했거나 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("파일 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 클래스 삭제 (논리적 삭제)
    @DeleteMapping("/{roomNo}")
    public ResponseEntity<String> deleteClass(
            @PathVariable int roomNo, 
            @AuthenticationPrincipal Long userNo) {

        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }

        boolean isDeleted = ckclassService.deleteClass(roomNo, userNo.intValue());
        
        if (isDeleted) {
            return new ResponseEntity<>("클래스가 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("클래스 삭제에 실패했거나 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
    
 // 전체 클래스 조회
    @GetMapping("/all")
    public ResponseEntity<List<CkclassDto>> getAllClasses(
            @RequestParam(required = false, defaultValue = "false") boolean excludeCode
    ) {
        List<CkclassDto> allClasses = ckclassService.findAllClasses(excludeCode);
        return ResponseEntity.ok(allClasses);
    }

    
    // 쿠킹 클래스 검색
    @GetMapping("/search")
    public ResponseEntity<List<CkclassDto>> searchClasses(
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "false") boolean excludeCode,
        @AuthenticationPrincipal Long userNo
    ) {
        List<CkclassDto> searchResults = ckclassService.searchClasses(searchType, keyword, excludeCode, userNo != null ? userNo.intValue() : null);
        return ResponseEntity.ok(searchResults);
    }
    
    @PostMapping("/markRead")
    public ResponseEntity<String> markRead(
            @RequestParam int roomNo,
            @AuthenticationPrincipal Long userNo) {

        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }

        ckclassService.markMessagesRead(roomNo, userNo.intValue());
        return ResponseEntity.ok("읽음 처리 완료");
    }
}