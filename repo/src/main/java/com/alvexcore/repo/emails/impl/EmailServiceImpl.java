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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.alfresco.email.server.impl.subetha.SubethaEmailMessage;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.email.EmailMessageException;
import org.alfresco.service.cmr.email.EmailMessagePart;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alvexcore.repo.AlvexContentModel;
import com.alvexcore.repo.emails.EmailConfig;
import com.alvexcore.repo.emails.EmailFolder;
import com.alvexcore.repo.emails.EmailMessage;
import com.alvexcore.repo.emails.EmailMessageEventListener;
import com.alvexcore.repo.emails.EmailProvider;
import com.alvexcore.repo.emails.EmailService;
import com.alvexcore.repo.EmailsExtension;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.smtp.SMTPTransport;

interface ExtendedEmailMessage extends org.alfresco.service.cmr.email.EmailMessage {
	public long getUID();

	public String getID();

	public String getInReplyTo();
}

class AlvexEmailMessage implements ExtendedEmailMessage {
	private static final long serialVersionUID = 1L;
	protected static final String IN_REPLY_TO = "In-Reply-To";
	protected static final String ERR_EXTRACTING_IN_REPLY_TO = "Failed to extract in-reply-to header value";
	protected static final String ERR_EXTRACTING_ATTRS = "Failed to extract additional message attributes";

	protected SubethaEmailMessage wrappedMessage = null;
	protected long uid = -1;
	protected String id = null;
	protected String inReplyTo = null;

	public AlvexEmailMessage(MimeMessage msg, UIDFolder folder) throws MessagingException {
		// FIXME creating new MimeMessage is a work-around
		// for more details see http://dmitriy-bannikov.blogspot.ru/2013/05/javaxmailmessagingexception-unable-to.html
		wrappedMessage = new SubethaEmailMessage(new MimeMessage(msg));
		parseMessage(msg, folder);
	}

	protected void parseMessage(MimeMessage msg, UIDFolder folder) {
		try {
			if (uid == -1) {
				uid = folder.getUID(msg);
			}
			if (id == null) {
				id = msg.getMessageID();
			}
			if (inReplyTo == null) {
				String[] headers = msg.getHeader(IN_REPLY_TO);
				if (headers != null) {
					if (headers.length == 1)
						inReplyTo = headers[0];
					else if (headers.length > 1)
						throw new EmailMessageException(
								ERR_EXTRACTING_IN_REPLY_TO,
								"Not more than one " + IN_REPLY_TO
										+ " header expected");
				}
			}
		} catch (MessagingException e) {
			throw new EmailMessageException(ERR_EXTRACTING_ATTRS,
					e.getMessage());
		}
	}

	@Override
	public String getFrom() {
		return wrappedMessage.getFrom();
	}

	@Override
	public String getTo() {
		return wrappedMessage.getTo();
	}

	@Override
	public List<String> getCC() {
		return wrappedMessage.getCC();
	}

	@Override
	public Date getSentDate() {
		return wrappedMessage.getSentDate();
	}

	@Override
	public String getSubject() {
		return wrappedMessage.getSubject();
	}

	@Override
	public EmailMessagePart getBody() {
		return wrappedMessage.getBody();
	}

	@Override
	public EmailMessagePart[] getAttachments() {
		return wrappedMessage.getAttachments();
	}

	@Override
	public long getUID() {
		return uid;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getInReplyTo() {
		return inReplyTo;
	}
}


class RepositoryDataSource implements DataSource {
	ContentService contentService;
	NodeRef nodeRef;
	String name;
	
	public RepositoryDataSource(NodeRef nodeRef, String name, ContentService contentService) {
		this.nodeRef = nodeRef;
		this.contentService = contentService;
		this.name = name;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getContentInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Not supported");
	}

	@Override
	public String getContentType() {
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getMimetype();
	}

	@Override
	public String getName() {
		return name;
	}
	
}

public class EmailServiceImpl implements EmailService, InitializingBean {
	
	protected ServiceRegistry serviceRegistry;
	protected NodeService nodeService;
	protected PersonService personService;
	protected ContentService contentService;
	protected AuthenticationService authenticationService;
	protected MimetypeService mimetypeService;
	protected EmailsExtension emailsExtension;
	protected List<EmailProvider> providers = null;
	protected List<EmailMessageEventListener> listeners = new ArrayList<EmailMessageEventListener>();
	private static Log logger = LogFactory.getLog(EmailServiceImpl.class);

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public void setEmailsExtension(EmailsExtension emailsExtension) {
		this.emailsExtension = emailsExtension;
	}

	@Override
	public EmailProvider addProvider(String id, String incomingProto,
			String incomingServer, int incomingPort, String outgoingProto,
			String outgoingServer, int outgoingPort) throws Exception {
		try {
			getEmailProvider(id);
			throw new Exception("Provider already exists");
		} catch (NoSuchProviderException e) {
			NodeRef path = emailsExtension.getDataPath();
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(AlvexContentModel.PROP_EMAIL_PROVIDER_INCOMING_PROTO, incomingProto);
			props.put(AlvexContentModel.PROP_EMAIL_PROVIDER_INCOMING_SERVER, incomingServer);
			props.put(AlvexContentModel.PROP_EMAIL_PROVIDER_INCOMING_PORT, incomingPort);
			props.put(AlvexContentModel.PROP_EMAIL_PROVIDER_OUTGOING_PROTO, outgoingProto);
			props.put(AlvexContentModel.PROP_EMAIL_PROVIDER_OUTGOING_SERVER, outgoingServer);
			props.put(AlvexContentModel.PROP_EMAIL_PROVIDER_OUTGOING_PORT, outgoingPort);
			NodeRef nodeRef = nodeService.createNode(path, ContentModel.ASSOC_CHILDREN, QName.createQName(AlvexContentModel.ALVEXEM_MODEL_URI, id), AlvexContentModel.TYPE_EMAIL_PROVIDER, props).getChildRef();
			EmailProviderImpl provider = new EmailProviderImpl(id, incomingProto, incomingServer, incomingPort, outgoingProto, outgoingServer, outgoingPort, nodeRef);
			providers.add(provider);
			return provider;
		}		
	}

	@Override
	public void removeProvider(String id) throws Exception {
		EmailProvider provider = getEmailProvider(id);
		nodeService.deleteNode(((EmailProviderImpl)provider).getNodeRef());
		for (int i = 0; i < providers.size(); i++)
			if (providers.get(i).getId().equals(id)) {
				providers.remove(i);
				break;
			}
	}	
	
	protected void loadProviders() {
		providers = new ArrayList<EmailProvider>();
		for (ChildAssociationRef ref: nodeService.getChildAssocs(emailsExtension.getDataPath(), AlvexContentModel.TYPE_EMAIL_PROVIDER, RegexQNamePattern.MATCH_ALL)) {
			Map<QName, Serializable> props = nodeService.getProperties(ref.getChildRef());
			providers.add(new EmailProviderImpl(ref.getQName().getLocalName(), (String)props.get(AlvexContentModel.PROP_EMAIL_PROVIDER_INCOMING_PROTO), (String)props.get(AlvexContentModel.PROP_EMAIL_PROVIDER_INCOMING_SERVER), (Integer)props.get(AlvexContentModel.PROP_EMAIL_PROVIDER_INCOMING_PORT), (String)props.get(AlvexContentModel.PROP_EMAIL_PROVIDER_OUTGOING_PROTO), (String)props.get(AlvexContentModel.PROP_EMAIL_PROVIDER_OUTGOING_SERVER), (Integer)props.get(AlvexContentModel.PROP_EMAIL_PROVIDER_OUTGOING_PORT), ref.getChildRef()));
		}
	}
	
	@Override
	public List<EmailProvider> getProviders() {
		if (providers == null)
			loadProviders();
		return providers;
	}
	
	protected NodeRef getUserHomeFolder() {
		String userName = authenticationService.getCurrentUserName();
		NodeRef personNode = personService.getPersonOrNull(userName);
		return (NodeRef)nodeService.getProperty(personNode, ContentModel.PROP_HOMEFOLDER);
	}
	
	protected NodeRef getEmailConfigNode() {
		NodeRef homeFolderNode = getUserHomeFolder();
		if (homeFolderNode == null)
			return null;
		List<ChildAssociationRef> childs = nodeService.getChildAssocs(homeFolderNode, ContentModel.ASSOC_CONTAINS, AlvexContentModel.ASSOC_NAME_EMAILS);
		if (childs.size() == 0)
			return null;
		return childs.get(0).getChildRef();
	}
	
	protected EmailConfig getConfig() {
		NodeRef container = getEmailConfigNode();
		if (container == null)
			return null;
		Map<QName, Serializable> props = nodeService.getProperties(container);
		String username = (String)props.get(AlvexContentModel.PROP_EMAIL_CONFIG_USERNAME);
		String password = (String)props.get(AlvexContentModel.PROP_EMAIL_CONFIG_PASSWORD);
		String address = (String)props.get(AlvexContentModel.PROP_EMAIL_CONFIG_ADDRESS);
		String realName = (String)props.get(AlvexContentModel.PROP_EMAIL_CONFIG_REAL_NAME);
		List<AssociationRef> p = nodeService.getTargetAssocs(container, AlvexContentModel.ASSOC_EMAIL_PROVIDER);
		return new EmailConfig(username, password, nodeService.getPrimaryParent(p.get(0).getTargetRef()).getQName().getLocalName(), address, realName);
	}

	@Override
	public boolean isConfigured() {
		return getConfig() != null;
	}

	@Override
	public void configure(String provider, String username,	String password, String address, String realName) throws Exception {
		NodeRef container = getEmailConfigNode();
		if (container == null){
			NodeRef homeFolderNode = getUserHomeFolder();
			container = nodeService.createNode(homeFolderNode, ContentModel.ASSOC_CONTAINS, AlvexContentModel.ASSOC_NAME_EMAILS, AlvexContentModel.TYPE_EMAIL_CONTAINER).getChildRef();
		}
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		props.put(AlvexContentModel.PROP_EMAIL_CONFIG_USERNAME, username);
		props.put(AlvexContentModel.PROP_EMAIL_CONFIG_PASSWORD, password);
		props.put(AlvexContentModel.PROP_EMAIL_CONFIG_ADDRESS, address);
		props.put(AlvexContentModel.PROP_EMAIL_CONFIG_REAL_NAME, realName);
		nodeService.setProperties(container, props);
		nodeService.createAssociation(container, ((EmailProviderImpl)getEmailProvider(provider)).getNodeRef(), AlvexContentModel.ASSOC_EMAIL_PROVIDER);
		nodeService.addAspect(container, ContentModel.ASPECT_HIDDEN, null);
		fetchFolderList();
	}

	@Override
	public void dropConfiguration() {
		NodeRef configNode = getEmailConfigNode();
		nodeService.deleteNode(configNode);

	}
	
	protected NodeRef createNode(NodeRef parentRef, QName assocTypeQName, QName nodeTypeQName, Map<QName, Serializable> properties) {
		return nodeService.createNode(parentRef, assocTypeQName, QName.createQName(AlvexContentModel.ALVEXEM_MODEL_URI, GUID.generate()), nodeTypeQName, properties).getChildRef();	
	}
	
	protected void writeContent(NodeRef ref, EmailMessagePart content) {
		if (content == null)
			return;
		ContentWriter writer = contentService.getWriter(ref, ContentModel.PROP_CONTENT, true);
		String mime = mimetypeService.guessMimetype(content.getFileName());
		// String contentType = content.getContentType();
		String contentEncoding = content.getEncoding();
		// FIXME it's a WA to handle case when mimetype is too long
		if (mime != null && mime.length() < 100)
			writer.setMimetype(mime);
		else
			writer.guessMimetype(null);
		// FIXME it's a WA to handle case when encoding is too long
		if (contentEncoding != null && contentEncoding.length() < 100)
			writer.setEncoding(contentEncoding);
		else
			writer.guessEncoding();
        writer.putContent(content.getContent());
	}
	
	protected int fetchFolder(IMAPFolder folder, EmailFolder repoFolder, boolean topFlag) throws MessagingException {
		int n = 0;
		if (!topFlag) {
			if (logger.isDebugEnabled())
				logger.debug("Openning folder " + folder.getName());
			folder.open(Folder.READ_ONLY);
			List<EmailMessage> fetchedEmails = repoFolder.getEmails();
			long firstUID = 1;
			if (fetchedEmails.size() > 0) {
				firstUID = fetchedEmails.get(fetchedEmails.size()-1).getUID()+1;
			}
			Message[] messages = folder.getMessagesByUID(firstUID, UIDFolder.LASTUID);
			n = messages.length;
			for (int i = 0; i < messages.length; i++) {
				AlvexEmailMessage m = new AlvexEmailMessage((MimeMessage)messages[i], folder);
				if (m.getUID() < firstUID)
					continue;
				if (logger.isDebugEnabled())
					logger.debug("Fetched message " + Integer.toString((i+1)) + "/" + Integer.toString(messages.length) +  " from folder " + folder.getName() + ": \""+m.getSubject()+"\" from "+m.getFrom());
				Map<QName, Serializable> props = new HashMap<QName, Serializable>();
				props.put(AlvexContentModel.PROP_EMAIL_FROM, m.getFrom());
				props.put(AlvexContentModel.PROP_EMAIL_TO, m.getTo());
				props.put(AlvexContentModel.PROP_EMAIL_CC, (Serializable)m.getCC());
				props.put(AlvexContentModel.PROP_EMAIL_SUBJECT, m.getSubject());
				props.put(AlvexContentModel.PROP_EMAIL_IN_REPLY_TO, m.getInReplyTo());
				props.put(AlvexContentModel.PROP_EMAIL_SENT_DATE, m.getSentDate());
				props.put(AlvexContentModel.PROP_EMAIL_UID, m.getUID());
				props.put(AlvexContentModel.PROP_EMAIL_ID, m.getID());
				NodeRef messageNode = createNode(repoFolder.getNodeRef(), ContentModel.ASSOC_CONTAINS, AlvexContentModel.TYPE_EMAIL_MESSAGE, props);
				writeContent(messageNode, m.getBody());
		        for (EmailMessagePart att: m.getAttachments()) {
					// FIXME_ASAP: think about it more carefully
					String subj = m.getSubject();
					String fname = att.getFileName();
					if( fname.equals(subj + " (part 1).html") )
						continue;
					if (logger.isDebugEnabled())
						logger.debug("Creating attachment: " + att.getFileName());
		        	props.clear();
		        	props.put(AlvexContentModel.PROP_EMAIL_REAL_NAME, att.getFileName());
					// FIXME_ASAP: think about it more carefully
					props.put(ContentModel.PROP_NAME, att.getFileName());
		        	NodeRef attNodeRef = createNode(messageNode, AlvexContentModel.ASSOC_EMAIL_ATTACHMENTS, AlvexContentModel.TYPE_EMAIL_ATTACHMENT, props);
		        	writeContent(attNodeRef, att);
		        }
		        for (EmailMessageEventListener l: listeners)
					if( l != null )
						l.onNewMessageReceived(new EmailMessageImpl(messageNode, nodeService, contentService));
			}
			if (logger.isDebugEnabled())
				logger.debug("Closing folder " + folder.getName());
			folder.close(true);
		}
		for (EmailFolder f: repoFolder.getSubfolders())
			if (f.isFetchActive()) {
				fetchFolder((IMAPFolder)folder.getFolder(f.getName()), f, false);
			}
		return n;
	}

	@Override
	public void fetch() {
		try {
			Store store = connectToStore();
			fetchFolder((IMAPFolder)store.getDefaultFolder(), new EmailFolderImpl(getEmailConfigNode(), nodeService, contentService), true);
			store.close();
		} catch (MessagingException e) {
			throw new AlfrescoRuntimeException("Failed to fetch emails", e);
		}
	}

	@Override
	public List<EmailFolder> getFolders() {
		NodeRef container = getEmailConfigNode();
		List<EmailFolder> result = new ArrayList<EmailFolder>();
		for (ChildAssociationRef assoc: nodeService.getChildAssocs(container, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL))
			result.add(new EmailFolderImpl(assoc.getChildRef(), nodeService, contentService));
		return result;
	}

	@Override
	public EmailFolder getFolder(String name) {
		NodeRef container = getEmailConfigNode();
		NodeRef folder = nodeService.getChildByName(container, ContentModel.ASSOC_CONTAINS, name);
		return new EmailFolderImpl(folder, nodeService, contentService);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		nodeService = serviceRegistry.getNodeService();
		personService = serviceRegistry.getPersonService();
		authenticationService = serviceRegistry.getAuthenticationService();
		contentService = serviceRegistry.getContentService();
		mimetypeService = serviceRegistry.getMimetypeService();
	}
	
	protected EmailProvider getEmailProvider(String id) throws NoSuchProviderException{
		EmailProvider provider = null;
		for (EmailProvider p: getProviders())
			if (p.getId().equals(id))
				provider = p;
		if (provider == null)
			throw new NoSuchProviderException("Specified email provider not found");
		return provider;
	}
	
	protected Store connectToStore() throws MessagingException{
		EmailConfig config = getConfig();
		EmailProvider provider = getEmailProvider(config.getProviderId());
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", provider.getIncomingProto());

		Session session = Session.getDefaultInstance(props, null);

		Store store = session.getStore(provider.getIncomingProto());
		store.connect(provider.getIncomingServer(), provider.getIncomingPort(), config.getUsername(),
				config.getPassword());
		return store;
	}

	protected void fetchFolderChildren(Folder folder, NodeRef repoFolder) throws MessagingException {
		for (Folder f: folder.list()) {
			Map<QName, Serializable> props = new HashMap<QName, Serializable>();
			props.put(AlvexContentModel.PROP_EMAIL_FOLDER_FETCH_ACTIVE, true);
			props.put(ContentModel.PROP_NAME, f.getName());
			NodeRef newNode = nodeService.createNode(repoFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(AlvexContentModel.ALVEXEM_MODEL_URI, f.getName()), AlvexContentModel.TYPE_EMAIL_FOLDER, props).getChildRef();
			fetchFolderChildren(f, newNode);
		}
	}
	
	@Override
	public void fetchFolderList() {
		try {
			Store store = connectToStore();
			fetchFolderChildren(store.getDefaultFolder(), getEmailConfigNode());
		} catch (MessagingException e) {
			throw new AlfrescoRuntimeException("Folder list fetch failed", e);
		}
	}

	@Override
	public void clearProviders() {
		providers.clear();		
	}

	@Override
	public void send(List<String> to, List<String> cc, List<String> bcc,
			String subject, String body, List<NodeRef> attachments, boolean html) throws Exception {
		EmailConfig config = getConfig();
		EmailProvider provider = getEmailProvider(config.getProviderId());
        Properties props = System.getProperties();
        String prefix = "mail."+provider.getOutgoingProto()+".";
        props.put(prefix+"host", provider.getOutgoingServer());
        props.put(prefix+"port", provider.getOutgoingPort());
        props.put(prefix+"auth","true");
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(config.getAddress(), config.getRealName()));
        if (to != null) {
	        InternetAddress[] recipients = new InternetAddress[to.size()];
	        for (int i = 0; i < to.size(); i++)
	        	recipients[i] = new InternetAddress(to.get(i));
	        msg.setRecipients(Message.RecipientType.TO, recipients);
        }
        
        if (cc != null) {
	        InternetAddress[] recipients = new InternetAddress[cc.size()];
	        for (int i = 0; i < cc.size(); i++)
	        	recipients[i] = new InternetAddress(cc.get(i));
	        msg.setRecipients(Message.RecipientType.CC, recipients);
        }
        
        if (bcc != null) {
	        InternetAddress[] recipients = new InternetAddress[bcc.size()];
	        for (int i = 0; i < bcc.size(); i++)
	        	recipients[i] = new InternetAddress(bcc.get(i));
	        msg.setRecipients(Message.RecipientType.BCC, recipients);
        }
        
        msg.setSubject(subject);
        msg.setHeader("X-Mailer", "Alvex Emailer");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        
        if (body != null) {
	        messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText(body, "utf-8", html ? "html" : "plain");
	        multipart.addBodyPart(messageBodyPart);
        }        

        if (attachments != null)
	        for (NodeRef att: attachments) {
		        messageBodyPart = new MimeBodyPart();
		        String fileName = (String)nodeService.getProperty(att, AlvexContentModel.PROP_EMAIL_REAL_NAME);
		        messageBodyPart.setDataHandler(new DataHandler(new RepositoryDataSource(att, fileName, contentService)));
		        messageBodyPart.setFileName(fileName);
		        multipart.addBodyPart(messageBodyPart);
	        }

        msg.setContent(multipart);

        SMTPTransport t = (SMTPTransport)session.getTransport(provider.getOutgoingProto());
        t.connect(config.getUsername(), config.getPassword());
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
	}

	@Override
	public void registerEventListener(EmailMessageEventListener listener) {
		listeners.add(listener);		
	}
}
