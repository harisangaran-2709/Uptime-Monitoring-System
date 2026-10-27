package com.jobscheduler.app;
import com.jobscheduler.util.DBConnection;
import java.util.*;

import java.sql.*;

public class JobDAO {
    public boolean createJobs() throws SQLException{
        String sql = "insert into monitor_manager(name, target_url, expected_status_code, check_interval) values(?, ?, ?, ?);";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ){
            con.setAutoCommit(false);
            Job jobs = new Job();
            ps.setString(1, jobs.get_name());
            ps.setString(2, jobs.get_target_url());
            ps.setInt(3, jobs.get_expected_status_code());
            ps.setTimestamp(4, jobs.get_check_interval());

            return ps.executeUpdate()> 0;
        }
    }

    public List<Job> getAllJobs() throws SQLException{
        List<Job> jobs = new ArrayList<>();
        String sql = "select* from monitor_manager;";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String name = rs.getString(1);
                String target_url = rs.getString(2);
                int expected_status_code = rs.getInt(3);
                Timestamp check_interval = rs.getTimestamp(4);

                Job job1 = new Job(name, target_url, expected_status_code, check_interval);
                jobs.add(job1);
            }
        }
        return jobs;
    }

    public Job getJobById(int id) throws SQLException{
        String name;
        String target_url;
        int expected_status_code;
        Timestamp check_interval;

        String sql = "select * from monitor_manager where id = ?;";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            name = rs.getString(1);
            target_url = rs.getString(2);
            expected_status_code = rs.getInt(3);
            check_interval = rs.getTimestamp(4);
        }
        Job job1 = new Job(name, target_url, expected_status_code, check_interval);
        return job1;
    }

    public boolean updateJob(Job job) throws SQLException{
        String sql = "update monitor_manager set name = ?, target_url = ?, expected_status_code = ?, check_interval = ? where id = ?;";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, job.get_name());
            ps.setString(2, job.get_target_url());
            ps.setInt(3, job.get_expected_status_code());
            ps.setTimestamp(4, job.get_check_interval());
            ps.setInt(5, job.getId());

            return ps.executeUpdate() > 0;
        }
    }
    public boolean deleteJob(int id) throws SQLException{
        String sql = "delete from monitor_manager where id = ?;";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate()> 0;
        }
    }
}