package com.alvexcore.repo.wfscheduler.model;

import org.alfresco.service.namespace.QName;

/**
 * Created by Fedor on 14.03.2017.
 */
public interface WfSchedulerModel {
    public static final String MODEL_URL = "http://www.system.ru/model/wfScheduler/1.0";
    public static final String MODEL_PREFIX = "wfsch";

    public static final QName TYPE_CRON = QName.createQName(MODEL_URL, "cronInfo");
    public static final QName ASSOC_OWNER = QName.createQName(MODEL_URL, "owner");
    public static final QName PROP_WORKFLOW_DATA = QName.createQName(MODEL_URL, "workflowData");
}
