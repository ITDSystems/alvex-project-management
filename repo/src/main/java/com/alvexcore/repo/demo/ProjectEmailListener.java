package com.alvexcore.repo.demo;

import com.alvexcore.repo.emails.EmailMessage;
import com.alvexcore.repo.emails.EmailMessageEventListener;
import com.alvexcore.repo.emails.EmailMessageAttachment;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public class ProjectEmailListener implements EmailMessageEventListener {

	protected ServiceRegistry serviceRegistry;
	protected NodeService nodeService;
	
	final static String ALVEXCM_MODEL_URI = "http://alvexcore.com/prefix/alvexcm";
	final static QName PROP_EMAIL = QName.createQName(ALVEXCM_MODEL_URI, "contactEmail");
	
	final static QName TYPE_CONV = QName.createQName(ALVEXCM_MODEL_URI, "conversationItem");
	final static QName PROP_SUMMARY = QName.createQName(ALVEXCM_MODEL_URI, "conversationSummary");
	final static QName PROP_TYPE = QName.createQName(ALVEXCM_MODEL_URI, "conversationType");
	final static QName PROP_DATE = QName.createQName(ALVEXCM_MODEL_URI, "conversationDate");
	final static QName PROP_DETAILS = QName.createQName(ALVEXCM_MODEL_URI, "conversationDetails");
	final static QName ASSOC_PEOPLE = QName.createQName(ALVEXCM_MODEL_URI, "conversationParticipants");
	final static QName ASSOC_FILES = QName.createQName(ALVEXCM_MODEL_URI, "conversationAttachments");
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
		this.nodeService = serviceRegistry.getNodeService();
	}
	
	@Override
	public void onNewMessageReceived(EmailMessage message)
	{
		
		String from = message.getFrom();
		List<String> to = message.getTo();
		List<String> cc = message.getCC();
		List<String> involved = new ArrayList<String>();
		involved.add(from);
		involved.addAll(cc);
		involved.addAll(to);
		
		NodeRef folder = new NodeRef("workspace://SpacesStore/6223645d-db3a-44f0-a3c7-81c32e5264b4");
		List<ChildAssociationRef> contacts = nodeService.getChildAssocs(folder);
		for(ChildAssociationRef c : contacts)
		{
			NodeRef contact = c.getChildRef();
			String email = (String)nodeService.getProperty(contact, PROP_EMAIL);
			if( involved.contains(email) )
				addEmailToProject(message);
		}
		
	}
	
	protected void addEmailToProject(EmailMessage message)
	{
		NodeRef folder = new NodeRef("workspace://SpacesStore/010eef85-74ed-4d53-999a-3daa048b2210");
		
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>(11);
		properties.put(PROP_TYPE, "email");
		properties.put(PROP_DATE, message.getSentDate());
		properties.put(PROP_SUMMARY, message.getSubject());
		properties.put(PROP_DETAILS, message.getBodyString());

		NodeRef item = nodeService.createNode( folder, ContentModel.ASSOC_CONTAINS, 
			QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, message.getID()), 
			TYPE_CONV, properties).getChildRef();
		for( EmailMessageAttachment a : message.getAttachments())
		{
			nodeService.createAssociation(item, a.getNodeRef(), ASSOC_FILES);
		}
	}
	
}