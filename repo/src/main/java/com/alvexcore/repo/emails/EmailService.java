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
 * Email service that gives an ability to work with emails from remote server
 * through Alfresco. It provides a way to fetch emails, perform a search over them,
 * send emails.
 * 
 * @author Alexey Ermakov
 *
 */
public interface EmailService {
	/**
	 * Adds new email provider.
	 * @param id Provider if
	 * @param incomingProto incoming server protocol
	 * @param incomingServer incoming server address
	 * @param incomingPort incoming server port
	 * @param outgoingProto outgoing server protocol
	 * @param outgoingServer outgoing server address
	 * @param outgoingPort outgoing server port
	 * @throws Exception
	 */
	public EmailProvider addProvider(String id, String incomingProto, String incomingServer, int incomingPort, String outgoingProto, String outgoingServer, int outgoingPort) throws Exception;
	/**
	 * Removes specified provider
	 * @param id provider id
	 * @throws Exception
	 */
	public void removeProvider(String id) throws Exception;
	/**
	 * Returns list of registered email providers.
	 * @return registered email providers
	 */
	public List<EmailProvider> getProviders();
	/**
	 * Cleans providers list.
	 */
	public void clearProviders();
	/**
	 * Checks if emails service is configured for user who invoked the method.
	 * @return true if service is configured and false otherwise
	 */
	public boolean isConfigured();
	/**
	 * Configures emails service for user who invoked method. All next fetches will
	 * look only into explicitly configured folders.
	 * @param provider Email provider
	 * @param username Username to authenticate
	 * @param password Password to authenticate
	 * @param address TODO
	 * @param realName TODO
	 * @throws Exception
	 */
	public void configure(String provider, String username, String password, String address, String realName) throws Exception;
	/**
	 * Drops emails configuration for current user.
	 */
	public void dropConfiguration();
	/**
	 * 
	 * Incrementally fetches emails. 
	 */
	public void fetch();
	/**
	 * Returns list of folders fetched from email server.
	 * @return folders
	 */
	public List<EmailFolder> getFolders();
	/**
	 * Returns folder specified by name.
	 * @param name Folder name
	 * @return folder
	 */
	public EmailFolder getFolder(String name);
	/**
	 * Fetches folder list from repository
	 */
	public void fetchFolderList();
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
	public void send(List<String> to, List<String> cc, List<String> bcc, String subject, String body, List<NodeRef> attachments, boolean html) throws Exception;
	
	/**
	 * Add event listener to be notified about system events.
	 * @param listener Listener
	 */
	public void registerEventListener(EmailMessageEventListener listener);
}
