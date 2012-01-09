/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.ice.utils;

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
