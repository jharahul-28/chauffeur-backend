package com.velvet.rest.services.user.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.user.services.WelcomeMessageService;

@Service
public class WelcomeMessageScheduler {

	private static final Logger logger = LoggerFactory.getLogger(WelcomeMessageScheduler.class);

	@Value("${velvet.scheduler.welcome-message.enabled:false}")
	private boolean enabled;

	@Autowired
	private WelcomeMessageService welcomeMessageService;

	@Scheduled(fixedRate = 300000)
	public void sendWelcomeMessages() {
		if (!enabled) {
			return;
		}
		logger.info("WelcomeMessageScheduler: sendWelcomeMessages triggered");
		welcomeMessageService.sendPendingWelcomeMessages();
	}
}
