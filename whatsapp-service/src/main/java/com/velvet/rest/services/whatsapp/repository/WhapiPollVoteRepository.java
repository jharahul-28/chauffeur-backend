package com.velvet.rest.services.whatsapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.velvet.rest.services.whatsapp.entity.WhapiPollVoteEntity;

public interface WhapiPollVoteRepository extends JpaRepository<WhapiPollVoteEntity, Long> {

	List<WhapiPollVoteEntity> findByPollId(Long pollId);

	List<WhapiPollVoteEntity> findByVoterWaId(String voterWaId);

	List<WhapiPollVoteEntity> findByPollIdAndVoterWaId(Long pollId, String voterWaId);

	boolean existsByPollIdAndWaOptionIdAndVoterWaId(Long pollId, String waOptionId, String voterWaId);
}
