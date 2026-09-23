package com.interviewai.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedJobDescription {

    private String title;
    private String company;
    private String seniority;

    @Builder.Default private List<String> requiredSkills = new ArrayList<>();
    @Builder.Default private List<String> preferredSkills = new ArrayList<>();
    @Builder.Default private List<String> technologies = new ArrayList<>();
    @Builder.Default private List<String> responsibilities = new ArrayList<>();
    @Builder.Default private List<String> experienceRequirements = new ArrayList<>();
}