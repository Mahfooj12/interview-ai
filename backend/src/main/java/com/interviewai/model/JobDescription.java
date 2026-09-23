package com.interviewai.model;

import com.interviewai.model.embedded.MatchResult;
import com.interviewai.model.embedded.ParsedJobDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "jobDescriptions")
@CompoundIndex(name = "user_created_jd_idx", def = "{'userId': 1, 'createdAt': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDescription extends BaseAudit {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;
    private String company;

    private String rawText;
    private String fileUrl;
    private String fileName;

    private ParsedJobDescription parsed;

    private MatchResult matchResult;
}