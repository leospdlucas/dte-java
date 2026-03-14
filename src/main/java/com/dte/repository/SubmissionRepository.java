package com.dte.repository;

import com.dte.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Database operations for user submissions
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Get all submissions for a user, newest first
    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Get latest submission for a user
    Optional<Submission> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    // Count submissions for a user
    long countByUserId(Long userId);

    // Get all coordinates for the chart
    @Query("SELECT s.coordX, s.coordY FROM Submission s")
    List<Object[]> findAllCoordinates();

    // Get average scores
    @Query("SELECT AVG(s.scoreM), AVG(s.scoreC), AVG(s.scoreR) FROM Submission s")
    Object[] findAverageScores();

    // Get average position
    @Query("SELECT AVG(s.coordX), AVG(s.coordY) FROM Submission s")
    Object[] findAverageCoordinates();

    // Check if user has any submissions
    boolean existsByUserId(Long userId);

    // Find by score range
    @Query("SELECT s FROM Submission s WHERE s.scoreM BETWEEN :min AND :max")
    List<Submission> findByScoreMRange(@Param("min") Double min, @Param("max") Double max);
}
