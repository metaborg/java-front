package org.mbravenboer.mail;

import org.mbravenboer.work.ProgressingWorker;
import org.mbravenboer.work.helpers.DefaultProgressingWorker;
import org.mbravenboer.work.helpers.DefaultProgressingWorkerRunnable;


import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSystem
{
	private MailAccount account;

	private Properties accountProperties;

	public MailSystem(MailAccount account)
	{
		this.account = account;
		accountProperties = new Properties();
		String protocol = "mail." + account.getOutGoingMailProtocol() + ".host";
		accountProperties.put(protocol, account.getOutGoingMailServer());		
	}

	public void sendMail(MailMessage message) throws MessagingException
	{
		Message mailMessage = createMessage(message);
		Transport.send(mailMessage);
	}

	public ProgressingWorker sendMailInWorker(MailMessage message) throws MessagingException
	{
		return new DefaultProgressingWorker(new Sender(message), "Sending your e-mail.", 0, 2);
	}

	private Message createMessage(MailMessage message) throws MessagingException
	{
		Session session = Session.getDefaultInstance(accountProperties);		

		Message result = new MimeMessage(session);
        
		result.setFrom(account.getOwner());
		result.setRecipients(Message.RecipientType.TO, new InternetAddress[]{message.getRecipient()});
		result.setSubject(message.getSubject());
		result.setText(message.getContent());
		result.setHeader("X-mailer","mbravenboer.org mail");
		result.setSentDate(new Date());

		return result;
	}

	private class Sender extends DefaultProgressingWorkerRunnable
	{
		private MailMessage message;

		public Sender(MailMessage message)
		{
			super();
			this.message = message;
		}
		
		public void run()
		{
			ProgressingWorker worker = super.getProgressingWorker();

			try
			{
						
				worker.setStage("Preparing e-mail for transport");
				Message mailMessage = createMessage(message);
				worker.increaseProgress();

				worker.setStage("Sending e-mail");
				Transport.send(mailMessage);
				worker.increaseProgress();

				worker.setFinished();
			}
			catch(Exception exc)
			{
				worker.fatalExceptionOccured(exc);
			}
		}
	}
}
