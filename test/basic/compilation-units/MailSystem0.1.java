package org.mbravenboer.mail;

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

}
