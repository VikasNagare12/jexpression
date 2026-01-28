package com.example.jexpression.repository;

import com.example.jexpression.model.RuleConfigRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC repository for rule_config table.
 * Uses JdbcTemplate - no JPA/Hibernate.
 */
@Repository
public class RuleConfigJdbcRepository {

    private static final String FIND_ENABLED_QUERY =
            "SELECT id, country, currency, rules_json, enabled, updated_at " +
                    "FROM rule_config " +
                    "WHERE enabled = true";

    private final JdbcTemplate jdbcTemplate;

    public RuleConfigJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Load all enabled rule configurations from database.
     */
    public List<RuleConfigRow> findEnabledRules() {
        return jdbcTemplate.query(FIND_ENABLED_QUERY, new RuleConfigRowMapper());
    }

    /**
     * Row mapper for rule_config table.
     */
    private static class RuleConfigRowMapper implements RowMapper<RuleConfigRow> {
        @Override
        public RuleConfigRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RuleConfigRow(
                    rs.getLong("id"),
                    rs.getString("country"),
                    rs.getString("currency"),
                    rs.getString("rules_json"),
                    rs.getBoolean("enabled"),
                    rs.getTimestamp("updated_at").toLocalDateTime()
            );
        }
    }
}
