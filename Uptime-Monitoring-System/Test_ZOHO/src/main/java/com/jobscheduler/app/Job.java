package com.jobscheduler.app;
import java.sql.Timestamp;

public class Job {
    int id;
    String name;
    String target_url;
    int expected_status_code;
    Timestamp check_interval;
    boolean enabled;

    public Job() {}
    public Job(String name, String target_url, int expected_status_code, Timestamp check_interval) {
        this.name = name;
        this.target_url = target_url;
        this.expected_status_code = expected_status_code;
        this.check_interval = check_interval;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String get_name() {
        return name;
    }
    public void set_name(String name) {
        this.name = name;
    }
    public String get_target_url() {
        return target_url;
    }
    public void set_target_url(String target_url) {
        this.target_url = target_url;
    }
    public int get_expected_status_code() {
        return expected_status_code;
    }
    public void set_expected_status_code(int expected_status_code) {
        this.expected_status_code = expected_status_code;
    }
    public Timestamp get_check_interval() {
        return check_interval;
    }
    public void set_check_interval(Timestamp check_interval) {
        this.check_interval = check_interval;
    }
}
