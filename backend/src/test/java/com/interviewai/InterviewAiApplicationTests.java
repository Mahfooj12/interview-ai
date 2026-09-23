package com.interviewai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InterviewAiApplicationTests {

    @Test
    void contextLoads() {
        // Ensures Spring context starts. Requires MONGODB_URI in environment when run.
    }
}
