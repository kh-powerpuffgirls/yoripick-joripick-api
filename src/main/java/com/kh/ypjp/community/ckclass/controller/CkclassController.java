package com.kh.ypjp.community.ckclass.controller;

import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import com.kh.ypjp.community.ckclass.service.CkclassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/ckclass")
@RequiredArgsConstructor
public class CkclassController {
    private final CkclassService ckclassService;

    @GetMapping
    public ResponseEntity<List<CkclassDto>> getAllClasses() {
        List<CkclassDto> classes = ckclassService.findAllClasses();
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CkclassDto> getClassById(@PathVariable Long id) {
        CkclassDto ckclass = ckclassService.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id " + id));
        return ResponseEntity.ok(ckclass);
    }

    @PostMapping
    public ResponseEntity<CkclassDto> createClass(@RequestBody CkclassDto dto) {
        CkclassDto newClass = ckclassService.saveClass(dto);
        return ResponseEntity.ok(newClass);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CkclassDto> updateClass(@PathVariable Long id, @RequestBody CkclassDto dto) {
        CkclassDto updatedClass = ckclassService.updateClass(id, dto);
        return ResponseEntity.ok(updatedClass);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        ckclassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}