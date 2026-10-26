package com.velvet.rest.services.whatsapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.velvet.rest.services.whatsapp.entity.WhapiPollEntity;

public interface WhapiPollRepository extends JpaRepository<WhapiPollEntity, Long> {

	Optional<WhapiPollEntity> findByWaPollMessageId(String waPollMessageId);

	List<WhapiPollEntity> findByChat_IdOrderByCreatedOnDesc(Long chatId);

	@Query("SELECT p.id FROM WhapiPollEntity p WHERE p.waPollMessageId = :waMessageId")
	Long findIdByWaMessageId(@Param("waMessageId") String waMessageId);

	@Modifying
	@Query("UPDATE WhapiPollEntity p SET p.totalVotes = p.totalVotes + 1 WHERE p.id = :pollId")
	void incrementTotalVotes(@Param("pollId") Long pollId);

	@Modifying
	@Query("UPDATE WhapiPollEntity p SET p.totalVotes = p.totalVotes - 1 WHERE p.id = :pollId AND p.totalVotes > 0")
	void decrementTotalVotes(@Param("pollId") Long pollId);

	@Modifying
	@Query("UPDATE WhapiPollEntity p SET p.totalVotes = GREATEST(p.totalVotes + :delta, 0) WHERE p.id = :pollId")
	void updateTotalVotesByDelta(@Param("pollId") Long pollId, @Param("delta") int delta);
}
