package com.interviewai.worker;

import com.interviewai.model.InterviewGenerationJob;
import com.interviewai.model.JobStatus;
import com.interviewai.repository.InterviewGenerationJobRepository;
import com.interviewai.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class InterviewGenerationWorkerIT {

    @Autowired private JobService jobService;
    @Autowired private InterviewGenerationJobRepository jobRepository;

    @Test
    void claimJob_only_one_thread_wins() throws Exception {
        InterviewGenerationJob job = jobService.createJob("user-1", "interview-1");

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger wins = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    start.await();
                    InterviewGenerationJob claimed = jobService.claimJob(job.getId(), "instance-" + idx);
                    if (claimed != null) wins.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        pool.shutdown();

        // Only one thread should have claimed the job
        assertThat(wins.get()).isEqualTo(1);

        // Job should be in PROCESSING
        InterviewGenerationJob reloaded = jobRepository.findById(job.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(JobStatus.PROCESSING);
    }
}
