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
package com.alvexcore.repo.emails;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * EmailMessage provides necessary methods to work with email messages
 * stored in repository.  
 * @author Alexey Ermakov
 *
 */
public interface EmailMessage {
	/**
	 * Returns email message subject
	 * @return subject
	 */
	public String getSubject();
	
	/**
	 * Returns list of To addresses
	 * @return list of addresses
	 */
	public List<String> getTo();
	
	/**
	 * Returns list of CC addresses
	 * @return list of addresses
	 */
	public List<String> getCC();	
	
	/**
	 * Returns from address
	 * @return from address
	 */
	public String getFrom();
	
	/**
	 * Returns message body
	 * @return body text
	 */
	public InputStream getBody();
	
	/**
	 * Returns message body
	 * @return body text string
	 */
	public String getBodyString();
	
	/**
	 * Returns message sent date
	 * @return sent date
	 */
	public Date getSentDate();
	
	/**
	 * Returns all message attachments
	 * @return list of attachments
	 */
	public List<EmailMessageAttachment> getAttachments();
	/**
	 * Returns message unique number.
	 * @return message uid
	 */
	public long getUID();
	/**
	 * Returns message id.
	 * @return message id
	 */
	public String getID();
	/**
	 * Returns node that contains message.
	 * @return Node reference
	 */
	public NodeRef getNodeRef();
}
