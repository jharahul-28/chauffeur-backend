package com.velvet.rest.services.user.services;

import java.util.concurrent.CompletableFuture;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

@Service
public class EmailService {

	private static final AppLogger logger = LoggingManager.getLogger(EmailService.class);

	private static final String LOGIN_LINK_FORMAT = "https://app.honestmeals.co?orgCode=%s";

	@Autowired
	private JavaMailSender mailSender;

	public void sendLoginLink(String toEmail, String orgCode, String orgName) {
		CompletableFuture.runAsync(() -> {
			try {
				String loginLink = String.format(LOGIN_LINK_FORMAT, orgCode);
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true);
				helper.setTo(toEmail);
				helper.setSubject("Your Honest Meals Login Link");
				helper.setText(buildEmailBody(loginLink, orgName), true);
				mailSender.send(message);
				logger.info(String.format("Login link email sent to %s for org %s", toEmail, orgCode));
			} catch (Exception e) {
				logger.error(String.format("Failed to send login link email to %s", toEmail), e);
			}
		});
	}

	private String buildEmailBody(String loginLink, String orgName) {
		return String.format(
				"<html><body><h3>Welcome to Honest Meals</h3><p>Here is your login link for <b>%s</b>:</p><p><a href=\"%s\">%s</a></p><br><p>Team Honest Meals</p></body></html>",
				orgName, loginLink, loginLink);
	}
}
