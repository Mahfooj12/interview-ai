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
public class LearningItem {

    private String topic;
    private String priority; // HIGH | MEDIUM | LOW
    @Builder.Default private List<Resource> resources = new ArrayList<>();

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Resource {
        private String type; // COURSE | ARTICLE | PRACTICE | VIDEO
        private String title;
        private String url;
    }
}