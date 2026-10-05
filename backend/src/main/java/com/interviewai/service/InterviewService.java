package com.interviewai.service;

import com.interviewai.dto.common.CursorPage;
import com.interviewai.dto.interview.AnswerRequest;
import com.interviewai.dto.interview.AnswerResponse;
import com.interviewai.dto.interview.CreateInterviewRequest;
import com.interviewai.dto.interview.InterviewPageRequest;
import com.interviewai.dto.interview.InterviewResponse;
import com.interviewai.dto.interview.InterviewStateResponse;
import com.interviewai.dto.interview.InterviewSummaryResponse;
import com.interviewai.dto.interview.NextQuestionResponse;
import com.interviewai.model.InterviewSession;

import java.util.List;

public interface InterviewService {

    InterviewResponse create(String userId, CreateInterviewRequest request);

    List<InterviewSummaryResponse> list(String userId);

    /** New paginated list. */
    CursorPage<InterviewSummaryResponse> listPaginated(String userId, InterviewPageRequest request);

    InterviewResponse get(String userId, String interviewId);

    InterviewSession getOwned(String userId, String interviewId);

    InterviewStateResponse getState(String userId, String interviewId);

    NextQuestionResponse start(String userId, String interviewId);

    NextQuestionResponse nextQuestion(String userId, String interviewId);

    AnswerResponse submitAnswer(String userId, String interviewId, AnswerRequest request);

    InterviewStateResponse complete(String userId, String interviewId);
}
