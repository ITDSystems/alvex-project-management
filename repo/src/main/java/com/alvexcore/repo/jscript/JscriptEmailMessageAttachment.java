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

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.mozilla.javascript.Scriptable;

import com.alvexcore.repo.emails.EmailMessageAttachment;

public class JscriptEmailMessageAttachment implements Serializable {
	private static final long serialVersionUID = 1L;
	protected EmailMessageAttachment attachment;
	protected ServiceRegistry serviceRegistry; 
	protected Scriptable scope;

	public JscriptEmailMessageAttachment(EmailMessageAttachment attachment, ServiceRegistry serviceRegistry, Scriptable scope) {
		this.attachment = attachment;
		this.serviceRegistry = serviceRegistry;
		this.scope = scope;
	}
	
	/**
	 * Returns attachment file name.
	 * @return file name
	 */
	public String getFileName() {
		return attachment.getFileName();
	}
	/**
	 * Returns attachment mime type.
	 * @return mime type
	 */
	public String getMimeType() {
		return attachment.getMimeType();
	}
	/**
	 * Returns repository node for attachment
	 * @return node
	 */
	public ScriptNode getNode() {
		return new ScriptNode(attachment.getNodeRef(), serviceRegistry, scope);
	}

}
