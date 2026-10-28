package com.interviewai.service;

import com.interviewai.model.InterviewGenerationJob;
import com.interviewai.model.JobStatus;
import com.interviewai.repository.InterviewGenerationJobRepository;
import com.interviewai.repository.ProcessedEventRepository;
import com.interviewai.service.impl.JobServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceClaimTest {

    @Mock private InterviewGenerationJobRepository jobRepository;
    @Mock private ProcessedEventRepository processedEventRepository;
    @Mock private MongoTemplate mongoTemplate;

    @Test
    void claimJob_returns_null_when_already_claimed() {
        JobServiceImpl service = new JobServiceImpl(jobRepository, processedEventRepository, mongoTemplate);
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(InterviewGenerationJob.class)))
                .thenReturn(null);

        InterviewGenerationJob result = service.claimJob("job-1", "instance-a");

        assertThat(result).isNull();
    }

    @Test
    void claimJob_returns_job_when_claim_succeeds() {
        JobServiceImpl service = new JobServiceImpl(jobRepository, processedEventRepository, mongoTemplate);
        InterviewGenerationJob claimed = InterviewGenerationJob.builder()
                .id("job-1").status(JobStatus.PROCESSING).build();
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(InterviewGenerationJob.class)))
                .thenReturn(claimed);

        InterviewGenerationJob result = service.claimJob("job-1", "instance-a");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(JobStatus.PROCESSING);
    }
}