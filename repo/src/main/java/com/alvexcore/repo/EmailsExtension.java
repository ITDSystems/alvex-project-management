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

package com.alvexcore.repo;

import com.alvexcore.repo.emails.impl.EmailServiceImpl;


/**
 * Emails extension implementation
 */

public class EmailsExtension extends RepositoryExtension {
	
	protected EmailServiceImpl emailService;
	
	public void setEmailService(EmailServiceImpl emailService) {
		this.emailService = emailService;
		emailService.setEmailsExtension(this);
	}
	
	public EmailsExtension() throws Exception {
		id = "emails";
		fileListPath = "alvex-emails-file-list.txt";
		extInfoPath = "alvex-emails.properties";
	}

	@Override
	public void init(boolean failIfInitialized) throws Exception {
		super.init(failIfInitialized);
	}


	void upgradeConfiguration(String oldVersion, String oldEdition) {
		//	
	}
}
