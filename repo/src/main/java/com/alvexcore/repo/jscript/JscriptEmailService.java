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
import java.util.List;
import java.util.Map;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.ScriptableLinkedHashMap;

import com.alvexcore.repo.emails.EmailFolder;
import com.alvexcore.repo.emails.EmailProvider;
import com.alvexcore.repo.emails.EmailService;

/**
 * Javascript wrapper for EmailService
 * @author Alexey Ermakov
 *
 */
public class JscriptEmailService extends BaseScopableProcessorExtension{
	protected ServiceRegistry serviceRegistry;
	protected EmailService emailService;
	private ValueConverter converter = new ValueConverter();
	
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	/**
	 * Adds new email provider.
	 * @param provider Email provider
	 */
	public JscriptEmailProvider addProvider (Scriptable provider) throws Exception{
		@SuppressWarnings("unchecked")
		Map<String, Object> p = (Map<String, Object>) converter.convertValueForJava(provider);
		EmailProvider _p =  emailService.addProvider((String)p.get("id"), (String)p.get("incomingProto"), (String)p.get("incomingServer"), (Integer)p.get("incomingPort"), (String)p.get("outgoingProto"), (String)p.get("outgoingServer"), (Integer)p.get("outgoingPort"));
		return new JscriptEmailProvider(_p);
	}
	
	/**
	 * Returns list of registered email providers.
	 * @return registered email providers
	 */
	public Scriptable getProviders() {
		ArrayList<Serializable> result = new ArrayList<Serializable>();
		for (EmailProvider p: emailService.getProviders())
			result.add(new JscriptEmailProvider(p));
		return (Scriptable) converter.convertValueForScript(serviceRegistry, getScope(), null, result);
	}
	/**
	 * Cleans providers list.
	 */
	public void clearProviders() {
		emailService.clearProviders();
	}
	/**
	 * Checks if emails service is configured for user who invoked the method.
	 * @return true if service is configured and false otherwise
	 */
	public boolean isConfigured() {
		return emailService.isConfigured();
	}
	/**
	 * Configures emails service for user who invoked method. All next fetches will
	 * look only into explicitly configured folders.
	 * @param provider Email provider
	 * @param username Username to authenticate
	 * @param password Password to authenticate
	 * @param address Email address to send from
	 * @param realName Person to send from
	 * @throws Exception
	 */
	public void configure(String provider, String username, String password, String address, String realName) throws Exception{
		emailService.configure(provider, username, password, address, realName);
	}
	/**
	 * Drops emails configuration for current user.
	 */
	public void dropConfiguration() {
		emailService.dropConfiguration();
	}
	/**
	 * 
	 * Incrementally fetches emails. 
	 */
	public void fetch() {
		emailService.fetch();
	}
	/**
	 * Returns list of folders fetched from email server.
	 * @return folders
	 */
	public Scriptable getFolders() {
		ScriptableLinkedHashMap<String, Serializable> result = new ScriptableLinkedHashMap<String, Serializable>();
		for (EmailFolder f: emailService.getFolders())
			result.put(f.getName(), new JscriptEmailFolder(f, serviceRegistry, getScope()));
		return result;
	}
	/**
	 * Returns folder specified by name.
	 * @param name Folder name
	 * @return folder
	 */
	public JscriptEmailFolder getFolder(String name) {
		return new JscriptEmailFolder(emailService.getFolder(name), serviceRegistry, getScope());
	}
	/**
	 * Fetches folder list from repository
	 */
	public void fetchFolderList() {
		emailService.fetchFolderList();
	}
	/**
	 * Sends outgoing email.
	 * @param to List of to addresses
	 * @param cc List of cc addresses
	 * @param bcc List of bcc addresses
	 * @param subject Message subject
	 * @param body Message body
	 * @param attachments List of repository nodes to attach to message
	 * @param html Is this message html one or not
	 */
	@SuppressWarnings("unchecked")
	public void send(Scriptable to, Scriptable cc, Scriptable bcc, String subject, String body, Scriptable attachments, boolean html) throws Exception {
		List<String> _to = (List<String>) converter.convertValueForJava(to);
		List<String> _cc = (List<String>) converter.convertValueForJava(cc);
		List<String> _bcc = (List<String>) converter.convertValueForJava(bcc);
		List<NodeRef> atts = (List<NodeRef>) converter.convertValueForJava(attachments);
		emailService.send(_to, _cc, _bcc, subject, body, atts, html);
	}

}
