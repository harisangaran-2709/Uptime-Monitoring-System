package com.jobscheduler.app;

import com.jobscheduler.util.DBConnection;
import java.sql.*;
import java.util.*;

public class JobDAO {

    public boolean createJob(Job job) throws SQLException {
        String sql = "INSERT INTO monitor_manager (name, target_url, expected_status_code, check_interval, enabled, failure_threshold) VALUES (?, ?, ?, ?, ?, 3)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, job.get_name());
            ps.setString(2, job.get_target_url());
            ps.setInt(3, job.get_expected_status_code());
            ps.setInt(4, job.get_check_interval());
            ps.setBoolean(5, job.isEnabled());

            if (ps.executeUpdate() != 1) {
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    job.setId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    public List<Job> getAllJobs() throws SQLException {
        List<Job> jobs = new ArrayList<>();

        String sql = "SELECT id, name, target_url, expected_status_code, check_interval, enabled FROM monitor_manager ORDER BY id";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.set_name(rs.getString("name"));
                job.set_target_url(rs.getString("target_url"));
                job.set_expected_status_code(rs.getInt("expected_status_code"));
                job.set_check_interval(rs.getInt("check_interval"));
                job.setEnabled(rs.getBoolean("enabled"));
                jobs.add(job);
            }
        }

        return jobs;
    }

    public Job getJobById(int id) throws SQLException {
        String sql = "SELECT id, name, target_url, expected_status_code, check_interval, enabled FROM monitor_manager WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    Job job = new Job();
                    job.setId(rs.getInt("id"));
                    job.set_name(rs.getString("name"));
                    job.set_target_url(rs.getString("target_url"));
                    job.set_expected_status_code(rs.getInt("expected_status_code"));
                    job.set_check_interval(rs.getInt("check_interval"));
                    job.setEnabled(rs.getBoolean("enabled"));

                    return job;
            }
        }
    }

    public boolean updateJob(Job job) throws SQLException {
        String sql = "UPDATE monitor_manager SET name = ?, target_url = ?, expected_status_code = ?, check_interval = ?, enabled = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, job.get_name());
                ps.setString(2, job.get_target_url());
                ps.setInt(3, job.get_expected_status_code());
                ps.setInt(4, job.get_check_interval());
                ps.setBoolean(5, job.isEnabled());
                ps.setInt(6, job.getId());

                return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteJob(int id) throws SQLException {
        String deleteResultsSql = "DELETE FROM monitor_check_results WHERE monitor_id = ?";
        String deleteAlertsSql = "DELETE FROM alerts WHERE monitor_id = ?";
        String deleteJobSql = "DELETE FROM monitor_manager WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(deleteResultsSql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(deleteAlertsSql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                int deleted;

                try (PreparedStatement ps = con.prepareStatement(deleteJobSql)) {
                    ps.setInt(1, id);
                    deleted = ps.executeUpdate();
                }

                con.commit();
                return deleted > 0;

            }
            catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void saveCheckResult(int monitorId, Integer statusCode, long responseTimeMs, boolean success) throws SQLException {
        String sql = "INSERT INTO monitor_check_results (monitor_id, status_code, response_time_ms, success) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, monitorId);
                if (statusCode == null) {
                    ps.setNull(2, Types.INTEGER);
                }
                else {
                    ps.setInt(2, statusCode);
                }
                ps.setLong(3, responseTimeMs);
                ps.setBoolean(4, success);
                ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> getCheckHistory(int monitorId, int limit) throws SQLException {
        List<Map<String, Object>> history = new ArrayList<>();

        String sql = "SELECT id, checked_at, status_code, response_time_ms, success FROM monitor_check_results WHERE monitor_id = ? ORDER BY checked_at DESC, id DESC LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, monitorId);
                ps.setInt(2, Math.max(1, Math.min(limit, 100)));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("id", rs.getLong("id"));
                        row.put("checked_at", rs.getTimestamp("checked_at"));
                        if (rs.getObject("status_code") == null) {
                            row.put("status_code", null);
                        }
                        else {
                            row.put("status_code", rs.getInt("status_code"));
                        }
                        row.put("response_time_ms", rs.getLong("response_time_ms"));
                        row.put("success", rs.getBoolean("success"));
                        history.add(row);
                    }
                }
        }
        return history;
    }
}
