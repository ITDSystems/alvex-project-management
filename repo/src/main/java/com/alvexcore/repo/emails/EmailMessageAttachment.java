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

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Email message attachment.
 * @author Alexey Ermakov
 *
 */
public interface EmailMessageAttachment {
	/**
	 * Returns attachment file name.
	 * @return file name
	 */
	public String getFileName();
	/**
	 * Returns attachment mime type.
	 * @return mime type
	 */
	public String getMimeType();
	/**
	 * Returns attachment size in bytes.
	 * @return size in bytes
	 */
	public long getSize();
	/**
	 * Returns attachment content.
	 * @return content
	 */
	public InputStream getContent();
	/**
	 * Returns repository node reference.
	 * @return node reference
	 */
	public NodeRef getNodeRef();
}
