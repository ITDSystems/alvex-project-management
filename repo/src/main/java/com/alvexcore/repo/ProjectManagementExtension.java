/**
 * Copyright © 2013 ITD Systems
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

import com.alvexcore.repo.emails.EmailMessageEventListener;
import com.alvexcore.repo.emails.impl.EmailServiceImpl;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.impl.model.Permission;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

/**
 * ProjectManagement extension implementation
 */

public class ProjectManagementExtension extends RepositoryExtension {

	protected EmailServiceImpl emailService = null;
	protected EmailMessageEventListener emailListener = null;
	
	public void setEmailService(EmailServiceImpl emailService) {
		this.emailService = emailService;
	}
	
	public void setEmailListener(EmailMessageEventListener emailListener) {
		this.emailListener = emailListener;
	}
	
	// constructor
	public ProjectManagementExtension() throws Exception {
		id = "project-management";
		fileListPath = "alvex-project-management-file-list.txt";
		extInfoPath = "alvex-project-management.properties";
	}

	@Override
	public void init(boolean failIfInitialized) throws Exception {
		super.init(failIfInitialized);
		initializeStorage();
		this.emailService.registerEventListener(this.emailListener);
	}

	private void initializeStorage() throws Exception {
		PermissionService permissionService = extensionRegistry
				.getServiceRegistry().getPermissionService();
		permissionService.setPermission(getDataPath(),
				PermissionService.ALL_AUTHORITIES,
				PermissionService.CONTRIBUTOR, true);
	}


	void upgradeConfiguration(String oldVersion, String oldEdition) {
		//
		
	}
}
