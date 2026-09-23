export interface ParsedResumeProject {
  name: string;
  tech: string[];
  description: string;
  duration?: string | null;
}

export interface ParsedResumeExperience {
  company: string;
  role: string;
  from?: string | null;
  to?: string | null;
  description: string;
}

export interface ParsedResumeEducation {
  institute: string;
  degree: string;
  year?: string | null;
}

export interface ParsedResumeTechnicalClaim {
  topic: string;
  evidence: string;
}

export interface ParsedResume {
  name: string | null;
  email: string | null;
  phone: string | null;
  summary: string | null;
  skills: string[];
  languages: string[];
  frameworks: string[];
  databases: string[];
  tools: string[];
  projects: ParsedResumeProject[];
  experience: ParsedResumeExperience[];
  education: ParsedResumeEducation[];
  certifications: string[];
  achievements: string[];
  technicalClaims: ParsedResumeTechnicalClaim[];
}

export interface ResumeSummary {
  id: string;
  fileName: string;
  fileType: "PDF" | "DOCX";
  primaryResume: boolean;
  parsedName: string | null;
  skillCount: number;
  projectCount: number;
  createdAt: string;
}

export interface ResumeResponse {
  id: string;
  userId: string;
  fileName: string;
  fileUrl: string;
  fileType: "PDF" | "DOCX";
  fileSize: number;
  parsed: ParsedResume | null;
  primaryResume: boolean;
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface ResumeUploadResponse {
  resumeId: string;
  fileName: string;
  fileUrl: string;
  fileType: "PDF" | "DOCX";
  parsed: boolean;
  analyzed: boolean;
  message: string;
}

export interface ResumeAnalysisResponse {
  resumeId: string;
  analyzed: boolean;
  parsed: ParsedResume | null;
  message: string;
}