package com.velvet.rest.services.whatsapp.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.velvet.rest.services.whatsapp.services.WhapiGroupSyncService;

@Service
public class GroupSyncScheduler {

	private static final Logger logger = LoggerFactory.getLogger(GroupSyncScheduler.class);

	@Value("${velvet.scheduler.group-sync.enabled:false}")
	private boolean enabled;

	@Autowired
	private WhapiGroupSyncService groupSyncService;

	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Kolkata")
	public void syncGroups() {
		if (!enabled) {
			return;
		}
		logger.info("GroupSyncScheduler: syncGroups triggered");
		groupSyncService.syncAllGroups();
	}
}
