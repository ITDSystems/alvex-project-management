package com.alvexcore.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.model.ContentModel;

import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowInstance;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;

import com.alvexcore.repo.AlvexContentModel;
import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

public class AlvexRelatedWorkflowsServiceImpl implements AlvexRelatedWorkflowsService, InitializingBean {

	protected ServiceRegistry serviceRegistry;
	protected WorkflowService workflowService;
	
	/*
	 * Startup function
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	@Override
	public boolean test()
	{
		return true;
	}
	
	protected NodeRef getBaseFolder(String workflowId)
	{
		NodeService nodeService = serviceRegistry.getNodeService();
		NodeRef rootRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		
		List<NodeRef> nodes = serviceRegistry.getSearchService().selectNodes(
				rootRef, "/sys:system/sys:alvex/alvex:data/alvex:advanced-workflows", 
				null, serviceRegistry.getNamespaceService(), false);
		if(nodes.size() != 1)
			return null;
		
		List<ChildAssociationRef> folders = nodeService.getChildAssocs(nodes.get(0));
		for( ChildAssociationRef folderRef : folders) {
			NodeRef folder = folderRef.getChildRef();
			String name = (String) nodeService.getProperty(folder, ContentModel.PROP_NAME);
			if(name.equals(workflowId)) {
				return folder;
			}
		}
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(11);
		properties.put(ContentModel.PROP_NAME, workflowId);
		
		return nodeService.createNode(nodes.get(0), ContentModel.ASSOC_CHILDREN, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, workflowId), 
				ContentModel.TYPE_CONTAINER, properties).getChildRef();
	}
	
	protected List<NodeRef> getRelationNodes(String workflowId)
	{
		ArrayList<NodeRef> relations = new ArrayList<NodeRef>();
		NodeService nodeService = serviceRegistry.getNodeService();
		NodeRef workflowFolder = getBaseFolder(workflowId);
		if(workflowFolder == null)
			return relations;
		
		// Loop through children (for parent -> child relations)
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(workflowFolder);
		for( ChildAssociationRef child : childAssocs) {
			NodeRef relation = child.getChildRef();
			String relationType = (String) nodeService.getProperty(relation, AlvexContentModel.PROP_RELATION_TYPE);
			if(AlvexContentModel.RELATION_TYPE_RELATED_WORKFLOW.equals(relationType)) {
				relations.add(relation);
			}
		}
		
		// Loop through assocs (for child -> parent relations)
		List<AssociationRef> assocs = nodeService.getTargetAssocs(workflowFolder, ContentModel.ASSOC_CHILDREN);
		for( AssociationRef assoc : assocs) {
			NodeRef relation = assoc.getTargetRef();
			String relationType = (String) nodeService.getProperty(relation, AlvexContentModel.PROP_RELATION_TYPE);
			if(AlvexContentModel.RELATION_TYPE_RELATED_WORKFLOW.equals(relationType)) {
				relations.add(relation);
			}
		}
		return relations;
	}
	
	@Override
	public List<WorkflowInstance> getRelatedWorkflows(String workflowId)
	{
		NodeService nodeService = serviceRegistry.getNodeService();
		ArrayList<WorkflowInstance> res = new ArrayList<WorkflowInstance>();
		
		List<NodeRef> relations = getRelationNodes(workflowId);
		
		for(NodeRef relation : relations) {
			String currentWorkflowId = (String) nodeService.getProperty(relation, AlvexContentModel.PROP_WORKFLOW_INSTANCE);
			String relatedWorkflowId = (String) nodeService.getProperty(relation, AlvexContentModel.PROP_RELATED_OBJECT);
			String targetWorkflowId = (currentWorkflowId.equals(workflowId) ? relatedWorkflowId : currentWorkflowId );
			WorkflowInstance targetWorkflow = workflowService.getWorkflowById(targetWorkflowId);
			res.add(targetWorkflow);
		}
		
		return res;
	}
	
	@Override
	public void attachRelatedWorkflow(String workflowId, String relatedId)
	{
		NodeService nodeService = serviceRegistry.getNodeService();
		List<NodeRef> relations = getRelationNodes(workflowId);
		// If relation exists - just return
		for(NodeRef relation : relations) {
			String currentWorkflowId = (String) nodeService.getProperty(relation, AlvexContentModel.PROP_WORKFLOW_INSTANCE);
			String relatedWorkflowId = (String) nodeService.getProperty(relation, AlvexContentModel.PROP_RELATED_OBJECT);
			if(currentWorkflowId.equals(workflowId) && relatedWorkflowId.equals(relatedId))
				return;
		}
		
		NodeRef workflowFolder = getBaseFolder(workflowId);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(11);
		properties.put(AlvexContentModel.PROP_WORKFLOW_INSTANCE, workflowId);
		properties.put(AlvexContentModel.PROP_RELATED_OBJECT, relatedId);
		properties.put(AlvexContentModel.PROP_RELATION_TYPE, AlvexContentModel.RELATION_TYPE_RELATED_WORKFLOW);
		
		String name = GUID.generate();
		NodeRef newRelation = nodeService.createNode(workflowFolder, ContentModel.ASSOC_CHILDREN, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), 
				AlvexContentModel.TYPE_WORKFLOW_RELATION, properties).getChildRef();
		
		NodeRef relatedWorkflowFolder = getBaseFolder(relatedId);
		nodeService.createAssociation(relatedWorkflowFolder, newRelation, ContentModel.ASSOC_CHILDREN);
	}
	
	@Override
	public void detachRelatedWorkflow(String workflowId, String relatedId)
	{
		NodeService nodeService = serviceRegistry.getNodeService();
		List<NodeRef> relations = getRelationNodes(workflowId);
		for(NodeRef relation : relations) {
			String relatedWorkflowId = (String) nodeService.getProperty(relation, AlvexContentModel.PROP_RELATED_OBJECT);
			if(relatedWorkflowId.equals(relatedId)) {
				nodeService.deleteNode(relation);
				return;
			}
		}
	}
}