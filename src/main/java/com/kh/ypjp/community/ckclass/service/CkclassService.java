package com.kh.ypjp.community.ckclass.service;

import com.kh.ypjp.community.ckclass.dao.CkclassDao;
import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CkclassService {
    private final CkclassDao ckclassDao;

    public List<CkclassDto> findAllClasses() {
        return ckclassDao.findAllClasses();
    }

    public Optional<CkclassDto> findById(Long id) {
        return ckclassDao.findById(id);
    }

    public CkclassDto saveClass(CkclassDto dto) {
        return ckclassDao.saveClass(dto);
    }

    public CkclassDto updateClass(Long id, CkclassDto dto) {
        ckclassDao.updateClass(id, dto);
        return ckclassDao.findById(id).orElseThrow(() -> new RuntimeException("Updated class not found"));
    }

    public void deleteClass(Long id) {
        ckclassDao.deleteClass(id);
    }
}