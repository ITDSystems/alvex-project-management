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
package com.alvexcore.repo.jscript;

import java.io.Serializable;
import java.util.ArrayList;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.ServiceRegistry;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.ScriptableLinkedHashMap;

import com.alvexcore.repo.emails.EmailFolder;
import com.alvexcore.repo.emails.EmailMessage;

/**
 * Javascript wrapper for email folder.
 * @author Alexey Ermakov
 *
 */
public class JscriptEmailFolder implements Serializable {	
	private static final long serialVersionUID = 1L;
	protected EmailFolder folder;
	protected ServiceRegistry serviceRegistry;
	protected Scriptable scope;
	protected ValueConverter converter = new ValueConverter();

	public JscriptEmailFolder(EmailFolder folder, ServiceRegistry serviceRegistry, Scriptable scope) {
		this.folder = folder;
		this.serviceRegistry = serviceRegistry;
		this.scope = scope;
	}
	
	/**
	 * Returns list of all emails in folder
	 * @return emails
	 */
	public Scriptable getEmails() {
		ArrayList<Serializable> result = new ArrayList<Serializable>();
		for (EmailMessage m: folder.getEmails())
			result.add(new JscriptEmailMessage(m, serviceRegistry, scope));
		return (Scriptable) converter.convertValueForScript(serviceRegistry, scope, null, result);
	}
	/**
	 * Returns node reference for email folder.
	 * @return node reference
	 */
	public ScriptNode getNode() {
		return new ScriptNode(folder.getNodeRef(), serviceRegistry);
	}
	/**
	 * Returns email message specified by UID.
	 * @param uid Message UID
	 * @return email message
	 */
	public JscriptEmailMessage getMessageByUID(int uid) {
		return new JscriptEmailMessage(folder.getMessageByUID(uid), serviceRegistry, scope);
	}
	/**
	 * Returns email message specified by id.
	 * @param id Message ID
	 * @return email message
	 */
	public JscriptEmailMessage getMessageByID(String id) {
		return new JscriptEmailMessage(folder.getMessageByID(id), serviceRegistry, scope);
	}
	/**
	 * Returns email message specified by its number in folder.
	 * @param number Message number
	 * @return email message
	 */
	public JscriptEmailMessage getMessageByNumber(int number) {
		return new JscriptEmailMessage(folder.getMessageByNumber(number), serviceRegistry, scope);
	}
	/**
	 * Returns folder name.
	 * @return folder name
	 */
	public String getName() {
		return folder.getName();
	}
	/**
	 * Returns subfolders list.
	 * @return
	 */
	public Scriptable getSubfolders() {
		ScriptableLinkedHashMap<String, Serializable> result = new ScriptableLinkedHashMap<String, Serializable>();
		for (EmailFolder f: folder.getSubfolders())
			result.put(f.getName(), new JscriptEmailFolder(f, serviceRegistry, scope));
		return (Scriptable) converter.convertValueForScript(serviceRegistry, scope, null, result);
	}
	/**
	 * Returns true if fetch is active for this folder and false otherwise
	 * @return true or false
	 */	
	public boolean isFetchActive() {
		return folder.isFetchActive();
	}
	
}
