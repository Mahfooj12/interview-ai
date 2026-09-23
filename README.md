# InterviewAI

An AI-powered mock interview platform. Upload a resume, optionally add a job
description, and InterviewAI conducts a personalized, adaptive interview and
produces a detailed performance report with a learning roadmap.

## Tech Stack

- **Frontend:** Next.js 15, TypeScript, Tailwind CSS, shadcn/ui, React Query, Recharts, Framer Motion
- **Backend:** Java 21, Spring Boot 3, Spring Security, JWT, Spring Data MongoDB, WebSocket
- **Database:** MongoDB Atlas
- **AI:** DeepSeek API (chat completions)
- **Storage:** Cloudinary or S3-compatible
- **Deployment:** Docker, GitHub Actions, AWS

## Features

- User registration / login / JWT + refresh tokens
- Resume upload (PDF / DOCX) with parsing
- AI resume analysis and structured extraction
- Job description upload/paste + resume↔JD matching
- Adaptive interview engine (stateful, follow-up aware)
- Text and voice interview modes
- Answer evaluation across multiple dimensions
- Final report with scores, feedback, and learning roadmap
- Progress tracking and weak-area practice
- Admin panel with role-based access

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+
- Docker + Docker Compose
- MongoDB Atlas account
- DeepSeek API key
- (Optional) Cloudinary account

## Quick Start

### 1. Clone the repo

```bash
git clone https://github.com/your-org/interview-ai.git
cd interview-ai