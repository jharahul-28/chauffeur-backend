package com.velvet.rest.services.util.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.util.entity.FeedbackEntity;

@Repository
public interface FeedbackRepository extends BaseCrudRepository<FeedbackEntity, Long> {

	@Query("SELECT f FROM FeedbackEntity f WHERE f.userId = :userId AND f.active = 1 ORDER BY f.createdOn DESC")
	List<FeedbackEntity> findByUserId(@Param("userId") Integer userId);
	
	@Query("SELECT f FROM FeedbackEntity f WHERE f.active = 1 ORDER BY f.createdOn DESC")
	List<FeedbackEntity> findAllActiveFeedback();
	
	@Query("SELECT f FROM FeedbackEntity f WHERE f.status = :status AND f.active = 1 ORDER BY f.createdOn DESC")
	List<FeedbackEntity> findByStatus(@Param("status") String status);
}

