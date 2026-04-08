package com.dte.service;

import com.dte.dto.AggregateResultDTO;
import com.dte.dto.AggregateResultDTO.CoordinatePoint;
import com.dte.dto.ScoreResultDTO;
import com.dte.model.GuestSubmission;
import com.dte.model.Submission;
import com.dte.repository.GuestSubmissionRepository;
import com.dte.repository.SubmissionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Handles saving and getting quiz results
@Service
@Transactional
public class SubmissionService {

  private static final Logger logger = LoggerFactory.getLogger(
    SubmissionService.class
  );

  private final SubmissionRepository submissionRepository;
  private final GuestSubmissionRepository guestSubmissionRepository;
  private final ScoringService scoringService;
  private final ObjectMapper objectMapper;

  public SubmissionService(
    SubmissionRepository submissionRepository,
    GuestSubmissionRepository guestSubmissionRepository,
    ScoringService scoringService,
    ObjectMapper objectMapper
  ) {
    this.submissionRepository = submissionRepository;
    this.guestSubmissionRepository = guestSubmissionRepository;
    this.scoringService = scoringService;
    this.objectMapper = objectMapper;
  }

  // Save answers from logged in user
  public ScoreResultDTO submitForUser(
    Long userId,
    Map<Integer, Integer> answers
  ) {
    logger.info("Processing submission for user: {}", userId);

    // Calculate scores
    ScoreResultDTO result = scoringService.calculateScores(answers);

    // Create submission object
    Submission submission = new Submission(
      userId,
      result.getScoreM(),
      result.getScoreC(),
      result.getScoreR(),
      result.getWeightM(),
      result.getWeightC(),
      result.getWeightR(),
      result.getX(),
      result.getY(),
      answersToJson(answers)
    );

    // Save to database
    submission = submissionRepository.save(submission);
    result.setSubmissionId(submission.getId());

    logger.info("Saved submission {} for user {}", submission.getId(), userId);
    return result;
  }

  // Save answers from guest
  public ScoreResultDTO submitForGuest(
    String inviteCode,
    Map<Integer, Integer> answers
  ) {
    logger.info("Processing guest submission, code: {}", inviteCode);

    // Calculate scores
    ScoreResultDTO result = scoringService.calculateScores(answers);

    // Create submission object
    GuestSubmission submission = new GuestSubmission(
      inviteCode,
      null,
      result.getScoreM(),
      result.getScoreC(),
      result.getScoreR(),
      result.getWeightM(),
      result.getWeightC(),
      result.getWeightR(),
      result.getX(),
      result.getY(),
      answersToJson(answers)
    );

    // Save to database
    submission = guestSubmissionRepository.save(submission);
    result.setSubmissionId(submission.getId());

    logger.info("Saved guest submission {}", submission.getId());
    return result;
  }

  // Get latest result for user
  @Transactional(readOnly = true)
  public Optional<ScoreResultDTO> getLatestForUser(Long userId) {
    Optional<Submission> submission =
      submissionRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);

    if (submission.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(toDTO(submission.get()));
  }

  // Get all results for user
  @Transactional(readOnly = true)
  public List<ScoreResultDTO> getAllForUser(Long userId) {
    List<Submission> submissions =
      submissionRepository.findByUserIdOrderByCreatedAtDesc(userId);

    List<ScoreResultDTO> results = new ArrayList<>();
    for (Submission s : submissions) {
      results.add(toDTO(s));
    }
    return results;
  }

  // Get stats for all submissions
  @Transactional(readOnly = true)
  public AggregateResultDTO getAggregateResults() {
    long userCount = submissionRepository.count();
    long guestCount = guestSubmissionRepository.count();
    long totalCount = userCount + guestCount;

    // Return empty if no data
    if (totalCount == 0) {
      return AggregateResultDTO.builder()
        .totalSubmissions(0L)
        .userSubmissions(0L)
        .guestSubmissions(0L)
        .allPoints(Collections.emptyList())
        .build();
    }

    // Collect all points and calculate score sums
    List<CoordinatePoint> allPoints = new ArrayList<>();
    double sumM = 0,
      sumC = 0,
      sumR = 0;

    List<Submission> allSubs = submissionRepository.findAll();
    for (Submission s : allSubs) {
      allPoints.add(new CoordinatePoint(s.getCoordX(), s.getCoordY(), "user"));
      sumM += s.getScoreM();
      sumC += s.getScoreC();
      sumR += s.getScoreR();
    }

    List<GuestSubmission> allGuests = guestSubmissionRepository.findAll();
    for (GuestSubmission g : allGuests) {
      allPoints.add(new CoordinatePoint(g.getCoordX(), g.getCoordY(), "guest"));
      sumM += g.getScoreM();
      sumC += g.getScoreC();
      sumR += g.getScoreR();
    }

    // Calculate average position
    double sumX = 0,
      sumY = 0;
    for (CoordinatePoint p : allPoints) {
      sumX += p.getX();
      sumY += p.getY();
    }
    double avgX = sumX / allPoints.size();
    double avgY = sumY / allPoints.size();

    // Calculate average scores
    Double avgScoreM = round(sumM / totalCount);
    Double avgScoreC = round(sumC / totalCount);
    Double avgScoreR = round(sumR / totalCount);

    return AggregateResultDTO.builder()
      .totalSubmissions(totalCount)
      .userSubmissions(userCount)
      .guestSubmissions(guestCount)
      .avgScoreM(avgScoreM)
      .avgScoreC(avgScoreC)
      .avgScoreR(avgScoreR)
      .avgX(round(avgX))
      .avgY(round(avgY))
      .allPoints(allPoints)
      .build();
  }

  // Get simple stats
  @Transactional(readOnly = true)
  public Map<String, Object> getStatistics() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUserSubmissions", submissionRepository.count());
    stats.put("totalGuestSubmissions", guestSubmissionRepository.count());
    stats.put(
      "totalSubmissions",
      submissionRepository.count() + guestSubmissionRepository.count()
    );
    return stats;
  }

  // Convert submission to DTO
  private ScoreResultDTO toDTO(Submission s) {
    return ScoreResultDTO.builder()
      .scoreM(s.getScoreM())
      .scoreC(s.getScoreC())
      .scoreR(s.getScoreR())
      .weightM(s.getWeightM())
      .weightC(s.getWeightC())
      .weightR(s.getWeightR())
      .x(s.getCoordX())
      .y(s.getCoordY())
      .submissionId(s.getId())
      .build();
  }

  // Convert answers map to JSON string
  private String answersToJson(Map<Integer, Integer> answers) {
    try {
      return objectMapper.writeValueAsString(answers);
    } catch (JsonProcessingException e) {
      logger.error("Could not convert answers to JSON", e);
      return "{}";
    }
  }

  // Round to 4 decimal places
  private Double round(Double value) {
    if (value == null) {
      return null;
    }
    return Math.round(value * 10000.0) / 10000.0;
  }
}
