/**
 * Copyright © 2014 ITD Systems
 *
 * This file is part of Alvex
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alvexcore.repo.emails.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;

import com.alvexcore.repo.AlvexContentModel;
import com.alvexcore.repo.emails.EmailMessage;
import com.alvexcore.repo.emails.EmailMessageAttachment;

public class EmailMessageImpl implements EmailMessage {

	protected NodeRef nodeRef;
	protected NodeService nodeService;
	protected ContentService contentService;

	public EmailMessageImpl(NodeRef nodeRef, NodeService nodeService, ContentService contentService) {
		this.nodeRef = nodeRef;
		this.nodeService = nodeService;
		this.contentService = contentService;
	}

	@Override
	public String getSubject() {
		return (String)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_SUBJECT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getTo() {
		return (List<String>)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_TO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCC() {
		return (List<String>)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_CC);
	}

	@Override
	public String getFrom() {
		return (String)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_FROM);
	}

	@Override
	public InputStream getBody() {
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getContentInputStream();
	}
	
	@Override
	public String getBodyString() {
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getContentString();
	}	

	@Override
	public Date getSentDate() {
		return (Date)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_SENT_DATE);
	}

	@Override
	public List<EmailMessageAttachment> getAttachments() {
		List<EmailMessageAttachment> result = new ArrayList<EmailMessageAttachment>();
		for (ChildAssociationRef assoc: nodeService.getChildAssocs(nodeRef, AlvexContentModel.ASSOC_EMAIL_ATTACHMENTS, RegexQNamePattern.MATCH_ALL))
			result.add(new EmailMessageAttachmentImpl(assoc.getChildRef(), nodeService, contentService));
		return result;
	}

	@Override
	public long getUID() {
		return (Long)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_UID);
	}

	@Override
	public String getID() {
		return (String)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_ID);
	}

	@Override
	public NodeRef getNodeRef() {
		return nodeRef;
	}


}
