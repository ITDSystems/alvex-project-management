package com.alvexcore.repo.wfscheduler.webscripts;

import com.alvexcore.repo.wfscheduler.service.WfSchedulerService;
import org.json.simple.JSONArray;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

public class ReadJobInfo extends DeclarativeWebScript {
    private WfSchedulerService wfSchedulerService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req,
                                              Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", false);

        String login = req.getParameter("user");

        if(login != null && !login.isEmpty()){
            JSONArray json = wfSchedulerService.readAllRecordByUser(login);
            if(json != null) model.put("workflows", json.toJSONString());
            model.put("success", true);
        }

        return model;
    }

    public WfSchedulerService getWfSchedulerService() {
        return wfSchedulerService;
    }

    public void setWfSchedulerService(WfSchedulerService wfSchedulerService) {
        this.wfSchedulerService = wfSchedulerService;
    }

}
