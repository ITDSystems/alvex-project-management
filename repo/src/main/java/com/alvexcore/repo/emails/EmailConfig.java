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


/** Holds email configuration.
 * @author Alexey Ermakov
 *
 */
public class EmailConfig {
	protected String username;
	protected String password;
	protected String providerId;
	protected String address;
	protected String realName;

	/**
	 * Creates email config instance
	 * @param username Username
	 * @param password Password
	 * @param providerId Provider id
	 * @param address TODO
	 * @param realName TODO
	 */
	public EmailConfig(String username, String password, String providerId, String address, String realName) {
		this.username = username;
		this.password = password;
		this.providerId = providerId;
		this.address = address;
		this.realName = realName;
	}
	
	/**
	 * Returns username to authenticate against server.
	 * @return username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Returns password to authenticate against server.
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	
	/**
	 * Returns email provider id.
	 * @return provider id
	 */
	public String getProviderId() {
		return providerId;
	}
	
	/**
	 * Returns user's email address.
	 * @return email address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Returns user's real name.
	 * @return real name
	 */
	public String getRealName() {
		return realName;
	}
}
