package org.ice.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
	
	private Authenticator pa;
	private String username, password;
	
	public void setup(String smtpServer, String port, String useSSL, String _username, String _password)	{
		Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", useSSL);
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
	
	public void send(String from, String to, String subject, String body, String replyTo) throws Exception {
		Properties props = System.getProperties();
		Session session = Session.getInstance(props, pa);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, to);

        msg.setSubject(subject, "utf-8");
        msg.setContent(body, "text/html; charset=utf-8");
        msg.setHeader("X-Mailer", "Asking Email Service");
        msg.setHeader("Content-Transfer-Encoding", "quoted-printable");
        if (replyTo != null && !replyTo.isEmpty())
        	msg.setHeader("Reply-To", replyTo);
        msg.setSentDate(new Date());
        msg.saveChanges();
        Transport.send(msg);
	}
}
