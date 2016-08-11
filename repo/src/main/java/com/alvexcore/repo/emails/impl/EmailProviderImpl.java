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

import org.alfresco.service.cmr.repository.NodeRef;

import com.alvexcore.repo.emails.EmailProvider;

public class EmailProviderImpl implements EmailProvider {
	
	protected String id;
	protected String incomingProto;
	protected String incomingServer;
	protected int incomingPort;
	protected String outgoingProto;
	protected String outgoingServer;
	protected int outgoingPort;
	protected NodeRef nodeRef;

	public EmailProviderImpl(String id, String incomingProto, String incomingServer, int incomingPort, String outgoingProto, String outgoingServer, int outgoingPort, NodeRef nodeRef) {
		this.id = id;
		this.incomingProto = incomingProto;
		this.incomingServer = incomingServer;
		this.incomingPort = incomingPort;
		this.outgoingProto = outgoingProto;
		this.outgoingServer = outgoingServer;
		this.outgoingPort = outgoingPort;
		this.nodeRef = nodeRef;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIncomingProto() {
		return incomingProto;
	}

	@Override
	public String getIncomingServer() {
		return incomingServer;
	}

	@Override
	public int getIncomingPort() {
		return incomingPort;
	}

	@Override
	public String getOutgoingProto() {
		return outgoingProto;
	}

	@Override
	public String getOutgoingServer() {
		return outgoingServer;
	}

	@Override
	public int getOutgoingPort() {
		return outgoingPort;
	}
	
	public NodeRef getNodeRef() {
		return nodeRef;
	}

}
