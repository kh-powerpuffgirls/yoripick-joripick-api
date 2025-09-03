package com.kh.ypjp.community.challenge.dao;

import com.kh.ypjp.community.challenge.dto.ChallengeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChallengeDao {
    private final JdbcTemplate jdbcTemplate;

    private ChallengeDto mapRowToChallengeDto(ResultSet rs, int rowNum) throws SQLException {
        ChallengeDto dto = new ChallengeDto();
        dto.setId(rs.getLong("id"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setImageUrl(rs.getString("image_url"));
        dto.setAuthor(rs.getString("author"));
        dto.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        dto.setModifiedDate(rs.getObject("modified_date", LocalDateTime.class));
        dto.setViews(rs.getInt("views"));
        dto.setLikes(rs.getInt("likes"));
        return dto;
    }

    public List<ChallengeDto> findAll() {
        String sql = "SELECT * FROM challenge ORDER BY created_date DESC";
        return jdbcTemplate.query(sql, this::mapRowToChallengeDto);
    }

    public Optional<ChallengeDto> findById(Long id) {
        String sql = "SELECT * FROM challenge WHERE id = ?";
        List<ChallengeDto> results = jdbcTemplate.query(sql, this::mapRowToChallengeDto, id);
        return results.stream().findFirst();
    }

    public ChallengeDto save(ChallengeDto dto) {
        String sql = "INSERT INTO challenge (title, content, image_url, author, created_date, modified_date, views, likes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, dto.getTitle(), dto.getContent(), dto.getImageUrl(), dto.getAuthor(), LocalDateTime.now(), LocalDateTime.now(), 0, 0);
        return dto;
    }

    public int update(Long id, ChallengeDto dto) {
        String sql = "UPDATE challenge SET title = ?, content = ?, image_url = ?, modified_date = ? WHERE id = ?";
        return jdbcTemplate.update(sql, dto.getTitle(), dto.getContent(), dto.getImageUrl(), LocalDateTime.now(), id);
    }

    public int delete(Long id) {
        String sql = "DELETE FROM challenge WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public void incrementViews(Long id) {
        String sql = "UPDATE challenge SET views = views + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    public void incrementLikes(Long id) {
        String sql = "UPDATE challenge SET likes = likes + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}