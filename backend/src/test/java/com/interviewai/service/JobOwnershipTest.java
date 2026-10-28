package com.interviewai.service;

import com.interviewai.exception.ResourceNotFoundException;
import com.interviewai.model.InterviewGenerationJob;
import com.interviewai.model.JobStatus;
import com.interviewai.repository.InterviewGenerationJobRepository;
import com.interviewai.repository.ProcessedEventRepository;
import com.interviewai.service.impl.JobServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobOwnershipTest {

    @Mock private InterviewGenerationJobRepository jobRepository;
    @Mock private ProcessedEventRepository processedEventRepository;
    @Mock private MongoTemplate mongoTemplate;

    @Test
    void getStatus_throws_when_job_belongs_to_another_user() {
        JobServiceImpl service = new JobServiceImpl(jobRepository, processedEventRepository, mongoTemplate);
        when(jobRepository.findByIdAndUserId("job-1", "attacker"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStatus("attacker", "job-1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Job not found");
    }

    @Test
    void getStatus_returns_when_owner() {
        JobServiceImpl service = new JobServiceImpl(jobRepository, processedEventRepository, mongoTemplate);
        InterviewGenerationJob job = InterviewGenerationJob.builder()
                .id("job-1").userId("owner").interviewId("iv-1")
                .status(JobStatus.PROCESSING).build();
        when(jobRepository.findByIdAndUserId("job-1", "owner"))
                .thenReturn(Optional.of(job));

        var response = service.getStatus("owner", "job-1");
        org.assertj.core.api.Assertions.assertThat(response.getStatus()).isEqualTo("PROCESSING");
    }
}