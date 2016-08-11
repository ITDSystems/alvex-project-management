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


/**
 * Email provider hold information about preconfigured emails servers: incoming and outgoing.
 * 
 * @author Alexey Ermakov
 *
 */
public interface EmailProvider {
	/**
	 * Returns email provider id.
	 * @return id
	 */
	public String getId();
	
	/**
	 * Returns protocol name for incoming email server.
	 * @return protocol name
	 */
	public String getIncomingProto();
	
	/**
	 * Returns hostname for incoming email server.
	 * @return server hostname
	 */
	public String getIncomingServer();
	
	/**
	 * Returns port for incoming email server.
	 * @return server port
	 */
	public int getIncomingPort();
	
	/**
	 * Returns protocol name for outgoing email server.
	 * @return protocol name
	 */
	public String getOutgoingProto();
	
	/**
	 * Returns hostname for outgoing email server.
	 * @return server hostname
	 */
	public String getOutgoingServer();
	
	/**
	 * Returns port for outgoing email server.
	 * @return server port
	 */
	public int getOutgoingPort();
}
