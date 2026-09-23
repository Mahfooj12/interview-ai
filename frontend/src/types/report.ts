import type {
  InterviewDifficulty,
  InterviewMode,
  InterviewType,
} from "./interview";

export interface ReportSections {
  overall: number;
  technical: number;
  communication: number;
  problemSolving: number;
  confidence: number;
  resumeKnowledge: number;
  projectKnowledge: number;
}

export interface QuestionFeedback {
  questionId: string;
  question: string | null;
  answer: string | null;
  score: number;
  feedback: string | null;
  idealAnswer: string | null;
}

export interface LearningResource {
  type: "COURSE" | "ARTICLE" | "PRACTICE" | "VIDEO";
  title: string;
  url: string;
}

export interface LearningItem {
  topic: string;
  priority: "HIGH" | "MEDIUM" | "LOW";
  resources: LearningResource[];
}

export interface CommunicationStats {
  wpm: number;
  fillerWordCount: number;
  fillerWords: string[];
  hesitationCount: number;
  clarityScore: number;
  confidenceScore: number;
  grammarScore: number;
  toneScore: number;
}

export interface ReportResponse {
  id: string;
  interviewId: string;
  userId: string;
  sections: ReportSections;
  strongAreas: string[];
  weakAreas: string[];
  questionFeedback: QuestionFeedback[];
  recommendedTopics: string[];
  learningRoadmap: LearningItem[];
  communicationStats: CommunicationStats | null;
  summary: string | null;
  generatedAt: string;
}

export interface ReportSummary {
  id: string;
  interviewId: string;
  overallScore: number;
  technical: number;
  communication: number;
  confidence: number;
  problemSolving: number;
  generatedAt: string;
}

/** Aggregated per-user progress shape returned by GET /api/progress */
export interface ProgressSummary {
  totalInterviews: number;
  completedInterviews: number;
  averageScore: number;
  bestScore: number;
  averageTechnical: number;
  averageCommunication: number;
  averageConfidence: number;
  averageProblemSolving: number;
  topStrongAreas: string[];
  topWeakAreas: string[];
  timeline: ProgressPoint[];
}

export interface ProgressPoint {
  date: string;
  overallScore: number;
  technicalScore: number;
  communicationScore: number;
  confidenceScore: number;
  interviewsCompleted: number;
}

export interface ProgressSkill {
  skill: string;
  count: number;
  averageScore: number;
}

/** Metadata used by the report detail header. */
export interface ReportInterviewContext {
  type: InterviewType;
  mode: InterviewMode;
  difficulty: InterviewDifficulty;
  durationSec: number;
  completedAt: string | null;
}