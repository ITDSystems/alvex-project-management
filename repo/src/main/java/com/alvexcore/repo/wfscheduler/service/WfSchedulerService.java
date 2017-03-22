package com.alvexcore.repo.wfscheduler.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Calendar;

public interface WfSchedulerService {

    void writeScheduleInfo(String user, JSONObject formData);

    void updateAllScheduleInfo(String user, JSONArray formData);

    JSONArray readAllRecordByUser(String user);

    void startWfByCurrentDate(Calendar cal);
}
