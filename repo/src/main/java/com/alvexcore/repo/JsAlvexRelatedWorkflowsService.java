package com.alvexcore.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.mozilla.javascript.Scriptable;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.workflow.WorkflowInstance;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;

public class JsAlvexRelatedWorkflowsService extends BaseScopableProcessorExtension {

	protected ValueConverter converter = null;
	protected ServiceRegistry serviceRegistry;
	protected AlvexRelatedWorkflowsService alvexRelatedWorkflowsService;
	
	public boolean test()
	{
		return alvexRelatedWorkflowsService.test();
	}
	
	public void setAlvexRelatedWorkflowsService(AlvexRelatedWorkflowsService alvexRelatedWorkflowsService) {
		this.alvexRelatedWorkflowsService = alvexRelatedWorkflowsService;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public List<WorkflowInstance> getRelatedWorkflows(String workflowId)
	{
		//ArrayList<Serializable> result = new ArrayList<Serializable>();
		
		List<WorkflowInstance> relatedWorkflows = alvexRelatedWorkflowsService.getRelatedWorkflows(workflowId);
		return relatedWorkflows;
		/*for (HashMap<String, String> wfl: relatedWorkflows)
		{
			result.add(wfl);
		}
		
		Scriptable activeInstancesScriptable =
			(Scriptable)getValueConverter().convertValueForScript(this.serviceRegistry, getScope(), null, result);
		
		return activeInstancesScriptable;*/
	}
	
	public void attachRelatedWorkflow(String workflowId, String relatedId)
	{
		alvexRelatedWorkflowsService.attachRelatedWorkflow(workflowId, relatedId);
	}
	
	public void detachRelatedWorkflow(String workflowId, String relatedId)
	{
		alvexRelatedWorkflowsService.detachRelatedWorkflow(workflowId, relatedId);
	}
	
	/**
	* Gets the value converter
	* 
	* @return the value converter
	*/
	protected ValueConverter getValueConverter()
	{
		if (converter == null)
		{
			converter = new ValueConverter();
		}
		return converter;
	}
}