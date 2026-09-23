package com.interviewai.model;

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

@Document(collection = "progress")
@CompoundIndex(name = "user_date_progress_idx", def = "{'userId': 1, 'date': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Progress {

    @Id
    private String id;

    @Indexed
    private String userId;

    private Instant date;

    private double overallScore;
    private double technicalScore;
    private double communicationScore;
    private double confidenceScore;

    private int interviewsCompleted;

    @Builder.Default private List<String> topStrongAreas = new ArrayList<>();
    @Builder.Default private List<String> topWeakAreas = new ArrayList<>();
}