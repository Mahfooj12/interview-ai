export type InterviewType =
  | "TECHNICAL"
  | "HR"
  | "RESUME"
  | "PROJECT"
  | "SYSTEM_DESIGN"
  | "BEHAVIORAL";

export type InterviewMode = "TEXT" | "VOICE" | "VIDEO";
export type InterviewDifficulty = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";
export type InterviewPersonality =
  | "FRIENDLY"
  | "STRICT"
  | "PROFESSIONAL"
  | "TECHNICAL"
  | "HR_MANAGER";

export type InterviewStatus =
  | "SCHEDULED"
  | "IN_PROGRESS"
  | "COMPLETED"
  | "ABANDONED";

export type QuestionType =
  | "BASIC"
  | "SCENARIO"
  | "PROBLEM_SOLVING"
  | "FOLLOW_UP"
  | "TRUTH_TEST";

export type QuestionGeneratedFrom =
  | "RESUME"
  | "JD"
  | "PREVIOUS_ANSWER"
  | "CLAIM"
  | "FOCUS_TOPIC";

export interface InterviewState {
  currentQuestionIndex: number;
  totalQuestionsAsked: number;
  currentTopic: string | null;
  weakAreas: string[];
  strongAreas: string[];
  cumulativeScore: number;
  rollingSummary: string | null;
  lastQuestionId: string | null;
  completed: boolean;
  lastUpdatedAt: string | null;
}

export interface InterviewResponse {
  id: string;
  userId: string;
  resumeId: string;
  jobDescriptionId: string | null;
  type: InterviewType;
  mode: InterviewMode;
  difficulty: InterviewDifficulty;
  personality: InterviewPersonality;
  status: InterviewStatus;
  state: InterviewState;
  questionIds: string[];
  focusTopic: string | null;
  startedAt: string | null;
  completedAt: string | null;
  durationSec: number;
  createdAt: string;
  updatedAt: string;
}

export interface InterviewSummary {
  id: string;
  type: InterviewType;
  mode: InterviewMode;
  difficulty: InterviewDifficulty;
  personality: InterviewPersonality;
  status: InterviewStatus;
  totalQuestionsAsked: number;
  cumulativeScore: number;
  startedAt: string | null;
  completedAt: string | null;
  durationSec: number;
}

export interface QuestionResponse {
  id: string;
  interviewId: string;
  order: number;
  type: QuestionType;
  text: string;
  expectedTopics: string[];
  difficulty: InterviewDifficulty;
  generatedFrom: QuestionGeneratedFrom;
  parentQuestionId: string | null;
  createdAt: string;
}

export interface NextQuestionResponse {
  interviewId: string;
  status: InterviewStatus;
  question: QuestionResponse | null;
  questionNumber: number;
  weakAreas: string[];
  strongAreas: string[];
  cumulativeScore: number;
  completed: boolean;
  message: string;
}

export interface EvaluationResponse {
  id: string;
  interviewId: string;
  questionId: string;
  answerId: string;
  userId: string;
  technicalAccuracy: number;
  relevance: number;
  depth: number;
  clarity: number;
  communication: number;
  confidence: number;
  overall: number;
  strengths: string[];
  weaknesses: string[];
  weakAreasDetected: string[];
  feedback: string;
  idealAnswer: string;
  followUpNeeded: boolean;
  createdAt: string;
}

export interface AnswerResponse {
  interviewId: string;
  answerId: string;
  questionId: string;
  evaluation: EvaluationResponse;
  nextQuestion: NextQuestionResponse | null;
  status: InterviewStatus;
  weakAreas: string[];
  strongAreas: string[];
  cumulativeScore: number;
  completed: boolean;
}

export interface CreateInterviewRequest {
  resumeId?: string;
  jobDescriptionId?: string;
  type: InterviewType;
  mode: InterviewMode;
  difficulty: InterviewDifficulty;
  personality: InterviewPersonality;
  focusTopic?: string;
}

export interface AnswerRequest {
  questionId: string;
  text: string;
  audioUrl?: string;
  videoUrl?: string;
}