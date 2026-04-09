# DTE - Spheral Tendency Diagram
A quiz API that calculates user scores across three value spheres and shows results on a triangle.

## What is DTE?
The quiz measures how much you agree with three types of values:

- **M (Security)**: Safety, order, stability, protection
- **C (Prosperity)**: Wealth, success, achievement, progress
- **R (Meaning)**: Spirituality, purpose, transcendence, faith

After answering 30 questions, you get a point inside a triangle. Each corner represents one sphere. Your position shows your unique mix of values.

## Tech Stack

| Part | Technology |
|------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Build | Maven |
| Database | H2 (dev) / PostgreSQL (prod) |
| ORM | Spring Data JPA |
| Validation | Jakarta Bean Validation |
| Tests | JUnit 5 + Mockito |

## Project Structure
dte-java/
├── pom.xml |
├── README.md
├── .gitignore
├── src/main/java/com/dte/
│   ├── DteApplication.java
│   ├── config/
│   │   ├── GlobalExceptionHandler.java
│   │   └── WebConfig.java
│   ├── controller/
│   │   └── QuizController.java
│   ├── dto/
│   │   ├── QuestionDTO.java
│   │   ├── SubmitAnswersRequest.java
│   │   ├── GuestSubmitRequest.java
│   │   ├── ScoreResultDTO.java
│   │   └── AggregateResultDTO.java
│   ├── model/
│   │   ├── Question.java
│   │   ├── LikertValue.java
│   │   ├── Submission.java
│   │   └── GuestSubmission.java
│   ├── repository/
│   │   ├── SubmissionRepository.java
│   │   └── GuestSubmissionRepository.java
│   └── service/
│       ├── QuestionService.java
│       ├── ScoringService.java
│       └── SubmissionService.java
├── src/main/resources/
│   ├── application.yml
│   └── data/questions.json
└── src/test/java/com/dte/
    ├── controller/QuizControllerTest.java
    └── service/ScoringServiceTest.java


## API Endpoints
### Questions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/questions` | Get 30 questions (logged in user) |
| GET | `/api/questions-guest` | Get 30 questions (guest) |

### Submissions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/submit` | Submit answers (logged in user) |
| POST | `/api/submit-guest` | Submit answers (guest) |

### Results
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/my-latest` | Get latest result |
| GET | `/api/my-history` | Get all results |
| GET | `/api/aggregate` | Get stats for everyone |
| GET | `/api/stats` | Get submission counts |

### Utility
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check |
| GET | `/api/ping` | Keep-alive |

## Example Request
**Submit answers:**

http
POST /api/submit-guest
Content-Type: application/json

{
  "inviteCode": "abc123",
  "answers": {
    "1": 10,
    "2": -5,
    "3": 0,
    ...
  }
}


**Response:**
json
{
  "scoreM": 2.5,
  "scoreC": -1.3,
  "scoreR": 4.2,
  "weightM": 0.3452,
  "weightC": 0.2516,
  "weightR": 0.4032,
  "x": 0.4748,
  "y": 0.3489,
  "submissionId": 123
}

## How Scoring Works
### Step 1: Calculate Scores
For each axis (M, C, R), calculate the average of answers:
- Range: -10 to +10
- Positive = agree with that sphere
- Negative = disagree with that sphere

### Step 2: Calculate Weights
Convert scores to weights using this formula:

weight = (score + 10)^2 / total

- The +10 shifts range to [0, 20] (all positive)
- The power of 2 makes differences bigger
- Weights add up to 1.0

### Step 3: Calculate Position
Use weighted average of triangle corners:

position = wM × M + wC × C + wR × R

Where:
- M = (0, 0) - bottom left
- C = (1, 0) - bottom right
- R = (0.5, 0.866) - top

## How to Run
### Requirements
- Java 17+
- Maven 3.8+

### Quick Start
bash
# Go to project folder
cd dte-java

# Run with Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/dte-api-1.0.0.jar

### Access Points
- API: http://localhost:8080/api
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:dtedb`
  - Username: `sa`
  - Password: (empty)

### Run Tests
bash
mvn test

## Settings
Key settings in `application.yml`:

yaml
dte:
  quiz:
    questions-per-axis: 10
    total-questions: 30
  scoring:
    exponent: 2.0


## Using PostgreSQL
To switch from H2 to PostgreSQL:

1. Uncomment PostgreSQL in pom.xml
2. Set environment variables:

bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=dte_db
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword

3. Run with prod profile:

bash
mvn spring-boot:run -Dspring.profiles.active=prod

## Future Plans
- JWT authentication (userId field is ready)
- Frontend integration (CORS is configured)

## Video
- https://github.com/leospdlucas/dte-java/tree/main/video