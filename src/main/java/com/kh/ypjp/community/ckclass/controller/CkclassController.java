package com.kh.ypjp.community.ckclass.controller;

import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import com.kh.ypjp.community.ckclass.service.CkclassService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/my")
    public ResponseEntity<List<CkclassDto>> getMyClasses(@AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<CkclassDto> myClasses = ckclassService.findMyClasses(userNo.intValue());
        return ResponseEntity.ok(myClasses);
    }

    @GetMapping("/joined")
    public ResponseEntity<List<CkclassDto>> getJoinedClasses(@AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<CkclassDto> joinedClasses = ckclassService.findJoinedClasses(userNo.intValue());
        return ResponseEntity.ok(joinedClasses);
    }
    
    @GetMapping("/{roomNo}")
    public ResponseEntity<CkclassDto> getById(@PathVariable int roomNo,
                                              HttpServletRequest request) {
        CkclassDto ckclassDto = ckclassService.findById(roomNo);
        if (ckclassDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (ckclassDto.getServerName() != null && !ckclassDto.getServerName().isEmpty()) {
            String baseUrl = request.getScheme() + "://"
                    + request.getServerName() + ":"
                    + request.getServerPort();
            ckclassDto.setImageUrl(baseUrl + "/images/" + ckclassDto.getServerName());
        }

        return ResponseEntity.ok(ckclassDto);
    }
    
    @PostMapping
    public ResponseEntity<String> createClass(
            @ModelAttribute CkclassDto ckclassDto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userNo) {

        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }

        ckclassDto.setUserNo(userNo.intValue());

        if (ckclassDto.getPasscode() != null && ckclassDto.getPasscode().isEmpty()) {
            ckclassDto.setPasscode(null);
        }

        try {
            ckclassService.saveClass(ckclassDto, file);
            return new ResponseEntity<>("클래스가 성공적으로 등록되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("파일 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<String> updateClass(
            @ModelAttribute CkclassDto ckclassDto,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userNo,
            Authentication authentication) {

        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }

        ckclassDto.setUserNo(userNo.intValue());

        if (ckclassDto.getPasscode() != null && ckclassDto.getPasscode().isEmpty()) {
            ckclassDto.setPasscode(null);
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        try {
            int result = ckclassService.updateClass(ckclassDto, file, userNo.intValue(), isAdmin);

            if (result > 0) {
                return new ResponseEntity<>("클래스가 성공적으로 수정되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("클래스 수정에 실패했거나 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("파일 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
    
    @GetMapping("/all")
    public ResponseEntity<List<CkclassDto>> getAllClasses(
            @RequestParam(required = false, defaultValue = "false") boolean excludeCode
    ) {
        List<CkclassDto> allClasses = ckclassService.findAllClasses(excludeCode);
        return ResponseEntity.ok(allClasses);
    }
    
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
        return ResponseEntity.ok("읽음 처리 완료"); 
    }
    
    @GetMapping("/{roomNo}/members")
    public ResponseEntity<List<CkclassDto>> getClassMembers(@PathVariable int roomNo,
                                                            HttpServletRequest request) {
        try {
            List<CkclassDto> members = ckclassService.findMembersByClass(roomNo);
            String baseUrl = request.getScheme() + "://"
                    + request.getServerName() + ":"
                    + request.getServerPort();

            members.forEach(m -> {
                if (m.getServerName() != null && !m.getServerName().isEmpty()) {
                    m.setImageUrl(baseUrl + "/images/" + m.getServerName());
                }
            });

            return ResponseEntity.ok(members);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/enroll")
    public ResponseEntity<String> enrollUser(
            @RequestBody CkclassDto ckclassDto,
            @AuthenticationPrincipal Long userNo) {

        if (userNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }

        if (!ckclassDto.getUserNo().equals(userNo.intValue())) {
            return new ResponseEntity<>("잘못된 사용자 정보입니다.", HttpStatus.FORBIDDEN);
        }

        try {
            ckclassService.enrollUser(ckclassDto.getRoomNo(), userNo.intValue());
            return new ResponseEntity<>("클래스에 성공적으로 참여했습니다.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{roomNo}/toggleNotification")
    public ResponseEntity<?> toggleNotification(@PathVariable int roomNo, @AuthenticationPrincipal Long userNo) {
        try {
            CkclassDto updatedClass = ckclassService.toggleNotification(roomNo, userNo.intValue());
            return ResponseEntity.ok(updatedClass);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{roomNo}/kick/{userNo}")
    public ResponseEntity<String> kickMember(
            @PathVariable int roomNo,
            @PathVariable int userNo,
            @AuthenticationPrincipal Long currentUserNo
    ) {
        if (currentUserNo == null) {
            return new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
        }

        boolean result = ckclassService.kickMember(roomNo, userNo, currentUserNo.intValue());

        if (result) {
            return ResponseEntity.ok("멤버가 강퇴되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("강퇴에 실패했습니다. 권한이 없거나 잘못된 요청입니다.");
        }
    }

    @DeleteMapping("/{roomNo}/leave")
    public ResponseEntity<String> leaveClass(@PathVariable int roomNo, @AuthenticationPrincipal Long userNo) {
        if (userNo == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
        try {
            boolean isLeft = ckclassService.leaveClass(roomNo, userNo.intValue());
            
            if (isLeft) {
                return new ResponseEntity<>("클래스에서 성공적으로 탈퇴했습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("탈퇴에 실패했습니다. 클래스에 참여하지 않았거나 해당 클래스가 존재하지 않습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("탈퇴 처리 중 예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}