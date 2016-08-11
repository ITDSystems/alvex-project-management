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
import java.util.Date;

import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.ServiceRegistry;
import org.mozilla.javascript.Scriptable;

import com.alvexcore.repo.emails.EmailMessage;
import com.alvexcore.repo.emails.EmailMessageAttachment;

public class JscriptEmailMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	protected EmailMessage msg;
	protected ServiceRegistry serviceRegistry;
	protected Scriptable scope; 
	protected ValueConverter converter = new ValueConverter();
	
	public JscriptEmailMessage(EmailMessage msg, ServiceRegistry serviceRegistry, Scriptable scope) {
		this.msg = msg;
		this.serviceRegistry = serviceRegistry;
		this.scope = scope;
	}
	
	/**
	 * Returns wrapped email message.
	 * @return email message
	 */
	public EmailMessage getEmailMessage() {
		return msg;
	}
	
	/**
	 * Returns email message subject
	 * @return subject
	 */
	public String getSubject() {
		return msg.getSubject();
	}
	
	/**
	 * Returns list of To addresses
	 * @return list of addresses
	 */
	public Scriptable getTo() {
		return (Scriptable) converter.convertValueForScript(serviceRegistry, scope, null, (Serializable)msg.getTo());
	}
	
	/**
	 * Returns list of CC addresses
	 * @return list of addresses
	 */
	public Scriptable getCC() {
		return (Scriptable) converter.convertValueForScript(serviceRegistry, scope, null, (Serializable)msg.getCC());
	}	
	
	/**
	 * Returns from address
	 * @return from address
	 */
	public String getFrom() {
		return msg.getFrom();
	}
	
	/**
	 * Returns message body
	 * @return body text
	 */
	public String getBody() {
		return msg.getBodyString();
	}
	
	/**
	 * Returns message sent date
	 * @return sent date
	 */
	public Date getSentDate() {
		return msg.getSentDate();
	}
	
	/**
	 * Returns all message attachments
	 * @return list of attachments
	 */
	public Scriptable getAttachments() {
		ArrayList<Serializable> result = new ArrayList<Serializable>();
		for (EmailMessageAttachment att: msg.getAttachments())
			result.add(new JscriptEmailMessageAttachment(att, serviceRegistry, scope));
		return (Scriptable) converter.convertValueForScript(serviceRegistry, scope, null, result);
			
	}
	/**
	 * Returns message unique number.
	 * @return message uid
	 */
	public long getUID() {
		return msg.getUID();
	}
	/**
	 * Returns message id.
	 * @return message id
	 */
	public String getID() {
		return msg.getID();
	}
	
	/**
	 * Returns node that stores this message
	 * @return Repository node
	 */
	public ScriptNode getNode() {
		return new ScriptNode(msg.getNodeRef(), serviceRegistry, scope);
	}

}
