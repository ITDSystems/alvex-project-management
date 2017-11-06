package com.alvexcore.repo.wfscheduler.service;

import java.util.*;

public class RunWorkflowBySchedule {
    private WfSchedulerService wfSchedulerService;

    private void execute(){
        wfSchedulerService.startWfByCurrentDate(getCalendar());
    }

    private Calendar getCalendar()
    {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        return cal;
    }

    public WfSchedulerService getWfSchedulerService() {
        return wfSchedulerService;
    }

    public void setWfSchedulerService(WfSchedulerService wfSchedulerService) {
        this.wfSchedulerService = wfSchedulerService;
    }
}
