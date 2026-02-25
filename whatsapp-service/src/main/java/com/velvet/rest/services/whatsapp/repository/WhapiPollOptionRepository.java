package com.velvet.rest.services.whatsapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.velvet.rest.services.whatsapp.entity.WhapiPollOptionEntity;

public interface WhapiPollOptionRepository extends JpaRepository<WhapiPollOptionEntity, Long> {

	List<WhapiPollOptionEntity> findByPoll_Id(Long pollId);

	Optional<WhapiPollOptionEntity> findByPoll_IdAndWaOptionId(Long pollId, String waOptionId);

	@Modifying
	@Query("UPDATE WhapiPollOptionEntity o SET o.voteCount = o.voteCount + 1 WHERE o.poll.id = :pollId AND o.waOptionId = :waOptionId")
	void incrementVote(@Param("pollId") Long pollId, @Param("waOptionId") String waOptionId);

	@Modifying
	@Query("UPDATE WhapiPollOptionEntity o SET o.voteCount = o.voteCount - 1 WHERE o.poll.id = :pollId AND o.waOptionId = :waOptionId AND o.voteCount > 0")
	void decrementVote(@Param("pollId") Long pollId, @Param("waOptionId") String waOptionId);
}
