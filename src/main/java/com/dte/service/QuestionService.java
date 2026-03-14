package com.dte.service;

import com.dte.dto.QuestionDTO;
import com.dte.model.Question;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

// Loads questions from JSON and gives them to users
@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    private final ObjectMapper objectMapper;

    @Value("${dte.quiz.questions-per-axis:10}")
    private int questionsPerAxis;

    @Value("${dte.quiz.total-questions:30}")
    private int totalQuestions;

    // All questions in memory
    private List<Question> allQuestions;

    // Questions grouped by M, C, R
    private Map<String, List<Question>> questionsByAxis;

    public QuestionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Load questions when app starts
    @PostConstruct
    public void loadQuestions() {
        try {
            ClassPathResource resource = new ClassPathResource("data/questions.json");
            InputStream inputStream = resource.getInputStream();
            
            allQuestions = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<Question>>() {}
            );

            // Group by axis
            questionsByAxis = allQuestions.stream()
                .collect(Collectors.groupingBy(Question::getAxis));

            logger.info("Loaded {} questions", allQuestions.size());

        } catch (IOException e) {
            logger.error("Could not load questions", e);
            throw new RuntimeException("Could not load questions", e);
        }
    }

    // Get 30 random questions (10 from each axis)
    public List<QuestionDTO> getBalancedQuestions() {
        List<Question> selected = new ArrayList<>();

        // Pick 10 random from each axis
        for (String axis : Arrays.asList("M", "C", "R")) {
            List<Question> axisQuestions = new ArrayList<>(
                questionsByAxis.getOrDefault(axis, Collections.emptyList())
            );
            Collections.shuffle(axisQuestions);
            
            int count = Math.min(questionsPerAxis, axisQuestions.size());
            selected.addAll(axisQuestions.subList(0, count));
        }

        // Shuffle all together
        Collections.shuffle(selected);

        // Convert to DTOs (hide axis)
        List<QuestionDTO> result = new ArrayList<>();
        for (Question q : selected) {
            result.add(new QuestionDTO(q.getId(), q.getText()));
        }
        return result;
    }

    // Get all questions (for testing)
    public List<Question> getAllQuestions() {
        return Collections.unmodifiableList(allQuestions);
    }

    // Find question by ID
    public Optional<Question> findById(Integer id) {
        for (Question q : allQuestions) {
            if (q.getId().equals(id)) {
                return Optional.of(q);
            }
        }
        return Optional.empty();
    }

    // Get questions for one axis
    public List<Question> getQuestionsByAxis(String axis) {
        List<Question> list = questionsByAxis.get(axis.toUpperCase());
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    // Get total count
    public int getTotalQuestionCount() {
        if (allQuestions == null) {
            return 0;
        }
        return allQuestions.size();
    }

    public int getQuestionsPerAxis() {
        return questionsPerAxis;
    }

    public int getTotalQuestionsPerQuiz() {
        return totalQuestions;
    }
}
