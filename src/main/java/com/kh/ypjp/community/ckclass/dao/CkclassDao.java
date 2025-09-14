package com.kh.ypjp.community.ckclass.dao;

import com.kh.ypjp.community.ckclass.dto.CkclassDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CkclassDao {
    private final JdbcTemplate jdbcTemplate;

    private CkclassDto mapRowToCkclassDto(ResultSet rs, int rowNum) throws SQLException {
        CkclassDto dto = new CkclassDto();
        dto.setId(rs.getLong("id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setImageUrl(rs.getString("image_url"));
        dto.setJoinCode(rs.getString("join_code"));
        return dto;
    }

    public List<CkclassDto> findAllClasses() {
        String sql = "SELECT id, name, description, image_url, join_code FROM ckclass";
        return jdbcTemplate.query(sql, this::mapRowToCkclassDto);
    }

    public Optional<CkclassDto> findById(Long id) {
        String sql = "SELECT id, name, description, image_url, join_code FROM ckclass WHERE id = ?";
        List<CkclassDto> results = jdbcTemplate.query(sql, this::mapRowToCkclassDto, id);
        return results.stream().findFirst();
    }

    public CkclassDto saveClass(CkclassDto dto) {
        String sql = "INSERT INTO ckclass (name, description, image_url, join_code) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, dto.getName(), dto.getDescription(), dto.getImageUrl(), dto.getJoinCode());
        return dto;
    }

    public int updateClass(Long id, CkclassDto dto) {
        String sql = "UPDATE ckclass SET name = ?, description = ?, image_url = ?, join_code = ? WHERE id = ?";
        return jdbcTemplate.update(sql, dto.getName(), dto.getDescription(), dto.getImageUrl(), dto.getJoinCode(), id);
    }

    public int deleteClass(Long id) {
        String sql = "DELETE FROM ckclass WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}