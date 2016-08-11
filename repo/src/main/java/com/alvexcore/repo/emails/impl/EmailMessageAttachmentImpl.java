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

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

import com.alvexcore.repo.AlvexContentModel;
import com.alvexcore.repo.emails.EmailMessageAttachment;

public class EmailMessageAttachmentImpl implements EmailMessageAttachment {
	
	protected NodeRef nodeRef;
	protected NodeService nodeService;
	protected ContentService contentService;
	
	public EmailMessageAttachmentImpl(NodeRef nodeRef, NodeService nodeService, ContentService contentService) {
		this.nodeRef = nodeRef;
		this.nodeService = nodeService;
		this.contentService = contentService;
	}

	@Override
	public String getFileName() {
		return (String)nodeService.getProperty(nodeRef, AlvexContentModel.PROP_EMAIL_REAL_NAME);
	}

	@Override
	public String getMimeType() {
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getMimetype();
	}

	@Override
	public long getSize() {
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getSize();
	}

	@Override
	public InputStream getContent() {
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getContentInputStream();
	}

	@Override
	public NodeRef getNodeRef() {
		return nodeRef;
	}

}
