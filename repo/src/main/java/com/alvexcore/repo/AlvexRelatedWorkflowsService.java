package com.alvexcore.repo;

import java.util.List;
import org.alfresco.service.cmr.workflow.WorkflowInstance;

public interface AlvexRelatedWorkflowsService {

	public abstract boolean test();
	public abstract List<WorkflowInstance> getRelatedWorkflows(String workflowId);
	public abstract void attachRelatedWorkflow(String workflowId, String relatedId);
	public abstract void detachRelatedWorkflow(String workflowId, String relatedId);
}