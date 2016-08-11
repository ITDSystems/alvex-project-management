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

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Email folder that exposes emails mgmt functionality
 * @author Alexey Ermakov
 *
 */
public interface EmailFolder {
	/**
	 * Returns list of all emails in folder
	 * @return emails
	 */
	public List<EmailMessage> getEmails();
	/**
	 * Returns node reference for email folder.
	 * @return node reference
	 */
	public NodeRef getNodeRef();
	/**
	 * Returns email message specified by UID.
	 * @param uid Message UID
	 * @return email message
	 */
	public EmailMessage getMessageByUID(int uid);
	/**
	 * Returns email message specified by id.
	 * @param id Message ID
	 * @return email message
	 */
	public EmailMessage getMessageByID(String id);
	/**
	 * Returns email message specified by its number in folder.
	 * @param number Message number
	 * @return email message
	 */
	public EmailMessage getMessageByNumber(int number);
	/**
	 * Returns folder name.
	 * @return folder name
	 */
	public String getName();
	/**
	 * Returns subfolders list.
	 * @return
	 */
	public List<EmailFolder> getSubfolders();
	/**
	 * Returns true if fetch is active for this folder and false otherwise
	 * @return true or false
	 */
	public boolean isFetchActive();
}
