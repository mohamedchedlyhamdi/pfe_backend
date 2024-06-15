package tn.vermeg.vermegapplication.Services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isColumnEncrypted(String tableName, String columnName) {
        String query = "SELECT column_name FROM information_schema.columns WHERE table_name = ? AND column_name = ? AND is_encrypted = true";
        List<String> results = jdbcTemplate.queryForList(query, String.class, tableName, columnName);
        return !results.isEmpty();
    }
}
