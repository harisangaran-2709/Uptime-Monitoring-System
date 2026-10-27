package com.jobscheduler.app;

import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

public class jobExecutor {
    private final JobDAO jobDAO;
    private final HttpClient client;

    public jobExecutor(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    public Map<String, Object> execute(Job job) throws Exception {
        long start = System.nanoTime();
        Integer statusCode = null;
        boolean success = false;
        String error = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(job.get_target_url())).timeout(Duration.ofSeconds(20)).GET().build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            statusCode = response.statusCode();
            success = statusCode == job.get_expected_status_code();
        } catch (Exception e) {
            error = e.getMessage();
        }
        long responseTimeMs = (System.nanoTime() - start) / 1_000_000L;
        jobDAO.saveCheckResult(job.getId(), statusCode, responseTimeMs, success);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("monitor_id", job.getId());
        result.put("target_url", job.get_target_url());
        result.put("expected_status_code", job.get_expected_status_code());
        result.put("actual_status_code", statusCode);
        result.put("response_time_ms", responseTimeMs);
        result.put("success", success);
        result.put("error", error);
        return result;
    }
}
