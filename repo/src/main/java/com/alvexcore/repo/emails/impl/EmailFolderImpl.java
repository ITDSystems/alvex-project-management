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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.alvexcore.repo.AlvexContentModel;
import com.alvexcore.repo.emails.EmailFolder;
import com.alvexcore.repo.emails.EmailMessage;

public class EmailFolderImpl implements EmailFolder {
	
	protected NodeRef folder = null;
	protected NodeService nodeService;
	protected ContentService contentService;
	
	public EmailFolderImpl(NodeRef folder, NodeService nodeService, ContentService contentService) {
		this.folder = folder;
		this.nodeService = nodeService;
		this.contentService = contentService;
	}

	@Override
	public List<EmailMessage> getEmails() {
		List<EmailMessage> result = new ArrayList<EmailMessage>();
		Set<QName> types = new HashSet<QName>();
		types.add(AlvexContentModel.TYPE_EMAIL_MESSAGE);
		for (ChildAssociationRef assoc: nodeService.getChildAssocs(folder, types))
			result.add(new EmailMessageImpl(assoc.getChildRef(), nodeService, contentService));
		return result;
	}

	@Override
	public NodeRef getNodeRef() {
		return folder;
	}

	@Override
	public EmailMessage getMessageByUID(int uid) {
		List<ChildAssociationRef> refs = nodeService.getChildAssocsByPropertyValue(folder, AlvexContentModel.PROP_EMAIL_UID, uid);
		if (refs.size() == 0)
			return null;
		// FIXME how to handle case refs.size() > 1 ?
		return new EmailMessageImpl(refs.get(0).getChildRef(), nodeService, contentService);
	}

	@Override
	public EmailMessage getMessageByID(String id) {
		List<ChildAssociationRef> refs = nodeService.getChildAssocsByPropertyValue(folder, AlvexContentModel.PROP_EMAIL_ID, id);
		if (refs.size() == 0)
			return null;
		// FIXME how to handle case refs.size() > 1 ?
		return new EmailMessageImpl(refs.get(0).getChildRef(), nodeService, contentService);
	}

	@Override
	public EmailMessage getMessageByNumber(int number) {
		List<EmailMessage> messages = getEmails();
		if (messages.size() > number)
			return messages.get(number);
		else
			// FIXME should exception be thrown here?
			return null;
	}

	@Override
	public String getName() {
		return (String)nodeService.getProperty(folder, ContentModel.PROP_NAME);
	}

	@Override
	public List<EmailFolder> getSubfolders() {
		Set<QName> types = new HashSet<QName>();
		types.add(AlvexContentModel.TYPE_EMAIL_FOLDER);
		List<EmailFolder> result = new ArrayList<EmailFolder>();
		for (ChildAssociationRef assoc: nodeService.getChildAssocs(folder, types))
			result.add(new EmailFolderImpl(assoc.getChildRef(), nodeService, contentService));
		return result;
	}

	@Override
	public boolean isFetchActive() {
		return (Boolean)nodeService.getProperty(folder, AlvexContentModel.PROP_EMAIL_FOLDER_FETCH_ACTIVE);
	}
}
