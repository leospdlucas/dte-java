package com.dte.repository;

import com.dte.model.GuestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Database operations for guest submissions
@Repository
public interface GuestSubmissionRepository extends JpaRepository<GuestSubmission, Long> {

    // Get submissions by invite code
    List<GuestSubmission> findByInviteCode(String inviteCode);

    // Count by invite code
    long countByInviteCode(String inviteCode);

    // Get submissions by inviter
    List<GuestSubmission> findByInviterUserId(Long inviterUserId);

    // Get all coordinates for chart
    @Query("SELECT g.coordX, g.coordY FROM GuestSubmission g")
    List<Object[]> findAllCoordinates();

    // Get average scores
    @Query("SELECT AVG(g.scoreM), AVG(g.scoreC), AVG(g.scoreR) FROM GuestSubmission g")
    Object[] findAverageScores();

    // Get average position
    @Query("SELECT AVG(g.coordX), AVG(g.coordY) FROM GuestSubmission g")
    Object[] findAverageCoordinates();

    // Get coordinates by inviter
    @Query("SELECT g.coordX, g.coordY FROM GuestSubmission g WHERE g.inviterUserId = :userId")
    List<Object[]> findCoordinatesByInviter(@Param("userId") Long userId);

    // Get average scores by inviter
    @Query("SELECT AVG(g.scoreM), AVG(g.scoreC), AVG(g.scoreR) FROM GuestSubmission g WHERE g.inviterUserId = :userId")
    Object[] findAverageScoresByInviter(@Param("userId") Long userId);
}
