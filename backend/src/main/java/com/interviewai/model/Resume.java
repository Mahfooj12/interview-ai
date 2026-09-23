package com.interviewai.model;

import com.interviewai.model.embedded.ParsedResume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resumes")
@CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends BaseAudit {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String fileName;
    private String fileUrl;
    private String fileType; // PDF | DOCX
    private long fileSize;

    private String rawText;

    private ParsedResume parsed;

    @Builder.Default
    private boolean primaryResume = false;

    @Builder.Default
    private int version = 1;
}
