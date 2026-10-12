# Module 1: Backend Architecture & Authentication Lead
**Lead Engineer:** Tavish Bhanawat  
**Enrollment No:** 24E1ARADM40P163 | **Roll No:** 24EARAD163  
**Email:** tavishbhanawat@gmail.com | **Mobile:** 8890628152  
**Assigned Role:** Backend Architect & Auth Lead  

---

## 1. Module Overview & Core Ownership
Module 1 constitutes the foundational server-side nervous system of **CampusVoice**. It is responsible for:
1. **MVC Web Architecture:** Pure Java Jakarta Servlet 6.0 controllers routing incoming HTTP actions.
2. **Apache Tomcat 10+ Setup:** Complete deployment descriptor configuration (`web.xml`), context lifecycle, and MIME mapping.
3. **Authentication Servlets:** Handling secure session generation (`LoginServlet`), session invalidation (`LogoutServlet`), and multi-role credentials dispatching.
4. **Session Security & Guards:** Role-based request interceptor (`AuthFilter`), Cross-Origin Resource Sharing (`CorsFilter`), UTF-8 Request/Response encoding (`EncodingFilter`), and structured request tracing (`LoggingFilter`).
5. **Identity Decoupling Barrier:** Enforcing server-side barriers so that issue records are stripped of user/student identity tokens before reaching persistence layers.

---

## 2. Directory Structure & Key Artifacts
```
Module-1_Backend_Architecture_Auth/
├── WEB-INF/
│   └── web.xml               # Tomcat 10 deployment descriptor & servlet mapping
├── src/
│   ├── controller/           # HTTP Request handlers (Servlets)
│   │   ├── LoginServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── SubmitIssueServlet.java
│   │   ├── FetchClassIssuesServlet.java
│   │   ├── VoteServlet.java
│   │   ├── TrackIssueServlet.java
│   │   ├── AdminDashboardServlet.java
│   │   ├── UpdateResolutionServlet.java
│   │   └── ReverifyServlet.java
│   ├── filter/               # Security & Request Lifecycle Filters
│   │   ├── AuthFilter.java
│   │   ├── CorsFilter.java
│   │   ├── EncodingFilter.java
│   │   └── LoggingFilter.java
│   └── model/                # Data Transfer Objects & Domain Models
│       ├── User.java
│       ├── Issue.java
│       ├── IssueStatus.java
│       └── ApiResponse.java
├── pom.xml                   # Maven Build Specification
├── build.ps1                 # Automated Packaging & Test Script
└── README.md                 # Module Documentation
```

---

## 3. Security Implementation Highlights
- **Session Hijacking Prevention:** HTTP-Only and Secure session cookies enforced through servlet context flags.
- **Identity Barrier:** The `SubmitIssueServlet` consumes `studentId` solely to retrieve student `classId` from session; only `classId` is forwarded to the issue data model.
- **Unified JSON Response Standard:** All controllers return an immutable `ApiResponse<T>` payload:
  ```json
  {
    "success": true,
    "message": "Operation completed successfully",
    "data": { ... }
  }
  ```

---

## 4. How to Build & Test This Module
```powershell
# Build and package the WAR file using the automated script:
.\build.ps1
```
Output artifact generated at `target/campusvoice.war` for deployment on Apache Tomcat 10.1.60+.
