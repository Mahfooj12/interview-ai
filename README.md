<div align="center">

# 🎯 InterviewAI

**AI-Powered Mock Interview Platform**

Practice real interviews with AI that reads your resume, adapts to your answers, and gives you a detailed improvement report.

[![Live Demo](https://img.shields.io/badge/Live-Demo-success?style=for-the-badge&logo=vercel)](https://interview-ai-md-b744.vercel.app/)
[![Backend API](https://img.shields.io/badge/Backend-API-blue?style=for-the-badge&logo=render)](https://interview-ai-r93q.onrender.com/actuator/health)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-black?style=for-the-badge&logo=github)](https://github.com/Mahfooj12/interview-ai)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](#license)

**[Live Demo](https://interview-ai-md-b744.vercel.app/)** · **[API Health](https://interview-ai-r93q.onrender.com/actuator/health)** · **[GitHub](https://github.com/Mahfooj12/interview-ai)**

</div>

---

## 📖 Overview

**InterviewAI** is a production-grade full-stack SaaS platform that conducts **personalized AI mock interviews** based on a candidate's resume and target job description. It supports **text, voice, and video** interview modes, generates **adaptive follow-up questions**, evaluates answers across multiple dimensions, and produces a **detailed improvement report** with a learning roadmap.

Whether you're preparing for a technical round, an HR interview, or a system design discussion, InterviewAI simulates a real interviewer that listens, thinks, and responds — just like a human.

---

## ✨ Features

### 🎤 Interview Modes
- **Text Mode** — Type your answers in a distraction-free chat UI
- **Voice Mode** — Speak your answers; live transcription via Chrome Web Speech API
- **Video Mode** — WebRTC camera preview + animated AI interviewer avatar

### 🧠 AI-Powered Intelligence
- **Resume Parsing** — Extracts skills, projects, experience, education, and technical claims
- **Job Description Analysis** — Matches your resume against a target role
- **Adaptive Question Generation** — Personalizes questions based on your resume, JD, and previous answers
- **Dynamic Follow-ups** — Asks deeper questions when answers are vague or incomplete
- **Difficulty Escalation** — Adjusts difficulty based on your real-time performance
- **Multi-dimensional Evaluation** — Scores technical accuracy, relevance, depth, clarity, communication, and confidence
- **Weak/Strong Area Detection** — Tracks skill gaps as the interview progresses

### 📊 Reports & Analytics
- **Overall Score** with radial visualization
- **Section Breakdown** — Technical, Communication, Problem Solving, Confidence
- **Question-by-Question Feedback** with ideal answers
- **Communication Analysis** — Words per minute, filler words, hesitation, clarity
- **Personalized Learning Roadmap** — Curated courses, articles, and practice resources
- **Progress Dashboard** — Track improvement across interviews over time

### 🔐 Authentication & Security
- JWT authentication with **refresh token rotation**
- BCrypt password hashing
- Role-based access control (USER / ADMIN)
- CORS, rate limiting, input validation, secure file upload

### 🎯 Interview Configuration
- **6 Interview Types** — Technical, HR, Resume deep-dive, Project, System Design, Behavioral
- **3 Difficulty Levels** — Beginner, Intermediate, Advanced
- **5 Interviewer Personalities** — Friendly, Strict, Professional, Technical, HR Manager
- **Focus Topic Mode** — Practice specific weak areas

---

## 📸 Screenshots

### 🏠 Landing Page
<img width="1355" height="596" alt="Screenshot 2026-09-24 192432" src="https://github.com/user-attachments/assets/a0f4d18e-3f44-4f55-ab9c-3945a521245d" />


### 📊 Dashboard
<img width="1348" height="596" alt="Screenshot 2026-09-24 193549" src="https://github.com/user-attachments/assets/53275bd7-d4cb-4cea-92bc-29992d02139b" />

### 🎥 Video Interview Mode
<img width="1334" height="577" alt="Screenshot 2026-09-24 194328" src="https://github.com/user-attachments/assets/c050319c-c82b-43d5-a4c6-c0eb86f6f767" />


### 📄 Resume Analysis
<img width="1112" height="533" alt="Screenshot 2026-09-24 194020" src="https://github.com/user-attachments/assets/bce4fa8b-c9bf-4542-bfd2-a297ccf86af5" />
<img width="1108" height="514" alt="Screenshot 2026-09-24 194054" src="https://github.com/user-attachments/assets/a5ec628a-85bc-49f6-b26d-ca94bf044877" />



### 📈 Interview Report
<img width="1360" height="593" alt="Screenshot 2026-09-24 194148" src="https://github.com/user-attachments/assets/70e4884b-a5fa-440d-bdbf-f0b96a522376" />
<img width="1337" height="590" alt="Screenshot 2026-09-24 194812" src="https://github.com/user-attachments/assets/15835fd5-f153-40ee-8cae-3867bb0f9d87" />



---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│              CLIENT — Next.js 15 (Vercel)                    │
│   App Router · TypeScript · Tailwind · shadcn/ui · Zustand   │
│   React Query · Framer Motion · Recharts · Web Speech API    │
└──────────────────────┬───────────────────────────────────────┘
                       │ HTTPS (REST) + WSS
                       ▼
┌──────────────────────────────────────────────────────────────┐
│           BACKEND — Spring Boot 3 (Render)                   │
│   Java 21 · Spring Security · JWT · Spring Data MongoDB      │
│   REST APIs · WebSocket · 40+ Endpoints · Docker             │
└──────────────────────┬───────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┬───────────────┐
        ▼              ▼              ▼               ▼
   ┌─────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
   │ MongoDB │   │ Gemini   │   │ Chrome   │   │ WebRTC   │
   │  Atlas  │   │   API    │   │ WebSpeech│   │  Camera  │
   └─────────┘   └──────────┘   └──────────┘   └──────────┘
```

---

## 🛠️ Tech Stack

### Frontend
| Category | Technology |
|---|---|
| Framework | Next.js 15 (App Router) |
| Language | TypeScript |
| Styling | Tailwind CSS, shadcn/ui |
| State | Zustand, React Query |
| Animation | Framer Motion |
| Charts | Recharts |
| Forms | React Hook Form, Zod |
| HTTP | Axios |
| Icons | Lucide React |
| Voice | Web Speech API |
| Video | WebRTC |

### Backend
| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security, JWT |
| Data | Spring Data MongoDB |
| Realtime | Spring WebSocket |
| Build | Maven |
| Docs | SpringDoc OpenAPI |

### Database & AI
| Category | Technology |
|---|---|
| Database | MongoDB Atlas |
| AI Provider | Google Gemini API |
| Model | `gemini-flash-lite-latest` |

### DevOps & Deployment
| Category | Technology |
|---|---|
| Containerization | Docker |
| Frontend Hosting | Vercel |
| Backend Hosting | Render |
| CI/CD | GitHub Actions |

---

## 🚀 Live Demo

| Service | URL |
|---|---|
| **Frontend** | [https://interview-ai-md-b744.vercel.app/](https://interview-ai-md-b744.vercel.app/) |
| **Backend API** | [https://interview-ai-r93q.onrender.com](https://interview-ai-r93q.onrender.com) |
| **Health Check** | [https://interview-ai-r93q.onrender.com/actuator/health](https://interview-ai-r93q.onrender.com/actuator/health) |
| **Swagger UI** | [https://interview-ai-r93q.onrender.com/swagger-ui.html](https://interview-ai-r93q.onrender.com/swagger-ui.html) |

> ⏱️ **Note:** The backend runs on Render's **free tier**, which spins down after 15 minutes of inactivity. The first request after idle may take **30–60 seconds** to cold-start. Subsequent requests are fast.

---

## 💻 Local Setup

### Prerequisites
- **Java 21** — [Download](https://adoptium.net/temurin/releases/?version=21)
- **Node.js 20+** — [Download](https://nodejs.org/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **MongoDB Atlas account** — [Sign up free](https://www.mongodb.com/cloud/atlas/register)
- **Google Gemini API key** — [Get one free](https://aistudio.google.com/app/apikey)

### 1. Clone the repository

```bash
git clone https://github.com/Mahfooj12/interview-ai.git
cd interview-ai
```

### 2. Backend setup

```bash
cd backend
cp .env.example .env
```

Fill in `backend/.env`:

```env
MONGODB_URI=mongodb+srv://<user>:<pass>@cluster.mongodb.net/interviewai
MONGODB_DATABASE=interviewai
JWT_SECRET=<generate-with-openssl-rand-base64-64>
JWT_ACCESS_TOKEN_EXPIRATION_MS=900000
JWT_REFRESH_TOKEN_EXPIRATION_MS=604800000
AI_PROVIDER=gemini
GEMINI_API_KEY=<your-key-from-aistudio>
GEMINI_BASE_URL=https://generativelanguage.googleapis.com
GEMINI_MODEL=gemini-flash-lite-latest
SPEECH_PROVIDER=none
STORAGE_PROVIDER=local
CORS_ALLOWED_ORIGINS=http://localhost:3000
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

Run the backend:

```bash
mvn spring-boot:run
```

Backend runs at **http://localhost:8080**.

### 3. Frontend setup

```bash
cd ../frontend
npm install
```

Create `frontend/.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Run the frontend:

```bash
npm run dev
```

Frontend runs at **http://localhost:3000**.

### 4. Open the app

Go to **http://localhost:3000** → register an account → upload a resume → start an interview.

---

## 🐳 Docker Setup

Run the entire stack (backend + frontend) with Docker Compose:

```bash
docker compose up --build
```

- Frontend: **http://localhost:3000**
- Backend: **http://localhost:8080**

The backend uses MongoDB Atlas via `MONGODB_URI`. Make sure your IP is whitelisted in Atlas Network Access.

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Revoke refresh token |
| GET | `/api/auth/me` | Current user profile |

### Resumes
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/resumes` | Upload resume (PDF/DOCX) |
| GET | `/api/resumes` | List user's resumes |
| GET | `/api/resumes/{id}` | Get resume by ID |
| POST | `/api/resumes/{id}/analyze` | Trigger AI analysis |
| POST | `/api/resumes/{id}/primary` | Set as primary |
| DELETE | `/api/resumes/{id}` | Delete resume |

### Job Descriptions
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/jobs` | Create JD from text |
| POST | `/api/jobs/upload` | Upload JD file |
| GET | `/api/jobs` | List user's JDs |
| POST | `/api/jobs/{id}/match` | Match resume ↔ JD |

### Interviews
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/interviews` | Create interview session |
| GET | `/api/interviews` | List interviews |
| POST | `/api/interviews/{id}/start` | Start interview |
| POST | `/api/interviews/{id}/answer` | Submit answer |
| POST | `/api/interviews/{id}/next-question` | Get next question |
| POST | `/api/interviews/{id}/complete` | End interview |

### Reports & Progress
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports` | List user's reports |
| GET | `/api/reports/{id}` | Get report |
| GET | `/api/reports/by-interview/{interviewId}` | Get report by interview |
| GET | `/api/progress` | Aggregated progress |

**Full API documentation:** [Swagger UI](https://interview-ai-r93q.onrender.com/swagger-ui.html)

---

## 📁 Project Structure

```
interview-ai/
├── backend/                        # Spring Boot 3 + Java 21
│   ├── src/main/java/com/interviewai/
│   │   ├── ai/                     # Gemini integration, prompts
│   │   ├── config/                 # Security, MongoDB, CORS
│   │   ├── controller/             # REST controllers
│   │   ├── dto/                    # Request/response DTOs
│   │   ├── exception/              # Global error handling
│   │   ├── mapper/                 # Entity ↔ DTO mappers
│   │   ├── model/                  # MongoDB documents
│   │   ├── parser/                 # PDF / DOCX parsers
│   │   ├── repository/             # Spring Data repositories
│   │   ├── security/               # JWT, filters, user details
│   │   ├── service/                # Business logic
│   │   └── storage/                # File storage (Cloudinary / local)
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── prompts/                # AI prompt templates
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                       # Next.js 15 + TypeScript
│   ├── src/
│   │   ├── app/                    # App Router pages
│   │   ├── components/
│   │   │   ├── interview/          # Voice, video, text modes
│   │   │   ├── dashboard/
│   │   │   ├── report/
│   │   │   └── ui/                 # shadcn/ui primitives
│   │   ├── hooks/                  # React Query hooks
│   │   ├── lib/                    # API clients, auth
│   │   ├── stores/                 # Zustand stores
│   │   └── types/                  # TypeScript types
│   ├── public/
│   ├── package.json
│   └── next.config.ts
│
├── docs/
│   └── screenshots/                # App screenshots
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## 🎯 Roadmap

- [x] JWT auth with refresh tokens
- [x] Resume parsing (PDF / DOCX)
- [x] AI resume analysis
- [x] Job description matching
- [x] Text interview mode
- [x] Voice interview mode (Web Speech API)
- [x] Video interview mode (WebRTC + avatar)
- [x] Adaptive follow-up questions
- [x] Multi-dimensional answer evaluation
- [x] Report generation with learning roadmap
- [x] Progress dashboard
- [x] Admin panel
- [ ] Deepgram STT for better voice accuracy
- [ ] Custom AI avatar via Tavus
- [ ] Coding interview mode with Monaco editor
- [ ] Team/enterprise plans
- [ ] Mobile app (React Native)

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Mohammad Mahfooj**

- 🔗 LinkedIn: [mohammad-mahfooj-199012254](https://www.linkedin.com/in/mohammad-mahfooj-199012254/)
- 💻 GitHub: [@Mahfooj12](https://github.com/Mahfooj12)
- 📧 Email: [mohdmahfooj95@gmail.com](mailto:mohdmahfooj95@gmail.com)
- 🎯 LeetCode: [Mahfooj](https://leetcode.com/u/Mahfooj/)

---

<div align="center">

**⭐ If you found this project helpful, please give it a star! ⭐**

Made with ❤️ by [Mohammad Mahfooj](https://github.com/Mahfooj12)

</div>
