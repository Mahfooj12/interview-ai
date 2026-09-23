package com.interviewai.model;

import com.interviewai.model.embedded.AnswerMetrics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "answers")
@CompoundIndex(name = "interview_question_idx", def = "{'interviewId': 1, 'questionId': 1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    private String id;

    @Indexed
    private String interviewId;

    @Indexed
    private String questionId;

    @Indexed
    private String userId;

    private String text;
    private String audioUrl;
    private String videoUrl;

    private CodeSubmission codeSubmission;

    private AnswerMetrics metrics;

    private Instant answeredAt;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CodeSubmission {
        private String language;
        private String code;
        @Builder.Default private List<TestCaseResult> testResults = new ArrayList<>();

        @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
        public static class TestCaseResult {
            private String input;
            private String expected;
            private String actual;
            private boolean passed;
        }
    }
}