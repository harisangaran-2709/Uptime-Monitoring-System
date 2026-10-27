package com.jobscheduler.app;

public class Job {
    private int id;
    private String name;
    private String target_url;
    private int expected_status_code;
    private int check_interval;
    private boolean enabled;

    public Job() { }

    public Job(String name, String target_url, int expected_status_code, int check_interval, boolean enabled) {
        this.name = name;
        this.target_url = target_url;
        this.expected_status_code = expected_status_code;
        this.check_interval = check_interval;
        this.enabled = enabled;
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
    public int get_check_interval() {
        return check_interval;
    }
    public void set_check_interval(int check_interval) {
        this.check_interval = check_interval;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
