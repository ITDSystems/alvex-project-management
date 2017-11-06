package com.alvexcore.repo.wfscheduler.webscripts;

import com.alvexcore.repo.wfscheduler.service.WfSchedulerService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateAllJobByUser extends DeclarativeWebScript {
    private WfSchedulerService wfSchedulerService;

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req,
                                              Status status, Cache cache) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", false);
        try {
            Content content = req.getContent();
            if(content != null && content.getContent() != null){
                JSONParser parser = new JSONParser();
                JSONArray json = (JSONArray) parser.parse(content.getContent());
                String user = AuthenticationUtil.getFullyAuthenticatedUser();
                wfSchedulerService.updateAllScheduleInfo(user, json);
                model.put("success", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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