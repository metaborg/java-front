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

/**
 * MailSystem is a simple wrapper around JavaMail(TM) for sending MailMessages.
 * A MailSystem can be created in an easy way with a MailAccount. A MailSystem
 * is able to send MailMessages in a ProgressingWorker or just immediately.
 *
 * @version 1.0 27/01/2001
 * @author Martin Bravenboer
 */
public class MailSystem
{
	private MailAccount account;

	private Properties accountProperties;

	/**
	 * Creates a MailSystem with the specified MailAccount
	 *
	 * @param account the MailAccount of this MailSystem
	 */
	public MailSystem(MailAccount account)
	{
		this.account = account;
		accountProperties = new Properties();
		String protocol = "mail." + account.getOutGoingMailProtocol() + ".host";
		accountProperties.put(protocol, account.getOutGoingMailServer());		
	}

	/**
	 * Sends the specified MailMessage.
	 * When the method returns the message is send. When
	 * the MailSystem is unable to send the message an
	 * Exception will be thrown.
	 *
	 * @param message the MailMessage which must be send	 
	 */
	public void sendMail(MailMessage message) throws MessagingException
	{
		Message mailMessage = createMessage(message);
		Transport.send(mailMessage);
	}

	/**
	 * Sends the specified MailMessag in a ControlledWorker.
	 * The ControlledWorker is not started and is not cancellable.
	 * A program might want to use the ControlledWorker in a userinterface
	 * to show the progress of the transport or to show exceptions.
	 * This message returns immediately without doing anything with
	 * respect to sendin the message.
	 *
	 * @param message the MailMessage which must be send
	 */
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
