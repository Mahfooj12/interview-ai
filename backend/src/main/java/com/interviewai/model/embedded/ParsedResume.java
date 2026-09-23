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
public class ParsedResume {

    private String name;
    private String email;
    private String phone;
    private String summary;

    @Builder.Default private List<String> skills = new ArrayList<>();
    @Builder.Default private List<String> languages = new ArrayList<>();
    @Builder.Default private List<String> frameworks = new ArrayList<>();
    @Builder.Default private List<String> databases = new ArrayList<>();
    @Builder.Default private List<String> tools = new ArrayList<>();

    @Builder.Default private List<Project> projects = new ArrayList<>();
    @Builder.Default private List<Experience> experience = new ArrayList<>();
    @Builder.Default private List<Education> education = new ArrayList<>();
    @Builder.Default private List<String> certifications = new ArrayList<>();
    @Builder.Default private List<String> achievements = new ArrayList<>();
    @Builder.Default private List<TechnicalClaim> technicalClaims = new ArrayList<>();

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Project {
        private String name;
        @Builder.Default private List<String> tech = new ArrayList<>();
        private String description;
        private String duration;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Experience {
        private String company;
        private String role;
        private String from;
        private String to;
        private String description;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Education {
        private String institute;
        private String degree;
        private String year;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TechnicalClaim {
        private String topic;
        private String evidence;
    }
}
