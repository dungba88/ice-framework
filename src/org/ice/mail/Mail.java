package org.ice.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Mail {
	
	public Authenticator pa;
	public String username, password;

	public void setup(String smtpServer, String port, String _username, String _password)	{
		Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        this.username = _username;
        this.password = _password;
        if (username != null && password != null) { 
            props.put("mail.smtp.auth", "true");
            pa = new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
        }
	}
}
