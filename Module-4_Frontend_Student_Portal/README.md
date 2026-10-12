# Module 4: Frontend Lead (Student Portal)
**Lead Engineer:** Yash Raj  
**Enrollment No:** 24E1ARADM40P183 | **Roll No:** 24EARAD183  
**Email:** yashkumarafs43@gmail.com | **Mobile:** 7667348426  
**Assigned Role:** Frontend Lead (Student Portal)  

---

## 1. Module Overview & Core Ownership
Module 4 delivers the user-facing presentation layer and intuitive interaction design for **CampusVoice**. It is responsible for:
1. **Responsive Web UI Architecture:** Pure semantic HTML5, modern CSS3 layout systems (Flexbox, CSS Grid), and responsive vanilla JavaScript (zero external bloat).
2. **Student Issue Submission Portal (`modal-report`):** Guided grievance filing wizard with dynamic category selection (Classroom, Lab, Faculty, Hostel, Canteen, etc.), character limits, priority escalation, and cryptographic CV-ID generation.
3. **Anonymous Peer-Voting Interface (`modal-verify`):** Class voting interface with daily single-use verification tokens, 3-tier weighted options (*Frequently*, *Sometimes*, *Never*), and animated real-time voting progress meters.
4. **Interactive 5-Stage Stepper (`modal-track`):** Visual timeline tracker showing issue trajectory from `REPORTED` through `RESOLVED` / `REOPENED` with assigned departmental tags and admin feedback.
5. **Theme Switcher & Accessibility:** Instant Dark Mode / Light Mode toggle with local storage persistence and accessible typography.

---

## 2. Directory Structure & Key Artifacts
```
Module-4_Frontend_Student_Portal/
├── index.html                # Single-Page Web Application UI (90KB+)
├── style.css                 # Comprehensive Modern Design System (24KB+)
├── app.js                    # Core Interactive State & Event Handlers (74KB+)
└── README.md                 # UI/UX Specifications and Guide
```

---

## 3. Key Components & Modals

### A. Dual Role Authentication View
- Tabbed interface separating Student Login (`183` / `1234`), Student Registration with duplicate check, and Admin Authentication (`admin` / `admin123`).
- Instant public tracking search bar directly accessible from the login screen without authentication.

### B. Student Dashboard & Daily Token Shield
- Real-time profile card displaying student department, semester, and academic year.
- **Daily Shield Token Widget:** Displays today's cryptographic vote token (`CV1832026`) with one-click copy and state reset for demonstration.

### C. Anonymous Issue Filing Wizard
- Fields: Category, Urgency Priority (*Low*, *Medium*, *High*, *Critical*), Title, Description (up to 500 characters with live countdown), and file attachment dropzone.
- Automatic generation of permanent tracking key (e.g., `CV-4821`).

### D. Visual 5-Stage Milestone Stepper
```
[1. Reported] ──> [2. Peer Verifying] ──> [3. Class Verified] ──> [4. Dept Assigned] ──> [5. Resolved]
```
Color-coded status milestones with department allocation badges and detailed administrative remarks.

---

## 4. How to Run Locally
Open `index.html` directly in any modern browser (Chrome, Edge, Firefox, Safari) or use a local static server:
```powershell
npx serve .
```
Access at `http://localhost:3000` or double-click `index.html`.
