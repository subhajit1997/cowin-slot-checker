package com.cowin.scheduler.util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

	@Value("${email.host}")
	private String mailHost;

	@Value("${email.port}")
	private int mailPort;

	@Value("${email.from}")
	private String cowinEmail;

	@Value("${email.password}")
	private String cowinPassword;

	@Value("${email.to}")
	private String emailRecipient;
	
	@Value("${email.cc}")
	private String emailRecipientCc;

	@Value("${email.subject}")
	private String cowinMailSubject;

	@Value("${email.format}")
	private String cowinMailFormat;
	
	@Value("${cowin.user.mainLocation}")
	private String importantPinCode;

	private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

	public void sendMail(Map<Integer, List<String>> cowinData) {
		try {
			Properties properties = new Properties();
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.host", mailHost);
			properties.put("mail.smtp.port", Integer.toString(mailPort));

			Session mailSession = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(cowinEmail, cowinPassword);
				}
			});

			Message mailMessage = prepreMessage(mailSession, formatMailBody(cowinData));
			Transport.send(mailMessage);
		} catch (MessagingException e) {
			LOGGER.error("Error sending email", e);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("email send to :" + emailRecipient + " with subject: " + cowinMailSubject);
	}

	private Message prepreMessage(Session mailSession, String sendTextBody) {
		Message message = new MimeMessage(mailSession);

		try {
			message.setFrom(new InternetAddress(cowinEmail));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
			if(!emailRecipientCc.isEmpty() && !emailRecipientCc.equals(null)) {
				message.setRecipient(Message.RecipientType.CC, new InternetAddress(emailRecipientCc));
			}
			message.setContent(sendTextBody, cowinMailFormat);
			message.setSubject(cowinMailSubject + " " + new Timestamp(new Date().getTime()));
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return message;
	}

	private String formatMailBody(Map<Integer, List<String>> cowinData) {
		String mainLocation = "";
		StringBuffer tableBody = new StringBuffer();
		tableBody.append("Hi,<br/>");
		tableBody.append("<h3>Free Slot Available on Cowin</h3><br/>");
		tableBody.append("<table cellspacing=\"3\" bgcolor=\"#000000\">");
		tableBody.append("<tr bgcolor=\"#cdf7f9\">");
		tableBody.append(
				"<th>Center id</th><th>Center Name</th><th>Center Address</th><th>PinCode</th><th>Center Info</th><th>doseCapacity</th>");
		tableBody.append("</tr>");

		for (Integer centerId : cowinData.keySet()) {
			List<String> valueCowin = cowinData.get(centerId);
			if (valueCowin.get(2).equals(importantPinCode)) {
				mainLocation = "<h2 style=\"background-color:DodgerBlue;\" >Available at "+importantPinCode+" ,capacity:"
						+ valueCowin.get(4) + "</h2><br/>";
				cowinMailSubject=importantPinCode+" "+cowinMailSubject;
			}
			tableBody.append("<tr bgcolor=\"#ffffff\">");

			tableBody.append("<td>" + centerId + "</td>" + "<td>" + valueCowin.get(0) + "</td>" + "<td>"
					+ valueCowin.get(1) + "</td><td>" + valueCowin.get(2) + "</td><td>" + valueCowin.get(3)
					+ "</td><td>" + valueCowin.get(4) + "</td>");
			tableBody.append("</tr>");
		}
		tableBody.append("</table>");
		tableBody.append("<br/><p>Thanks & Regards<br/>Subhajit Biswas</p>");
		return mainLocation + tableBody.toString();
	}
}
