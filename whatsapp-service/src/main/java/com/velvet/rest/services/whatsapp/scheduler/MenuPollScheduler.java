package com.velvet.rest.services.whatsapp.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.services.WhapiMenuPollService;

@Service
public class MenuPollScheduler {

	private static final Logger logger = LoggerFactory.getLogger(MenuPollScheduler.class);

	@Value("${velvet.scheduler.menu-poll.enabled:false}")
	private boolean enabled;

	@Autowired
	private WhapiMenuPollService menuPollService;

	@Scheduled(cron = "0 0 15 * * MON-FRI", zone = "Asia/Kolkata")
	public void sendMenuPollAt3PM() {
		if (!enabled) {
			return;
		}
		logger.info("MenuPollScheduler: sendMenuPollAt3PM triggered");
		menuPollService.sendMenuPoll();
	}
}
