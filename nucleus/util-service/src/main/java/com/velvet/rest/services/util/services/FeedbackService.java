package com.velvet.rest.services.util.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.util.entity.FeedbackEntity;
import com.velvet.rest.services.util.repository.FeedbackRepository;

@Service
public class FeedbackService implements BaseService<FeedbackEntity> {

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Transactional
	public FeedbackEntity saveFeedback(FeedbackEntity feedback) {
		return feedbackRepository.save(feedback);
	}

	@Transactional(readOnly = true)
	public List<FeedbackEntity> getFeedbackByUserId(Integer userId) {
		return feedbackRepository.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	public List<FeedbackEntity> getAllFeedback() {
		return feedbackRepository.findAllActiveFeedback();
	}

	@Transactional(readOnly = true)
	public List<FeedbackEntity> getFeedbackByStatus(String status) {
		return feedbackRepository.findByStatus(status);
	}

	@Override
	public Class<FeedbackEntity> getEntity() {
		return FeedbackEntity.class;
	}

	@Override
	public BaseCrudRepository<FeedbackEntity, Long> getRepository() {
		return feedbackRepository;
	}
}

