import type { ParsedResume } from "./resume";

export interface ParsedJobDescription {
  title: string | null;
  company: string | null;
  seniority: "JUNIOR" | "MID" | "SENIOR" | "LEAD" | null;
  requiredSkills: string[];
  preferredSkills: string[];
  technologies: string[];
  responsibilities: string[];
  experienceRequirements: string[];
}

export interface MatchResult {
  matchedSkills: string[];
  partiallyMatchedSkills: string[];
  missingSkills: string[];
  matchPercentage: number;
}

export interface JobDescriptionSummary {
  id: string;
  title: string | null;
  company: string | null;
  seniority: string | null;
  requiredSkillCount: number;
  matchPercentage: number | null;
  createdAt: string;
}

export interface JobDescriptionResponse {
  id: string;
  userId: string;
  title: string | null;
  company: string | null;
  rawText: string;
  fileName: string | null;
  fileUrl: string | null;
  parsed: ParsedJobDescription | null;
  matchResult: MatchResult | null;
  createdAt: string;
  updatedAt: string;
}

export interface JobMatchResponse {
  jobDescriptionId: string;
  resumeId: string;
  matchResult: MatchResult;
  message: string;
}

export interface JobCreateTextPayload {
  rawText: string;
  title?: string;
  company?: string;
  resumeId?: string;
}

/** Re-export for convenience */
export type { ParsedResume };