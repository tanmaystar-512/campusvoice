/**
 * ========================================================
 * CampusVoice - Complete Interactive Web Application Engine
 * Lead: Yash Raj (24EARAD183)
 * Branch: B.Tech Artificial Intelligence & Data Science (Sec C)
 * College: Arya College of Engineering & IT, Jaipur (RTU Kota)
 * ========================================================
 */

// -------------------------------------------------------------
// 1. Initial State & Configuration
// -------------------------------------------------------------
const ESTIMATED_CLASS_SIZE = 60;
const DEFAULT_DEMO_TOKEN = "CV1832026";

const INITIAL_ISSUES = [
  {
    id: "CV-1847",
    category: "Classroom",
    priority: "HIGH",
    assignedDept: "Electrical & Maintenance",
    title: "Air Conditioner malfunction in Room 304 (C-Block)",
    description: "The split AC unit makes loud grinding noise and cooling cuts out after 10 minutes of lecture.",
    evidence: "evidence_room304.jpg",
    date: "2026-10-07",
    status: "REPORTED",
    adminRemarks: "Facility manager notified. Awaiting technician inspection.",
    votes: { frequently: 4, sometimes: 2, never: 1 },
    reverify: { resolved: 0, partially: 0, stillFacing: 0 }
  },
  {
    id: "CV-4821",
    category: "Laboratory",
    priority: "CRITICAL",
    assignedDept: "IT Cell & Laboratories",
    title: "GPU workstations rebooting during PyTorch model training in AI Lab 2",
    description: "Systems 14 to 22 experience kernel panics and black screens under GPU load. Pending cohort verification voting.",
    evidence: "crash_log.png",
    date: "2026-10-08",
    status: "UNDER VERIFICATION",
    adminRemarks: "Diagnostic initiated. Systems benchmarked with CUDA 12.4.",
    votes: { frequently: 28, sometimes: 8, never: 2 },
    reverify: { resolved: 0, partially: 0, stillFacing: 0 }
  },
  {
    id: "CV-7392",
    category: "Other",
    priority: "MEDIUM",
    assignedDept: "Electrical & Maintenance",
    title: "Water dispenser RO filter replacement required on 2nd Floor",
    description: "Water pressure low and TDS indicator blinking red near Department office.",
    evidence: "filter_photo.jpg",
    date: "2026-10-05",
    status: "RESOLVED",
    adminRemarks: "New commercial RO membrane and filter cartridges installed by vendor.",
    votes: { frequently: 32, sometimes: 10, never: 1 },
    reverify: { resolved: 8, partially: 1, stillFacing: 0 }
  },
  {
    id: "CV-2950",
    category: "Canteen",
    priority: "MEDIUM",
    assignedDept: "Canteen Management Board",
    title: "Digital payment terminal QR code scanner damaged",
    description: "Students experiencing long queue bottlenecks during short breaks due to cash-only transactions.",
    evidence: "scanner_damage.jpg",
    date: "2026-10-09",
    status: "REPORTED",
    adminRemarks: "Vendor contacted to deploy backup UPI soundbox terminal.",
    votes: { frequently: 6, sometimes: 1, never: 0 },
    reverify: { resolved: 0, partially: 0, stillFacing: 0 }
  },
  {
    id: "CV-5120",
    category: "Faculty",
    priority: "HIGH",
    assignedDept: "Academic Dean Office",
    title: "Computer Vision practical lab session scheduling overlap",
    description: "AI Lab 3 timetable shows concurrent slot reservation for both Sec B and Sec C on Thursdays.",
    evidence: "timetable_clash.pdf",
    date: "2026-10-06",
    status: "CLASS VERIFIED",
    adminRemarks: "Academic coordinator revising timetable to assign Sec C to AI Lab 1.",
    votes: { frequently: 24, sometimes: 6, never: 1 },
    reverify: { resolved: 0, partially: 0, stillFacing: 0 }
  }
];

const INITIAL_STUDENTS = [
  {
    id: "183",
    password: "1234",
    name: "Yash Raj",
    roll: "24EARAD183",
    email: "yashraj.ai@aryacollege.in",
    dept: "B.Tech Artificial Intelligence & Data Science",
    section: "C"
  },
  {
    id: "185",
    password: "1234",
    name: "Rohan Verma",
    roll: "24EARAD185",
    email: "rohan.verma@aryacollege.in",
    dept: "B.Tech Artificial Intelligence & Data Science",
    section: "C"
  }
];

const INITIAL_AUDIT_LOGS = [
  {
    timestamp: "2026-10-05 10:15",
    issueId: "CV-7392",
    action: "ISSUE_REPORTED",
    actor: "ANONYMOUS_STUDENT",
    remarks: "Anonymous report filed for water dispenser filter replacement."
  },
  {
    timestamp: "2026-10-06 14:30",
    issueId: "CV-7392",
    action: "STATUS_PROMOTED",
    actor: "VERIFICATION_ENGINE",
    remarks: "Quorum reached (71.6% participation). Promoted to CLASS VERIFIED."
  },
  {
    timestamp: "2026-10-07 16:45",
    issueId: "CV-7392",
    action: "STATUS_RESOLVED",
    actor: "ADMIN (DEPT_HEAD)",
    remarks: "Maintenance completed; replacement verified."
  },
  {
    timestamp: "2026-10-08 11:20",
    issueId: "CV-4821",
    action: "ISSUE_REPORTED",
    actor: "ANONYMOUS_STUDENT",
    remarks: "Critical GPU workstation rebooting reported in AI Lab 2."
  },
  {
    timestamp: "2026-10-08 12:00",
    issueId: "CV-4821",
    action: "STATUS_CHANGE",
    actor: "VERIFICATION_ENGINE",
    remarks: "Class verification active. Daily tokens circulating."
  }
];

const CAMPUS_NOTICES = [
  {
    date: "10 Oct 2026",
    title: "AI Lab 2 GPU Workstation Diagnostic Schedule",
    body: "Technicians from the vendor support team will service workstations 14 through 22 between 2:00 PM and 5:00 PM on Friday.",
    dept: "IT Cell & Laboratories"
  },
  {
    date: "08 Oct 2026",
    title: "Canteen Payment Terminal Replacement",
    body: "A new multi-bank UPI QR scanner terminal has been deployed at Counter 2 to alleviate rush hour delays.",
    dept: "Canteen Management Board"
  },
  {
    date: "06 Oct 2026",
    title: "C-Block Water Filtration System Complete Overhaul",
    body: "TDS levels have been calibrated to RTU safety benchmarks (110 ppm) following replacement of all active carbon filters.",
    dept: "Electrical & Maintenance"
  }
];

const INITIAL_ADMINS = [
  {
    username: "admin",
    password: "admin123",
    name: "Master Administrator",
    email: "admin@aryacollege.in",
    department: "Central Grievance Office",
    role: "Super Admin",
    dateAdded: "2026-08-01",
    isPrimary: true
  },
  {
    username: "hod_cse",
    password: "admin123",
    name: "Dr. Sandeep Sharma",
    email: "hod.cse@aryacollege.in",
    department: "Academic Dean Office",
    role: "Department Head",
    dateAdded: "2026-09-15",
    isPrimary: false
  },
  {
    username: "proctor_it",
    password: "admin123",
    name: "Prof. Neha Gupta",
    email: "proctor@aryacollege.in",
    department: "IT Cell & Laboratories",
    role: "Proctorial Board",
    dateAdded: "2026-10-01",
    isPrimary: false
  }
];

// -------------------------------------------------------------
// 1.5. Full-Stack REST API Bridge (Express / Vercel / Render)
// -------------------------------------------------------------
const API_BASE = '/api';
let isBackendConnected = false;

const api = {
  async get(endpoint) {
    try {
      const res = await fetch(`${API_BASE}${endpoint}`, {
        headers: { 'Accept': 'application/json' }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return await res.json();
    } catch (err) {
      console.warn(`API GET ${endpoint} error:`, err.message);
      return null;
    }
  },
  async post(endpoint, data) {
    try {
      const res = await fetch(`${API_BASE}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify(data)
      });
      return await res.json();
    } catch (err) {
      console.warn(`API POST ${endpoint} error:`, err.message);
      return null;
    }
  }
};

async function syncWithBackend() {
  const pill = document.getElementById("backend-status-pill");
  const dot = document.getElementById("backend-status-dot");
  const text = document.getElementById("backend-status-text");

  try {
    const health = await api.get('/health');
    if (health && health.status === 'ok') {
      isBackendConnected = true;
      if (dot) dot.className = "w-2 h-2 rounded-full bg-emerald-400 animate-pulse";
      if (text) text.textContent = "Live API Connected";
      if (pill) {
        pill.className = "flex items-center gap-1.5 px-2.5 py-1 rounded-xl bg-emerald-950/40 border border-emerald-500/30 text-[11px] font-mono font-semibold text-emerald-300";
        pill.title = `Connected to ${health.platform || 'CampusVoice Backend API'} (v${health.version || '2.5.0'})`;
      }

      // Hydrate state from backend
      const [issuesRes, logsRes, adminsRes] = await Promise.all([
        api.get('/issues'),
        api.get('/audit-logs'),
        api.get('/admin/admins')
      ]);

      if (issuesRes && issuesRes.issues && issuesRes.issues.length > 0) {
        issues = issuesRes.issues;
      }
      if (logsRes && logsRes.logs && logsRes.logs.length > 0) {
        auditLogs = logsRes.logs;
      }
      if (adminsRes && adminsRes.admins && adminsRes.admins.length > 0) {
        admins = adminsRes.admins;
        if (admins[0]) currentAdmin = admins[0];
      }

      refreshDashboard();
      if (currentUserRole === 'admin') renderAdminDashboard();
      return true;
    }
  } catch (err) {
    console.warn("Backend handshake failed, falling back to local storage engine:", err);
  }

  // Fallback to local storage
  isBackendConnected = false;
  if (dot) dot.className = "w-2 h-2 rounded-full bg-amber-400";
  if (text) text.textContent = "Local Offline Mode";
  if (pill) {
    pill.className = "flex items-center gap-1.5 px-2.5 py-1 rounded-xl bg-slate-800/80 border border-slate-700/80 text-[11px] font-mono font-semibold text-slate-300";
    pill.title = "Local storage engine active. Connect backend to enable live sync.";
  }
  return false;
}

// -------------------------------------------------------------
// 2. State Loading & Persistence
// -------------------------------------------------------------
let issues = [];
try {
  const saved = localStorage.getItem("campusvoice_issues_v2");
  issues = saved ? JSON.parse(saved) : JSON.parse(JSON.stringify(INITIAL_ISSUES));
} catch (e) {
  issues = JSON.parse(JSON.stringify(INITIAL_ISSUES));
}

let students = [];
try {
  const savedStudents = localStorage.getItem("campusvoice_students_v2");
  students = savedStudents ? JSON.parse(savedStudents) : JSON.parse(JSON.stringify(INITIAL_STUDENTS));
} catch (e) {
  students = JSON.parse(JSON.stringify(INITIAL_STUDENTS));
}

let auditLogs = [];
try {
  const savedLogs = localStorage.getItem("campusvoice_audit_v2");
  auditLogs = savedLogs ? JSON.parse(savedLogs) : JSON.parse(JSON.stringify(INITIAL_AUDIT_LOGS));
} catch (e) {
  auditLogs = JSON.parse(JSON.stringify(INITIAL_AUDIT_LOGS));
}

let admins = [];
try {
  const savedAdmins = localStorage.getItem("campusvoice_admins_v2");
  admins = savedAdmins ? JSON.parse(savedAdmins) : JSON.parse(JSON.stringify(INITIAL_ADMINS));
} catch (e) {
  admins = JSON.parse(JSON.stringify(INITIAL_ADMINS));
}

// Preserve existing custom master admin password if any
const legacyAdminPass = localStorage.getItem("campusvoice_admin_password");
if (legacyAdminPass && admins[0]) {
  admins[0].password = legacyAdminPass;
}

let currentAdmin = admins[0];
let adminPassword = admins[0].password;
let currentStudent = students[0];
let isTokenUsed = localStorage.getItem("campusvoice_token_used_v2") === "true";
let currentUserRole = null; // 'student' | 'admin' | null
let selectedVoteOption = null;
let selectedReverifyOption = null;
let currentAdminSelectedId = null;
let currentTableFilterStatus = "ALL";

// Active OTP session for password recovery
let activeResetSession = null; // { account: {...}, code: "123456", expiresAt: number }

function isMasterAdmin() {
  return !!(currentAdmin && (currentAdmin.isPrimary === true || currentAdmin.username.toLowerCase() === "admin"));
}

function saveState() {
  try {
    localStorage.setItem("campusvoice_issues_v2", JSON.stringify(issues));
    localStorage.setItem("campusvoice_students_v2", JSON.stringify(students));
    localStorage.setItem("campusvoice_audit_v2", JSON.stringify(auditLogs));
    localStorage.setItem("campusvoice_admins_v2", JSON.stringify(admins));
    localStorage.setItem("campusvoice_token_used_v2", isTokenUsed ? "true" : "false");
    localStorage.setItem("campusvoice_admin_password", admins[0].password);
  } catch (e) {
    console.warn("Storage error saving state", e);
  }
}

function logAuditEvent(issueId, action, actor, remarks) {
  const now = new Date();
  const dateStr = now.toISOString().replace("T", " ").substring(0, 16);
  auditLogs.unshift({
    timestamp: dateStr,
    issueId,
    action,
    actor,
    remarks
  });
  saveState();
}

// -------------------------------------------------------------
// 3. Theme Engine (Light / Dark Mode)
// -------------------------------------------------------------
function initTheme() {
  const savedTheme = localStorage.getItem("campusvoice_theme") || "dark";
  document.documentElement.setAttribute("data-theme", savedTheme);
  updateThemeIcon(savedTheme);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute("data-theme") || "dark";
  const target = current === "dark" ? "light" : "dark";
  document.documentElement.setAttribute("data-theme", target);
  localStorage.setItem("campusvoice_theme", target);
  updateThemeIcon(target);
  showToast("Theme Updated", `Switched to ${target.toUpperCase()} mode.`, "info");
}

function updateThemeIcon(theme) {
  const icon = document.getElementById("theme-icon");
  if (!icon) return;
  if (theme === "light") {
    icon.className = "fa-solid fa-sun text-amber-500 text-sm";
  } else {
    icon.className = "fa-solid fa-moon text-blue-400 text-sm";
  }
}

// -------------------------------------------------------------
// 4. Authentication & Role Switcher
// -------------------------------------------------------------
function switchAuthTab(tab) {
  const tabs = {
    student: document.getElementById("auth-tab-student"),
    register: document.getElementById("auth-tab-register"),
    admin: document.getElementById("auth-tab-admin"),
    track: document.getElementById("auth-tab-track")
  };

  const forms = {
    student: document.getElementById("form-student-login"),
    register: document.getElementById("form-student-register"),
    admin: document.getElementById("form-admin-login"),
    track: document.getElementById("form-public-track")
  };

  // Reset tab button styles
  Object.keys(tabs).forEach(k => {
    if (tabs[k]) {
      tabs[k].className = "py-2 px-1 rounded-lg font-bold text-[11px] sm:text-xs transition-all flex items-center justify-center gap-1 text-slate-400 hover:text-white";
    }
    if (forms[k]) {
      forms[k].classList.add("hidden");
    }
  });

  // Activate chosen tab
  if (tabs[tab]) {
    if (tab === "student") {
      tabs[tab].className = "py-2 px-1 rounded-lg font-bold text-[11px] sm:text-xs transition-all flex items-center justify-center gap-1 bg-blue-600 text-white shadow-md";
    } else if (tab === "register") {
      tabs[tab].className = "py-2 px-1 rounded-lg font-bold text-[11px] sm:text-xs transition-all flex items-center justify-center gap-1 bg-emerald-600 text-white shadow-md";
    } else if (tab === "admin") {
      tabs[tab].className = "py-2 px-1 rounded-lg font-bold text-[11px] sm:text-xs transition-all flex items-center justify-center gap-1 bg-amber-600 text-white shadow-md";
    } else if (tab === "track") {
      tabs[tab].className = "py-2 px-1 rounded-lg font-bold text-[11px] sm:text-xs transition-all flex items-center justify-center gap-1 bg-indigo-600 text-white shadow-md";
    }
  }

  if (forms[tab]) {
    forms[tab].classList.remove("hidden");
  }
}

function togglePasswordVisibility(inputId, btn) {
  const input = document.getElementById(inputId);
  if (!input) return;
  const icon = btn.querySelector("i");
  if (input.type === "password") {
    input.type = "text";
    icon.className = "fa-solid fa-eye-slash";
  } else {
    input.type = "password";
    icon.className = "fa-regular fa-eye";
  }
}

async function handleStudentLogin(e) {
  if (e && e.preventDefault) e.preventDefault();
  const id = document.getElementById("login-student-id").value.trim();
  const pass = document.getElementById("login-student-pass").value.trim();

  if (!id || !pass) {
    showToast("Input Required", "Please enter both Student ID and Password.", "warning");
    return;
  }

  let student = null;

  if (isBackendConnected) {
    const res = await api.post('/auth/login', { studentId: id, password: pass });
    if (res && res.success && res.role === 'student') {
      student = res.user;
    }
  }

  if (!student) {
    student = students.find(s => s.id.toLowerCase() === id.toLowerCase() && s.password === pass);
  }

  if (student) {
    currentStudent = student;
    currentUserRole = "student";

    document.getElementById("view-auth").classList.add("hidden");
    document.getElementById("view-admin").classList.add("hidden");
    document.getElementById("view-student").classList.remove("hidden");

    updateUserNavigation(student, "student");
    updateStudentDashboardViews(student);
    refreshDashboard();

    const src = isBackendConnected ? "Live Backend" : "Local Engine";
    showToast(`Welcome ${student.name}!`, `Authenticated via ${src} for ${student.roll || student.id} (Sec ${student.section || 'C'})`, "success");
  } else {
    showToast("Authentication Failed", "Invalid credentials. Use your registered Student ID & password, or create an account.", "error");
  }
}

async function handleStudentRegister(e) {
  e.preventDefault();
  const name = document.getElementById("reg-name").value.trim();
  const id = document.getElementById("reg-id").value.trim();
  const roll = document.getElementById("reg-roll").value.trim();
  const email = document.getElementById("reg-email").value.trim();
  const dept = document.getElementById("reg-dept").value;
  const sec = document.getElementById("reg-sec").value;
  const pass = document.getElementById("reg-pass").value.trim();
  const conf = document.getElementById("reg-conf").value.trim();

  if (!name || !id || !roll || !pass) {
    showToast("Validation Error", "Please fill all mandatory fields marked with an asterisk.", "warning");
    return;
  }

  if (pass.length < 4) {
    showToast("Security Notice", "Password must be at least 4 characters long.", "warning");
    return;
  }

  if (pass !== conf) {
    showToast("Password Mismatch", "Passwords do not match. Please re-enter.", "error");
    return;
  }

  if (students.some(s => s.id.toLowerCase() === id.toLowerCase())) {
    showToast("Duplicate Account", `Student ID "${id}" is already registered. Please login.`, "warning");
    return;
  }

  const newStudent = {
    id,
    password: pass,
    name,
    roll,
    email: email || `${id}@aryacollege.in`,
    dept,
    section: sec
  };

  if (isBackendConnected) {
    const res = await api.post('/auth/register', newStudent);
    if (res && !res.success) {
      showToast("Registration Error", res.message || "Failed to register on backend.", "error");
      return;
    }
  }

  students.push(newStudent);
  saveState();

  const src = isBackendConnected ? "Backend Database" : "Local Registry";
  showToast("Registration Complete!", `Account registered in ${src} for ${name} (ID: ${id}).`, "success");

  // Switch to login tab and prefill
  switchAuthTab("student");
  document.getElementById("login-student-id").value = id;
  document.getElementById("login-student-pass").value = "";
  document.getElementById("login-student-pass").focus();
}

async function handleAdminLogin(e) {
  if (e && e.preventDefault) e.preventDefault();
  const user = document.getElementById("login-admin-user").value.trim();
  const pass = document.getElementById("login-admin-pass").value.trim();

  if (!user || !pass) {
    showToast("Input Required", "Please enter Administrator credentials.", "warning");
    return;
  }

  let foundAdmin = null;

  if (isBackendConnected) {
    const res = await api.post('/auth/login', { username: user, password: pass });
    if (res && res.success && res.role === 'admin') {
      foundAdmin = res.user;
      // sync password
      foundAdmin.password = pass;
    }
  }

  if (!foundAdmin) {
    foundAdmin = admins.find(a => a.username.toLowerCase() === user.toLowerCase() && a.password === pass);
  }

  if (foundAdmin) {
    currentUserRole = "admin";
    currentAdmin = foundAdmin;

    document.getElementById("view-auth").classList.add("hidden");
    document.getElementById("view-student").classList.add("hidden");
    document.getElementById("view-admin").classList.remove("hidden");

    updateUserNavigation(foundAdmin, "admin");
    renderAdminDashboard();

    logAuditEvent(`ADMIN-${foundAdmin.username}`, "LOGIN", foundAdmin.name, `Administrator logged in: ${foundAdmin.username}`);
    const src = isBackendConnected ? "Secure Backend API" : "Local Authority";
    showToast("Authority Access Granted", `Welcome back, ${foundAdmin.name}! (${src})`, "success");
  } else {
    showToast("Access Denied", "Invalid Admin credentials. Check username & security password.", "error");
  }
}

function updateUserNavigation(user, role) {
  const chip = document.getElementById("nav-user-chip");
  if (!chip) return;

  chip.classList.remove("hidden");

  const initialsEl = document.getElementById("nav-user-initials");
  const nameEl = document.getElementById("nav-user-name");
  const roleEl = document.getElementById("nav-user-role");

  if (role === "admin") {
    const adminObj = currentAdmin || user || admins[0];
    const parts = (adminObj.name || "Admin").split(" ");
    const initials = parts.length >= 2 ? (parts[0][0] + parts[1][0]).toUpperCase() : (adminObj.name || "AD").substring(0, 2).toUpperCase();
    initialsEl.textContent = initials;
    initialsEl.className = "w-8 h-8 rounded-xl bg-gradient-to-tr from-amber-600 to-orange-600 flex items-center justify-center text-white font-black text-xs shadow";
    nameEl.textContent = adminObj.name;
    roleEl.textContent = `${adminObj.role} (${adminObj.department})`;
  } else {
    const parts = user.name.split(" ");
    const initials = parts.length >= 2 ? (parts[0][0] + parts[1][0]).toUpperCase() : user.name.substring(0, 2).toUpperCase();
    initialsEl.textContent = initials;
    initialsEl.className = "w-8 h-8 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-black text-xs shadow";
    nameEl.textContent = user.name;
    roleEl.textContent = `Student (${user.id})`;
  }
}

function updateStudentDashboardViews(student) {
  const parts = student.name.split(" ");
  const initials = parts.length >= 2 ? (parts[0][0] + parts[1][0]).toUpperCase() : student.name.substring(0, 2).toUpperCase();

  const avatar = document.getElementById("student-badge-avatar");
  if (avatar) avatar.textContent = initials;

  const nameEl = document.getElementById("student-badge-name");
  if (nameEl) nameEl.textContent = student.name;

  const rollEl = document.getElementById("student-badge-roll");
  if (rollEl) rollEl.textContent = student.roll;

  const deptEl = document.getElementById("student-badge-dept");
  if (deptEl) deptEl.textContent = `${student.dept} (Sec ${student.section})`;

  const tokenDisplay = document.getElementById("display-token-code");
  if (tokenDisplay) tokenDisplay.textContent = `CV${student.id}2026`;

  const pAvatar = document.getElementById("profile-modal-avatar");
  if (pAvatar) pAvatar.textContent = initials;
  const pName = document.getElementById("profile-modal-name");
  if (pName) pName.textContent = student.name;
  const pRoll = document.getElementById("profile-modal-roll");
  if (pRoll) pRoll.textContent = student.roll;
  const pBranch = document.getElementById("profile-modal-branch");
  if (pBranch) pBranch.textContent = `${student.dept} • Section ${student.section}`;
  const pToken = document.getElementById("profile-modal-token");
  if (pToken) pToken.textContent = `CV${student.id}2026`;
}

function logout() {
  currentUserRole = null;
  document.getElementById("view-student").classList.add("hidden");
  document.getElementById("view-admin").classList.add("hidden");
  document.getElementById("view-auth").classList.remove("hidden");

  const chip = document.getElementById("nav-user-chip");
  if (chip) chip.classList.add("hidden");

  showToast("Logged Out", "Session safely closed. Privacy preserved.", "info");
}

function handleLogoClick() {
  if (currentUserRole === "student") {
    // Already on student dashboard
  } else if (currentUserRole === "admin") {
    // Already on admin view
  } else {
    switchAuthTab("student");
  }
}

// -------------------------------------------------------------
// 5. Student Dashboard Metrics & Issue Table
// -------------------------------------------------------------
function refreshDashboard() {
  renderTokenBadge();
  renderDashboardStats();
  renderRecentIssuesTable();
  populateVerifyModalIssues();
  populateReverifyModalIssues();
  renderNoticeBoard();
}

function renderTokenBadge() {
  const badge = document.getElementById("badge-token-status");
  if (!badge) return;

  if (isTokenUsed) {
    badge.className = "text-xs font-bold px-2.5 py-0.5 rounded-full bg-rose-500/20 text-rose-300 border border-rose-500/30";
    badge.textContent = "USED TODAY";
  } else {
    badge.className = "text-xs font-bold px-2.5 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-500/30";
    badge.textContent = "ACTIVE";
  }
}

function renderDashboardStats() {
  let underVerification = 0;
  let classVerified = 0;
  let resolved = 0;
  let reopened = 0;

  issues.forEach(i => {
    const s = i.status.toUpperCase();
    if (s === "UNDER VERIFICATION") underVerification++;
    if (s === "CLASS VERIFIED") classVerified++;
    if (s === "RESOLVED") resolved++;
    if (s === "REOPENED") reopened++;
  });

  const elTotal = document.getElementById("stat-total-issues");
  const elVerif = document.getElementById("stat-under-verification");
  const elClass = document.getElementById("stat-class-verified");
  const elResol = document.getElementById("stat-resolved-issues");
  const elReope = document.getElementById("stat-reopened-issues");

  if (elTotal) elTotal.textContent = issues.length;
  if (elVerif) elVerif.textContent = underVerification;
  if (elClass) elClass.textContent = classVerified;
  if (elResol) elResol.textContent = resolved;
  if (elReope) elReope.textContent = reopened;
}

function renderRecentIssuesTable() {
  const tbody = document.getElementById("table-issues-body");
  if (!tbody) return;

  const searchInput = (document.getElementById("input-search-issues")?.value || "").toLowerCase().trim();
  const catFilter = document.getElementById("filter-table-cat")?.value || "ALL";

  let filtered = issues.filter(issue => {
    // Status Filter
    if (currentTableFilterStatus !== "ALL") {
      if (issue.status.toUpperCase() !== currentTableFilterStatus.toUpperCase()) {
        return false;
      }
    }

    // Category Filter
    if (catFilter !== "ALL" && issue.category !== catFilter) {
      return false;
    }

    // Search Query
    if (searchInput) {
      const match = issue.id.toLowerCase().includes(searchInput) ||
                    issue.title.toLowerCase().includes(searchInput) ||
                    issue.category.toLowerCase().includes(searchInput) ||
                    issue.description.toLowerCase().includes(searchInput);
      if (!match) return false;
    }
    return true;
  });

  const counter = document.getElementById("table-results-counter");
  if (counter) counter.textContent = `(${filtered.length} issues found)`;

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" class="py-10 text-center text-slate-500 font-medium">No matching issues match current search or filters.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(issue => {
    const totalVotes = issue.votes.frequently + issue.votes.sometimes + issue.votes.never;
    const partPct = Math.min(100, Math.round((totalVotes / ESTIMATED_CLASS_SIZE) * 100));

    return `
      <tr onclick="trackFromTable('${issue.id}')" class="hover:bg-slate-800/40 transition-colors cursor-pointer group">
        <td class="font-mono font-black text-blue-400 group-hover:text-blue-300 whitespace-nowrap">${issue.id}</td>
        <td>${renderPriorityBadgeHTML(issue.priority || 'MEDIUM')}</td>
        <td><span class="badge-cat">${getCategoryIconHTML(issue.category)} ${issue.category}</span></td>
        <td class="font-bold text-white max-w-xs truncate" title="${issue.title}">
          ${issue.title}
          <div class="text-[11px] font-normal text-slate-400 truncate">${issue.description}</div>
        </td>
        <td class="text-slate-400 whitespace-nowrap">${issue.date}</td>
        <td>
          <div class="w-24">
            <div class="flex justify-between text-[10px] text-slate-400 mb-0.5">
              <span>${partPct}%</span>
              <span>${totalVotes}/${ESTIMATED_CLASS_SIZE}</span>
            </div>
            <div class="w-full bg-slate-800 rounded-full h-1.5 overflow-hidden">
              <div class="bg-blue-500 h-1.5 rounded-full" style="width: ${partPct}%"></div>
            </div>
          </div>
        </td>
        <td>${renderStatusBadgeHTML(issue.status)}</td>
        <td class="text-right whitespace-nowrap">
          <button onclick="event.stopPropagation(); trackFromTable('${issue.id}')" class="px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-blue-600 text-slate-300 hover:text-white transition-all text-xs font-bold border border-slate-700">
            <i class="fa-solid fa-arrow-up-right-from-square mr-1"></i> Track
          </button>
        </td>
      </tr>
    `;
  }).join("");
}

function filterTableByStatus(status) {
  currentTableFilterStatus = status;
  renderRecentIssuesTable();
  const label = status === "ALL" ? "All Issues" : status;
  showToast("Filter Applied", `Showing ${label} issues in registry.`, "info");
}

function resetTableFilters() {
  currentTableFilterStatus = "ALL";
  const search = document.getElementById("input-search-issues");
  if (search) search.value = "";
  const cat = document.getElementById("filter-table-cat");
  if (cat) cat.value = "ALL";
  renderRecentIssuesTable();
  showToast("Filters Reset", "Showing full registry.", "info");
}

function handleTableSearch() {
  renderRecentIssuesTable();
}

function renderStatusBadgeHTML(status) {
  const cleanClass = status.replace(/\s+/g, '_').toUpperCase();
  return `<span class="badge-status ${cleanClass}">${status}</span>`;
}

function renderPriorityBadgeHTML(priority) {
  const p = (priority || "MEDIUM").toUpperCase();
  return `<span class="badge-priority ${p}">${p}</span>`;
}

function getCategoryIconHTML(cat) {
  const icons = {
    Classroom: '<i class="fa-solid fa-chalkboard-user text-blue-400"></i>',
    Laboratory: '<i class="fa-solid fa-microchip text-indigo-400"></i>',
    Faculty: '<i class="fa-solid fa-user-tie text-purple-400"></i>',
    Hostel: '<i class="fa-solid fa-bed text-amber-400"></i>',
    Canteen: '<i class="fa-solid fa-utensils text-orange-400"></i>',
    Administration: '<i class="fa-solid fa-building-columns text-cyan-400"></i>',
    Library: '<i class="fa-solid fa-book-open text-emerald-400"></i>',
    Transport: '<i class="fa-solid fa-bus text-yellow-400"></i>',
    Other: '<i class="fa-solid fa-cubes text-slate-400"></i>'
  };
  return icons[cat] || '<i class="fa-solid fa-tag text-slate-400"></i>';
}

// -------------------------------------------------------------
// 6. Anonymous Issue Reporting
// -------------------------------------------------------------
function updateCategoryHint() {
  const cat = document.getElementById("report-category").value;
  const hint = document.getElementById("report-category-hint");
  const hints = {
    Classroom: "Classroom: Report faulty projectors, damaged furniture, split AC cut-outs, or smart board errors.",
    Laboratory: "Laboratory: Report GPU kernel panics, software licensing, compiler crashes, or instrument issues.",
    Faculty: "Faculty & Academic: Confidential reporting on lecture coverage, lab viva alignment, or mentorship.",
    Hostel: "Hostel: Report electricity disruptions, RO drinking water issues, washroom repairs, or mess hygiene.",
    Canteen: "Canteen: Report digital UPI terminal scanner issues, hygiene standards, or pricing irregularities.",
    Administration: "Administration: Report fee counter delays, bonafide certificate queues, or scholarship desk.",
    Library: "Library: Report book reservations, reading hall AC, or campus Wi-Fi connectivity bottlenecks.",
    Transport: "Transport: Report bus route timing delays, overcrowding, or driver safety feedback.",
    Other: "Other: General campus infrastructure or facilities concerns."
  };
  hint.textContent = hints[cat] || "Select an issue category.";
}

function updateCharCount(el) {
  const len = el.value.length;
  document.getElementById("report-char-count").textContent = `${len} / 500 characters`;
}

let attachedFileName = "";
function handleFileSelect(input) {
  if (input.files && input.files[0]) {
    const file = input.files[0];
    attachedFileName = file.name;
    document.getElementById("report-file-name").textContent = `${file.name} (${Math.round(file.size / 1024)} KB)`;
    document.getElementById("btn-remove-file").classList.remove("hidden");
  }
}

function removeSelectedFile() {
  attachedFileName = "";
  const fileInput = document.getElementById("report-file");
  if (fileInput) fileInput.value = "";
  document.getElementById("report-file-name").textContent = "No file selected";
  document.getElementById("btn-remove-file").classList.add("hidden");
}

async function handleReportIssue(e) {
  e.preventDefault();
  const category = document.getElementById("report-category").value;
  const priority = document.getElementById("report-priority").value;
  const title = document.getElementById("report-title").value.trim();
  const desc = document.getElementById("report-desc").value.trim();

  if (!title || !desc) {
    showToast("Mandatory Fields", "Please supply both title and detailed description.", "warning");
    return;
  }

  let createdIssue = null;

  if (isBackendConnected) {
    const res = await api.post('/issues', {
      category,
      priority,
      title,
      description: desc,
      evidence: attachedFileName
    });
    if (res && res.success && res.issue) {
      createdIssue = res.issue;
    }
  }

  if (!createdIssue) {
    // Generate unique CV-XXXX ID fallback
    let newId;
    do {
      const rand = Math.floor(1000 + Math.random() * 9000);
      newId = `CV-${rand}`;
    } while (issues.some(i => i.id === newId));

    const today = new Date().toISOString().split("T")[0];

    const deptMap = {
      Classroom: "Electrical & Maintenance",
      Laboratory: "IT Cell & Laboratories",
      Faculty: "Academic Dean Office",
      Hostel: "Hostel Administration",
      Canteen: "Canteen Management Board",
      Administration: "Proctorial Board",
      Library: "Academic Dean Office",
      Transport: "Proctorial Board",
      Other: "Electrical & Maintenance"
    };

    createdIssue = {
      id: newId,
      category,
      priority,
      assignedDept: deptMap[category] || "General Administration",
      title,
      description: desc,
      evidence: attachedFileName,
      date: today,
      status: "REPORTED",
      adminRemarks: "New issue filed anonymously. Pending initial review or class voting.",
      votes: { frequently: 0, sometimes: 0, never: 0 },
      reverify: { resolved: 0, partially: 0, stillFacing: 0 }
    };
  }

  if (!issues.some(i => i.id === createdIssue.id)) {
    issues.unshift(createdIssue);
  }
  logAuditEvent(createdIssue.id, "ISSUE_REPORTED", "ANONYMOUS_STUDENT", `Filed anonymous ${category} grievance (${priority} priority).`);
  saveState();

  closeModal("modal-report");

  // Reset form
  document.getElementById("report-title").value = "";
  document.getElementById("report-desc").value = "";
  removeSelectedFile();
  document.getElementById("report-char-count").textContent = "0 / 500 characters";

  refreshDashboard();
  const src = isBackendConnected ? "Central Server Database" : "Local Storage";
  showToast("Issue Reported Anonymously!", `Generated ID: ${createdIssue.id} (${src}). Save this ID to track resolution.`, "success");

  setTimeout(() => {
    quickTrack(createdIssue.id);
  }, 400);
}

// -------------------------------------------------------------
// 7. Democratic Class-Level Verification Voting
// -------------------------------------------------------------
function pasteDailyToken() {
  const token = currentStudent ? `CV${currentStudent.id}2026` : DEFAULT_DEMO_TOKEN;
  document.getElementById("verify-token-input").value = token;
  showToast("Token Pasted", `Active daily token ${token} inserted.`, "info");
}

function populateVerifyModalIssues() {
  const select = document.getElementById("verify-issue-select");
  if (!select) return;
  select.innerHTML = issues.map(i => `<option value="${i.id}">[${i.id}] (${i.status}) ${i.title}</option>`).join("");
}

function selectVoteCard(option) {
  selectedVoteOption = option;
  ["Frequently", "Sometimes", "Never"].forEach(opt => {
    const card = document.getElementById(`vote-card-${opt}`);
    if (!card) return;
    card.classList.remove("selected-freq", "selected-some", "selected-never");
    if (opt === option) {
      if (opt === "Frequently") card.classList.add("selected-freq");
      if (opt === "Sometimes") card.classList.add("selected-some");
      if (opt === "Never") card.classList.add("selected-never");
    }
  });
}

function updateVerifyModalStats() {
  const select = document.getElementById("verify-issue-select");
  if (!select) return;
  const issueId = select.value;
  const issue = issues.find(i => i.id === issueId);
  if (!issue) return;

  const total = issue.votes.frequently + issue.votes.sometimes + issue.votes.never;
  const freqPct = total === 0 ? 0 : Math.round((issue.votes.frequently / total) * 100);
  const somePct = total === 0 ? 0 : Math.round((issue.votes.sometimes / total) * 100);
  const neverPct = total === 0 ? 0 : Math.round((issue.votes.never / total) * 100);
  const partPct = Math.min(100, Math.round((total / ESTIMATED_CLASS_SIZE) * 100));

  document.getElementById("verify-freq-val").textContent = `${freqPct}% (${issue.votes.frequently} votes)`;
  document.getElementById("verify-freq-bar").style.width = `${freqPct}%`;

  document.getElementById("verify-some-val").textContent = `${somePct}% (${issue.votes.sometimes} votes)`;
  document.getElementById("verify-some-bar").style.width = `${somePct}%`;

  document.getElementById("verify-never-val").textContent = `${neverPct}% (${issue.votes.never} votes)`;
  document.getElementById("verify-never-bar").style.width = `${neverPct}%`;

  document.getElementById("verify-part-val").textContent = `${partPct}% (${total}/${ESTIMATED_CLASS_SIZE} cohort participation)`;
  document.getElementById("verify-part-bar").style.width = `${partPct}%`;

  // Outcome Badge
  const outcomeBadge = document.getElementById("verify-outcome-badge");
  const isClassVerified = partPct >= 30 && (freqPct + somePct) >= 60;

  if (isClassVerified) {
    outcomeBadge.className = "badge-status CLASS_VERIFIED";
    outcomeBadge.textContent = "CLASS VERIFIED (QUORUM MET)";
  } else {
    outcomeBadge.className = "badge-status UNDER_VERIFICATION";
    outcomeBadge.textContent = "UNDER VERIFICATION";
  }
}

async function handleVoteSubmit() {
  const enteredToken = document.getElementById("verify-token-input").value.trim().toUpperCase();
  const validToken = currentStudent ? `CV${currentStudent.id}2026` : DEFAULT_DEMO_TOKEN;

  if (!enteredToken || enteredToken !== validToken) {
    showToast("Invalid Token", `Please enter your valid daily token: ${validToken}`, "error");
    return;
  }

  if (isTokenUsed) {
    showToast("Token Already Used", "This token has already cast a vote today. Single-use daily limit reached.", "warning");
    return;
  }

  if (!selectedVoteOption) {
    showToast("Selection Needed", "Please choose Frequently, Sometimes, or Never.", "warning");
    return;
  }

  const issueId = document.getElementById("verify-issue-select").value;
  const issue = issues.find(i => i.id === issueId);
  if (!issue) return;

  if (isBackendConnected) {
    const res = await api.post('/issues/vote', {
      issueId,
      voteOption: selectedVoteOption,
      token: enteredToken,
      studentId: currentStudent ? currentStudent.id : null
    });
    if (res && res.success && res.issue) {
      Object.assign(issue, res.issue);
      isTokenUsed = true;
      saveState();
      updateVerifyModalStats();
      refreshDashboard();
      showToast("Vote Processed via Backend!", `Anonymous vote (${selectedVoteOption}) confirmed. Algorithmic Quorum: ${res.stats?.participationRate || 0}%.`, "success");
      selectedVoteOption = null;
      ["Frequently", "Sometimes", "Never"].forEach(opt => {
        const card = document.getElementById(`vote-card-${opt}`);
        if (card) card.classList.remove("selected-freq", "selected-some", "selected-never");
      });
      return;
    }
  }

  // Local fallback
  if (selectedVoteOption === "Frequently") issue.votes.frequently++;
  if (selectedVoteOption === "Sometimes") issue.votes.sometimes++;
  if (selectedVoteOption === "Never") issue.votes.never++;

  const total = issue.votes.frequently + issue.votes.sometimes + issue.votes.never;
  const partPct = Math.round((total / ESTIMATED_CLASS_SIZE) * 100);
  const confirmPct = Math.round(((issue.votes.frequently + issue.votes.sometimes) / total) * 100);

  if (partPct >= 30 && confirmPct >= 60) {
    if (issue.status !== "CLASS VERIFIED") {
      issue.status = "CLASS VERIFIED";
      logAuditEvent(issue.id, "STATUS_PROMOTED", "VERIFICATION_ENGINE", `Class Quorum achieved (${partPct}% turnout, ${confirmPct}% confirmation). Promoted to CLASS VERIFIED.`);
    }
  } else if (total > 0 && issue.status === "REPORTED") {
    issue.status = "UNDER VERIFICATION";
    logAuditEvent(issue.id, "STATUS_CHANGE", "VERIFICATION_ENGINE", "Voting active. State transitioned to UNDER VERIFICATION.");
  }

  isTokenUsed = true;
  saveState();

  updateVerifyModalStats();
  refreshDashboard();

  showToast("Vote Confirmed!", `Anonymous vote (${selectedVoteOption}) cast for ${issue.id}. Daily token consumed.`, "success");

  selectedVoteOption = null;
  ["Frequently", "Sometimes", "Never"].forEach(opt => {
    const card = document.getElementById(`vote-card-${opt}`);
    if (card) card.classList.remove("selected-freq", "selected-some", "selected-never");
  });
}

function resetToken() {
  isTokenUsed = false;
  saveState();
  renderTokenBadge();
  showToast("Token Restored", "Daily token reset to ACTIVE.", "info");
}

function copyToken() {
  const token = currentStudent ? `CV${currentStudent.id}2026` : DEFAULT_DEMO_TOKEN;
  navigator.clipboard.writeText(token);
  showToast("Copied to Clipboard", `Token code ${token} ready to paste.`, "info");
}

// -------------------------------------------------------------
// 8. Public Issue Tracking & Milestone Stepper
// -------------------------------------------------------------
function setAndTrackSample(id) {
  document.getElementById("public-track-input").value = id;
  handlePublicTrackSubmit();
}

function handlePublicTrackSubmit() {
  const id = document.getElementById("public-track-input").value.trim().toUpperCase();
  if (!id) {
    showToast("Input Required", "Please enter an Issue ID (e.g. CV-1847)", "warning");
    return;
  }
  quickTrack(id);
}

function handleHeaderQuickTrack() {
  const input = document.getElementById("header-quick-track-input");
  const query = (input?.value || "").trim().toUpperCase();
  if (!query) return;
  quickTrack(query);
  input.value = "";
}

function quickTrack(id) {
  document.getElementById("track-id-input").value = id;
  openModal("modal-track");
  performTrackLookup();
}

function trackFromTable(issueId) {
  quickTrack(issueId);
}

function performTrackLookup() {
  const query = document.getElementById("track-id-input").value.trim().toUpperCase();
  const issue = issues.find(i => i.id.toUpperCase() === query);

  if (!issue) {
    showToast("Issue Not Found", `No issue found for ID "${query}". Check sample pills.`, "error");
    return;
  }

  document.getElementById("track-issue-id").textContent = issue.id;
  document.getElementById("track-category").textContent = issue.category;
  document.getElementById("track-date").textContent = issue.date;
  document.getElementById("track-title").textContent = issue.title;
  document.getElementById("track-desc").textContent = issue.description;
  document.getElementById("track-assigned-dept").textContent = issue.assignedDept || "Not Assigned";

  const prioEl = document.getElementById("track-priority");
  prioEl.className = `badge-priority ${issue.priority || 'MEDIUM'}`;
  prioEl.textContent = issue.priority || 'MEDIUM';

  const statusBadge = document.getElementById("track-status-badge");
  statusBadge.className = `badge-status ${issue.status.replace(/\s+/g, '_')}`;
  statusBadge.textContent = issue.status;

  const remarksBox = document.getElementById("track-remarks-box");
  const remarksText = document.getElementById("track-remarks-text");
  if (issue.adminRemarks) {
    remarksBox.classList.remove("hidden");
    remarksText.textContent = issue.adminRemarks;
  } else {
    remarksBox.classList.add("hidden");
  }

  const total = issue.votes.frequently + issue.votes.sometimes + issue.votes.never;
  const freqPct = total === 0 ? 0 : Math.round((issue.votes.frequently / total) * 100);
  const partPct = Math.min(100, Math.round((total / ESTIMATED_CLASS_SIZE) * 100));

  document.getElementById("track-votes-summary").textContent = `Turnout: ${partPct}% (${total}/${ESTIMATED_CLASS_SIZE}) | Confirmation: ${freqPct}%`;
  document.getElementById("track-votes-bar").style.width = `${Math.max(partPct, freqPct)}%`;

  updateMilestoneStepper(issue.status);
}

function updateMilestoneStepper(status) {
  const s = status.toUpperCase();

  const nodes = [
    { id: "step-1", name: "1. Reported", icon: "fa-check", active: true },
    { id: "step-2", name: "2. Verifying", icon: "fa-hourglass", active: s !== "REPORTED" },
    { id: "step-3", name: "3. Verified", icon: "fa-circle-check", active: s.includes("VERIFIED") || s.includes("INVESTIGATION") || s.includes("RESOLVED") },
    { id: "step-4", name: "4. Assigned", icon: "fa-wrench", active: s.includes("INVESTIGATION") || s.includes("RESOLVED") },
    { id: "step-5", name: "5. Resolved", icon: "fa-flag-checkered", active: s.includes("RESOLVED") }
  ];

  nodes.forEach(node => {
    const el = document.getElementById(node.id);
    if (!el) return;
    if (node.active) {
      el.className = "step-node active";
      el.innerHTML = `<i class="fa-solid ${node.icon}"></i> ${node.name}`;
    } else {
      el.className = "step-node pending";
      el.innerHTML = `<i class="fa-regular fa-circle"></i> ${node.name}`;
    }
  });

  if (s === "REOPENED") {
    const s5 = document.getElementById("step-5");
    if (s5) {
      s5.className = "step-node active !bg-rose-500/20 !border-rose-500 !text-rose-400";
      s5.innerHTML = `<i class="fa-solid fa-triangle-exclamation"></i> 5. Reopened`;
    }
  }
}

function copyTrackedId() {
  const id = document.getElementById("track-issue-id").textContent;
  navigator.clipboard.writeText(id);
  showToast("ID Copied", `Issue ID ${id} copied to clipboard.`, "info");
}

function copyTrackingShareLink() {
  const id = document.getElementById("track-issue-id").textContent;
  const url = `${window.location.origin}${window.location.pathname}?track=${id}`;
  navigator.clipboard.writeText(url);
  showToast("Link Copied", `Shareable tracking URL copied for ${id}.`, "success");
}

// -------------------------------------------------------------
// 9. Post-Resolution Re-Verification Loop
// -------------------------------------------------------------
function populateReverifyModalIssues() {
  const select = document.getElementById("reverify-issue-select");
  if (!select) return;
  select.innerHTML = issues.map(i => `<option value="${i.id}">[${i.id}] (${i.status}) ${i.title}</option>`).join("");
}

function selectReverifyOption(opt) {
  selectedReverifyOption = opt;
  ["Resolved", "Partially", "Still"].forEach(k => {
    const card = document.getElementById(`reverify-card-${k}`);
    if (!card) return;
    card.classList.remove("selected-resolved", "selected-partial", "selected-still");
    if ((k === "Resolved" && opt === "Resolved")) card.classList.add("selected-resolved");
    if ((k === "Partially" && opt === "Partially Resolved")) card.classList.add("selected-partial");
    if ((k === "Still" && opt === "Still Facing Problem")) card.classList.add("selected-still");
  });
}

async function handleReverifySubmit() {
  if (!selectedReverifyOption) {
    showToast("Feedback Required", "Please choose whether the issue is resolved, partially solved, or still persistent.", "warning");
    return;
  }

  const issueId = document.getElementById("reverify-issue-select").value;
  const issue = issues.find(i => i.id === issueId);
  if (!issue) return;

  if (isBackendConnected) {
    const res = await api.post('/issues/reverify', {
      issueId,
      response: selectedReverifyOption
    });
    if (res && res.success && res.issue) {
      Object.assign(issue, res.issue);
      saveState();
      closeModal("modal-reverify");
      refreshDashboard();
      showToast("Feedback Synced to Server!", res.message, selectedReverifyOption === "Still Facing Problem" ? "error" : "success");
      selectedReverifyOption = null;
      ["Resolved", "Partially", "Still"].forEach(k => {
        const card = document.getElementById(`reverify-card-${k}`);
        if (card) card.classList.remove("selected-resolved", "selected-partial", "selected-still");
      });
      return;
    }
  }

  // Local fallback
  if (selectedReverifyOption === "Resolved") {
    issue.reverify.resolved++;
    logAuditEvent(issueId, "REVERIFY_CONFIRMED", "STUDENT", "Student verified satisfactory resolution.");
    showToast("Resolution Verified", `Positive confirmation recorded for ${issueId}.`, "success");
  } else if (selectedReverifyOption === "Partially Resolved") {
    issue.reverify.partially++;
    logAuditEvent(issueId, "REVERIFY_PARTIAL", "STUDENT", "Student reported partial resolution.");
    showToast("Feedback Logged", `Partial resolution noted for ${issueId}.`, "info");
  } else if (selectedReverifyOption === "Still Facing Problem") {
    issue.reverify.stillFacing++;
    // Auto-Reopen algorithm triggered
    issue.status = "REOPENED";
    issue.priority = "CRITICAL";
    issue.adminRemarks = "AUTO-REOPENED: Recurring student complaints detected following management fix.";
    logAuditEvent(issueId, "AUTO_REOPEN_TRIGGER", "REVERIFICATION_ENGINE", "Auto-reopen triggered due to ongoing dissatisfaction report. Escalated to CRITICAL priority.");
    showToast("Issue Auto-Reopened!", `Recurring problem flagged! ${issueId} escalated to REOPENED with CRITICAL priority.`, "error");
  }

  saveState();
  closeModal("modal-reverify");
  refreshDashboard();

  selectedReverifyOption = null;
  ["Resolved", "Partially", "Still"].forEach(k => {
    const card = document.getElementById(`reverify-card-${k}`);
    if (card) card.classList.remove("selected-resolved", "selected-partial", "selected-still");
  });
}

// -------------------------------------------------------------
// 10. Administrator Dashboard & Command Center
// -------------------------------------------------------------
function renderAdminDashboard() {
  let reported = 0, verifying = 0, verified = 0, investigating = 0, resolved = 0, reopened = 0;

  issues.forEach(i => {
    const s = i.status.toUpperCase();
    if (s === "REPORTED") reported++;
    if (s === "UNDER VERIFICATION") verifying++;
    if (s === "CLASS VERIFIED") verified++;
    if (s === "UNDER INVESTIGATION") investigating++;
    if (s === "RESOLVED") resolved++;
    if (s === "REOPENED") reopened++;
  });

  document.getElementById("admin-stat-total").textContent = issues.length;
  document.getElementById("admin-stat-reported").textContent = reported;
  document.getElementById("admin-stat-verifying").textContent = verifying;
  document.getElementById("admin-stat-verified").textContent = verified;
  document.getElementById("admin-stat-investigating").textContent = investigating;
  document.getElementById("admin-stat-resolved").textContent = resolved;
  document.getElementById("admin-stat-reopened").textContent = reopened;

  updateAdminRoleUI();
  renderAdminTable();
  renderAdminTeamTable();
}

function updateAdminRoleUI() {
  const master = isMasterAdmin();
  const badgeEl = document.getElementById("admin-role-badge");
  const subtextEl = document.getElementById("admin-header-subtext");
  const btnTopAdd = document.getElementById("btn-admin-add-top");
  const btnRosterAdd = document.getElementById("btn-admin-add-roster");
  const rosterNotice = document.getElementById("admin-roster-lock-notice");

  if (badgeEl) {
    if (master) {
      badgeEl.className = "text-xs px-2.5 py-0.5 rounded-full bg-amber-500/20 text-amber-300 font-bold border border-amber-500/30 flex items-center gap-1.5";
      badgeEl.innerHTML = `<i class="fa-solid fa-crown text-amber-400 text-[10px]"></i> MASTER ADMIN`;
    } else {
      badgeEl.className = "text-xs px-2.5 py-0.5 rounded-full bg-blue-500/20 text-blue-300 font-bold border border-blue-500/30 flex items-center gap-1.5";
      badgeEl.innerHTML = `<i class="fa-solid fa-user-shield text-blue-400 text-[10px]"></i> ${(currentAdmin.role || 'AUTHORIZED ADMIN').toUpperCase()}`;
    }
  }

  if (subtextEl) {
    if (master) {
      subtextEl.textContent = "Master Administrative Authority: Full control over staff privileges, grievance review, gateways, and institutional resolutions.";
    } else {
      subtextEl.textContent = `Department Review: Authority over complaints assigned to ${currentAdmin.department || 'designated department'}.`;
    }
  }

  // "+ Add Admin" in Top Bar (Master Admin Only)
  if (btnTopAdd) {
    if (master) {
      btnTopAdd.classList.remove("hidden");
    } else {
      btnTopAdd.classList.add("hidden");
    }
  }

  // "+ Add New Administrator" in Roster Card (Master Admin Only)
  if (btnRosterAdd) {
    if (master) {
      btnRosterAdd.classList.remove("hidden");
    } else {
      btnRosterAdd.classList.add("hidden");
    }
  }

  // Lock notice in Roster Card for non-master admins
  if (rosterNotice) {
    if (master) {
      rosterNotice.classList.add("hidden");
    } else {
      rosterNotice.classList.remove("hidden");
    }
  }
}

function renderAdminTable() {
  const tbody = document.getElementById("table-admin-body");
  if (!tbody) return;

  const catFilter = document.getElementById("admin-filter-cat").value;
  const statusFilter = document.getElementById("admin-filter-status").value;
  const searchInput = (document.getElementById("admin-search-input")?.value || "").toLowerCase().trim();

  const filtered = issues.filter(i => {
    if (catFilter !== "ALL" && i.category !== catFilter) return false;
    if (statusFilter !== "ALL" && i.status.toUpperCase() !== statusFilter.toUpperCase()) return false;
    if (searchInput) {
      const match = i.id.toLowerCase().includes(searchInput) ||
                    i.title.toLowerCase().includes(searchInput) ||
                    i.category.toLowerCase().includes(searchInput) ||
                    (i.assignedDept && i.assignedDept.toLowerCase().includes(searchInput));
      if (!match) return false;
    }
    return true;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" class="py-10 text-center text-slate-500">No issues match current admin filters.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(issue => {
    const isSelected = issue.id === currentAdminSelectedId;
    return `
      <tr onclick="selectAdminRow('${issue.id}')" class="transition-colors cursor-pointer ${isSelected ? 'bg-amber-950/40 border-l-4 border-amber-500' : 'hover:bg-slate-800/40'}">
        <td class="font-mono font-black text-amber-400 whitespace-nowrap">${issue.id}</td>
        <td><span class="badge-cat">${issue.category}</span></td>
        <td>${renderPriorityBadgeHTML(issue.priority)}</td>
        <td class="font-bold text-white max-w-xs truncate" title="${issue.title}">
          ${issue.title}
          <div class="text-[11px] font-normal text-slate-400 truncate">${issue.adminRemarks || 'No remarks recorded.'}</div>
        </td>
        <td class="text-slate-400 whitespace-nowrap">${issue.date}</td>
        <td class="text-slate-300 font-semibold whitespace-nowrap">${issue.assignedDept || 'Unassigned'}</td>
        <td>${renderStatusBadgeHTML(issue.status)}</td>
        <td class="text-right whitespace-nowrap">
          <input type="radio" name="admin-selected-radio" ${isSelected ? 'checked' : ''} class="accent-amber-500 w-4 h-4 cursor-pointer">
        </td>
      </tr>
    `;
  }).join("");
}

function selectAdminRow(issueId) {
  currentAdminSelectedId = issueId;
  const issue = issues.find(i => i.id === issueId);
  if (!issue) return;

  document.getElementById("admin-selected-title").textContent = `[${issue.id}] ${issue.title} (${issue.status})`;
  document.getElementById("btn-admin-investigate").disabled = false;
  document.getElementById("btn-admin-resolve").disabled = false;
  document.getElementById("btn-admin-reopen").disabled = false;
  document.getElementById("btn-admin-save-remarks").disabled = false;

  if (issue.assignedDept) {
    document.getElementById("admin-assign-dept").value = issue.assignedDept;
  }
  document.getElementById("admin-remarks-input").value = issue.adminRemarks || "";

  renderAdminTable();
}

async function adminApplyStatus(newStatus) {
  if (!currentAdminSelectedId) return;
  const issue = issues.find(i => i.id === currentAdminSelectedId);
  if (!issue) return;

  const oldStatus = issue.status;
  issue.status = newStatus;

  if (isBackendConnected) {
    await api.post('/admin/issues/update', {
      issueId: issue.id,
      status: newStatus,
      adminName: currentAdmin ? currentAdmin.name : 'ADMINISTRATOR'
    });
  }

  saveState();

  logAuditEvent(issue.id, "STATUS_TRANSITION", "ADMIN (DEPT_HEAD)", `Status changed from ${oldStatus} to ${newStatus}.`);

  renderAdminDashboard();
  selectAdminRow(issue.id);

  const src = isBackendConnected ? "Live Backend" : "Local State";
  showToast("Status Updated", `Issue ${issue.id} transitioned to ${newStatus} (${src}).`, "success");
}

async function adminSaveRemarks() {
  if (!currentAdminSelectedId) return;
  const issue = issues.find(i => i.id === currentAdminSelectedId);
  if (!issue) return;

  const dept = document.getElementById("admin-assign-dept").value;
  const remarks = document.getElementById("admin-remarks-input").value.trim();

  issue.assignedDept = dept;
  issue.adminRemarks = remarks;

  if (isBackendConnected) {
    await api.post('/admin/issues/update', {
      issueId: issue.id,
      department: dept,
      remarks: remarks,
      adminName: currentAdmin ? currentAdmin.name : 'ADMINISTRATOR'
    });
  }

  saveState();

  logAuditEvent(issue.id, "DEPARTMENT_ASSIGNED", "ADMIN (DEPT_HEAD)", `Assigned to ${dept}. Notes: "${remarks}".`);

  renderAdminDashboard();
  const src = isBackendConnected ? "Database Synced" : "Local Saved";
  showToast("Department & Remarks Saved", `Updated details for ${issue.id} (${src}).`, "success");
}

function exportIssuesCSV() {
  const headers = ["Issue ID", "Category", "Priority", "Title", "Date", "Status", "Assigned Dept", "Admin Remarks"];
  const rows = issues.map(i => [
    i.id,
    `"${i.category}"`,
    i.priority || "MEDIUM",
    `"${i.title.replace(/"/g, '""')}"`,
    i.date,
    i.status,
    `"${(i.assignedDept || "").replace(/"/g, '""')}"`,
    `"${(i.adminRemarks || "").replace(/"/g, '""')}"`
  ]);

  const csvContent = "data:text/csv;charset=utf-8," + [headers.join(","), ...rows.map(e => e.join(","))].join("\n");
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", `campusvoice_registry_${new Date().toISOString().split("T")[0]}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  showToast("CSV Exported", "Downloaded full issue registry for administrative archiving.", "success");
}

// -------------------------------------------------------------
// 10B. Administrator Authority & Team Management
// -------------------------------------------------------------
function renderAdminTeamTable() {
  const tbody = document.getElementById("table-admins-body");
  const countBadge = document.getElementById("admin-count-badge");
  if (countBadge) {
    countBadge.textContent = `${admins.length} Authorized Admin${admins.length === 1 ? '' : 's'}`;
  }
  if (!tbody) return;

  if (!admins || admins.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center py-6 text-slate-400">No administrators provisioned.</td></tr>`;
    return;
  }

  tbody.innerHTML = admins.map(adm => {
    const isSelf = currentAdmin && currentAdmin.username.toLowerCase() === adm.username.toLowerCase();
    const isPrimary = adm.isPrimary || adm.username.toLowerCase() === "admin";
    const initials = (adm.name || "Admin").split(" ").map(w => w[0]).join("").slice(0, 2).toUpperCase();

    const badgeHtml = isPrimary
      ? `<span class="px-2.5 py-0.5 rounded-full text-[10px] font-black bg-amber-500/20 text-amber-300 border border-amber-500/30 uppercase tracking-wider">Super Admin</span>`
      : `<span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-blue-500/20 text-blue-300 border border-blue-500/30 uppercase tracking-wider">Authorized Admin</span>`;

    let actionHtml = "";
    if (isPrimary) {
      actionHtml = `<span class="text-[11px] text-slate-400 font-semibold italic">Protected</span>`;
    } else if (isSelf) {
      actionHtml = `<span class="text-[11px] text-emerald-400 font-bold"><i class="fa-solid fa-circle-check mr-1"></i>Current User</span>`;
    } else if (isMasterAdmin()) {
      actionHtml = `
        <button type="button" onclick="handleDeleteAdmin('${adm.username}')" class="px-2.5 py-1 rounded-lg text-xs font-bold bg-rose-500/10 hover:bg-rose-500/20 border border-rose-500/30 text-rose-400 hover:text-rose-300 transition-all flex items-center gap-1 ml-auto">
          <i class="fa-solid fa-trash text-[10px]"></i> Revoke
        </button>
      `;
    } else {
      actionHtml = `<span class="text-[11px] text-slate-500 font-semibold italic flex items-center justify-end gap-1"><i class="fa-solid fa-lock text-[9px]"></i> Restricted</span>`;
    }

    return `
      <tr class="hover:bg-slate-800/40 transition-colors">
        <td>
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-xl bg-gradient-to-tr from-amber-600 to-orange-500 text-white font-extrabold text-xs flex items-center justify-center shadow-sm">
              ${initials}
            </div>
            <div>
              <div class="font-bold text-white">${adm.name}</div>
              <div class="text-[11px] text-slate-400">${adm.email}</div>
            </div>
          </div>
        </td>
        <td class="font-mono text-xs font-bold text-amber-400">${adm.username}</td>
        <td class="text-xs text-slate-300 font-medium">${adm.department}</td>
        <td class="text-xs text-slate-200 font-semibold">${adm.role}</td>
        <td class="text-xs text-slate-400 whitespace-nowrap">${adm.dateAdded || '2026-10-01'}</td>
        <td>${badgeHtml}</td>
        <td class="text-right whitespace-nowrap">${actionHtml}</td>
      </tr>
    `;
  }).join("");
}

async function handleCreateAdmin(e) {
  if (e && e.preventDefault) e.preventDefault();
  if (!isMasterAdmin()) {
    showToast("Access Restricted", "Security Policy: Only the Master Administrator can add new administrators.", "error");
    closeModal("modal-add-admin");
    return;
  }
  const name = document.getElementById("add-admin-name").value.trim();
  const username = document.getElementById("add-admin-username").value.trim().toLowerCase();
  const email = document.getElementById("add-admin-email").value.trim();
  const dept = document.getElementById("add-admin-dept").value;
  const role = document.getElementById("add-admin-role").value;
  const pass = document.getElementById("add-admin-password").value.trim();
  const conf = document.getElementById("add-admin-confirm-password").value.trim();

  if (!name || !username || !email || !pass || !conf) {
    showToast("Missing Information", "Please fill in all mandatory fields.", "warning");
    return;
  }

  if (!/^[a-z0-9_.]+$/.test(username)) {
    showToast("Invalid Username", "Username can only contain lowercase letters, numbers, underscores, and dots.", "warning");
    return;
  }

  if (admins.some(a => a.username.toLowerCase() === username)) {
    showToast("Username Exists", `Admin username "${username}" is already assigned. Please choose another.`, "error");
    return;
  }

  if (pass.length < 4) {
    showToast("Weak Password", "Password must be at least 4 characters.", "warning");
    return;
  }

  if (pass !== conf) {
    showToast("Password Mismatch", "Initial Password and Confirm Password do not match.", "error");
    return;
  }

  const newAdmin = {
    username: username,
    password: pass,
    name: name,
    email: email,
    department: dept,
    role: role,
    dateAdded: new Date().toISOString().split("T")[0],
    isPrimary: false
  };

  if (isBackendConnected) {
    const res = await api.post('/admin/admins', newAdmin);
    if (res && !res.success) {
      showToast("Error", res.message || "Failed to persist administrator to backend.", "error");
      return;
    }
  }

  admins.push(newAdmin);
  saveState();

  logAuditEvent(
    `ADMIN-${username}`,
    "PROVISION_ADMIN",
    currentAdmin ? currentAdmin.name : "ADMIN",
    `New administrator account "${username}" (${name}) provisioned for department ${dept}.`
  );

  renderAdminTeamTable();
  closeModal("modal-add-admin");

  // Reset form fields
  document.getElementById("add-admin-name").value = "";
  document.getElementById("add-admin-username").value = "";
  document.getElementById("add-admin-email").value = "";
  document.getElementById("add-admin-password").value = "";
  document.getElementById("add-admin-confirm-password").value = "";

  const src = isBackendConnected ? "Central Server Roster" : "Local Registry";
  showToast("Administrator Created", `Successfully provisioned admin account in ${src} for ${name} (${username})!`, "success");
}

function handleDeleteAdmin(username) {
  if (!username) return;
  if (!isMasterAdmin()) {
    showToast("Access Restricted", "Security Policy: Only the Master Administrator can revoke administrator privileges.", "error");
    return;
  }
  if (username.toLowerCase() === "admin") {
    showToast("Action Prohibited", "The master primary administrator account cannot be deleted.", "error");
    return;
  }

  if (currentAdmin && currentAdmin.username.toLowerCase() === username.toLowerCase()) {
    showToast("Action Prohibited", "You cannot delete your own active administrator account.", "error");
    return;
  }

  if (!confirm(`Are you sure you want to revoke administrative privileges for account "${username}"?`)) {
    return;
  }

  const targetIndex = admins.findIndex(a => a.username.toLowerCase() === username.toLowerCase());
  if (targetIndex === -1) {
    showToast("Not Found", "Administrator account could not be found.", "error");
    return;
  }

  const removed = admins.splice(targetIndex, 1)[0];
  saveState();

  logAuditEvent(
    `ADMIN-${username}`,
    "REVOKE_ADMIN",
    currentAdmin ? currentAdmin.name : "ADMIN",
    `Administrative credentials for "${username}" (${removed.name}) were revoked.`
  );

  renderAdminTeamTable();
  showToast("Privileges Revoked", `Administrator "${username}" has been removed.`, "info");
}

// -------------------------------------------------------------
// 11. Full System Audit Log & Notice Board
// -------------------------------------------------------------
function renderAuditTrailTable() {
  const tbody = document.getElementById("table-audit-body");
  if (!tbody) return;

  tbody.innerHTML = auditLogs.map(log => `
    <tr>
      <td class="font-mono text-slate-400 whitespace-nowrap">${log.timestamp}</td>
      <td class="font-mono font-bold text-blue-400">${log.issueId}</td>
      <td><span class="px-2 py-0.5 rounded bg-slate-800 text-slate-200 font-mono text-[10px] font-bold">${log.action}</span></td>
      <td class="text-slate-300 font-semibold">${log.actor}</td>
      <td class="text-slate-300">${log.remarks}</td>
    </tr>
  `).join("");
}

function renderNoticeBoard() {
  const container = document.getElementById("notice-board-container");
  if (!container) return;

  container.innerHTML = CAMPUS_NOTICES.map(notice => `
    <div class="p-4 rounded-2xl bg-slate-900 border border-slate-700/80 space-y-1.5">
      <div class="flex items-center justify-between text-xs">
        <span class="font-bold text-blue-400">${notice.dept}</span>
        <span class="text-slate-500 font-mono">${notice.date}</span>
      </div>
      <h5 class="text-sm font-bold text-white">${notice.title}</h5>
      <p class="text-xs text-slate-300">${notice.body}</p>
    </div>
  `).join("");
}

// -------------------------------------------------------------
// 12. Password Recovery & Robust OTP Service
// -------------------------------------------------------------
function openForgotPasswordModal(suggestedRole = 'student') {
  activeResetSession = null;
  document.getElementById("forgot-step-1").classList.remove("hidden");
  document.getElementById("forgot-step-2").classList.add("hidden");

  const input = document.getElementById("forgot-account-input");
  if (input) {
    if (suggestedRole === 'admin') {
      input.value = "admin";
    } else {
      const studentVal = document.getElementById("login-student-id")?.value.trim();
      input.value = studentVal || "183";
    }
  }

  openModal("modal-forgot-password");
}

function handleForgotBackToStep1() {
  document.getElementById("forgot-step-1").classList.remove("hidden");
  document.getElementById("forgot-step-2").classList.add("hidden");
}

function handleSendResetOtp() {
  const input = document.getElementById("forgot-account-input").value.trim();

  if (!input) {
    showToast("Input Required", "Please enter your Student ID (e.g. 183), Roll Number, or Admin Username (admin).", "warning");
    return;
  }

  // 1. Resolve Account
  let resolvedAccount = null;
  let role = null;

  const foundAdmin = admins.find(a => 
    a.username.toLowerCase() === input.toLowerCase() ||
    (a.email && a.email.toLowerCase() === input.toLowerCase())
  );

  if (foundAdmin) {
    resolvedAccount = {
      id: foundAdmin.username,
      name: foundAdmin.name,
      email: foundAdmin.email || "admin@aryacollege.in",
      role: "admin",
      adminObj: foundAdmin
    };
    role = "admin";
  } else {
    // Search students
    const student = students.find(s => 
      s.id.toLowerCase() === input.toLowerCase() ||
      s.roll.toLowerCase() === input.toLowerCase() ||
      (s.email && s.email.toLowerCase() === input.toLowerCase())
    );
    if (student) {
      resolvedAccount = student;
      role = "student";
    }
  }

  if (!resolvedAccount) {
    showToast("Account Not Found", `No account matching "${input}". Please verify your Student ID (e.g. 183) or register.`, "error");
    return;
  }

  // 2. Generate 6-Digit OTP Code
  const otpCode = String(Math.floor(100000 + Math.random() * 900000));
  activeResetSession = {
    account: resolvedAccount,
    role: role,
    code: otpCode,
    expiresAt: Date.now() + 5 * 60 * 1000 // 5 minutes
  };

  // 3. Update Step 2 UI
  const nameEl = document.getElementById("forgot-account-name-text");
  if (nameEl) nameEl.textContent = `${resolvedAccount.name} (${resolvedAccount.id})`;

  const displayEl = document.getElementById("forgot-otp-display-code");
  if (displayEl) displayEl.textContent = otpCode;

  const destEl = document.getElementById("forgot-otp-destination");
  if (destEl) destEl.textContent = `Dispatched to: ${resolvedAccount.email || 'Registered Contact'}`;

  // Clear input fields
  document.getElementById("forgot-otp-input").value = "";
  document.getElementById("forgot-new-pass").value = "";
  document.getElementById("forgot-conf-pass").value = "";

  // Switch steps
  document.getElementById("forgot-step-1").classList.add("hidden");
  document.getElementById("forgot-step-2").classList.remove("hidden");

  // Show Toast
  showToast("OTP Dispatched!", `Verification Code: ${otpCode} (Sent to ${resolvedAccount.name})`, "info");
}

function autofillReceivedOtp() {
  if (activeResetSession && activeResetSession.code) {
    const input = document.getElementById("forgot-otp-input");
    if (input) {
      input.value = activeResetSession.code;
      input.focus();
    }
    showToast("OTP Auto-Filled", `Inserted verification code: ${activeResetSession.code}`, "info");
  }
}

function handleVerifyOtpAndReset() {
  if (!activeResetSession) {
    showToast("Session Expired", "Please request a new OTP code.", "error");
    handleForgotBackToStep1();
    return;
  }

  const enteredOtp = document.getElementById("forgot-otp-input").value.trim();
  const newPass = document.getElementById("forgot-new-pass").value.trim();
  const confPass = document.getElementById("forgot-conf-pass").value.trim();

  // Validate OTP
  if (!enteredOtp) {
    showToast("Input Required", "Please enter the 6-digit OTP code.", "warning");
    return;
  }

  if (enteredOtp !== activeResetSession.code) {
    showToast("Incorrect OTP", "The entered OTP code does not match. Check the dispatch box above.", "error");
    return;
  }

  if (Date.now() > activeResetSession.expiresAt) {
    showToast("OTP Expired", "This code has expired. Please request a new OTP.", "error");
    return;
  }

  // Validate Password
  if (!newPass || newPass.length < 4) {
    showToast("Weak Password", "New password must be at least 4 characters long.", "warning");
    return;
  }

  if (newPass !== confPass) {
    showToast("Password Mismatch", "New password and Confirm password do not match.", "error");
    return;
  }

  // Update password
  if (activeResetSession.role === "admin") {
    const adm = admins.find(a => a.username.toLowerCase() === activeResetSession.account.id.toLowerCase());
    if (adm) {
      adm.password = newPass;
      if (adm.username.toLowerCase() === "admin") {
        adminPassword = newPass;
      }
    }
    saveState();
    logAuditEvent(`ADMIN-${activeResetSession.account.id}`, "PASSWORD_RESET", activeResetSession.account.name, "Administrator password updated via OTP.");
    
    // Prefill Admin form
    switchAuthTab("admin");
    document.getElementById("login-admin-user").value = activeResetSession.account.id;
    document.getElementById("login-admin-pass").value = newPass;
  } else {
    // Student update
    const student = students.find(s => s.id.toLowerCase() === activeResetSession.account.id.toLowerCase());
    if (student) {
      student.password = newPass;
      saveState();
      logAuditEvent(`STUDENT-${student.id}`, "PASSWORD_RESET", student.name, "Student password updated via OTP.");

      // Prefill Student form
      switchAuthTab("student");
      document.getElementById("login-student-id").value = student.id;
      document.getElementById("login-student-pass").value = newPass;
    }
  }

  closeModal("modal-forgot-password");
  showToast("Password Reset Successful!", `Password updated for ${activeResetSession.account.name}. Login with your new password.`, "success");

  activeResetSession = null;
}

// -------------------------------------------------------------
// 13. Live SMTP & Gateway Configuration
// -------------------------------------------------------------
function saveGatewayConfig() {
  if (!isMasterAdmin()) {
    showToast("Access Restricted", "Security Policy: Only the Master Administrator can modify Gateway Infrastructure settings.", "error");
    return;
  }

  const isEnabled = document.getElementById("smtp-enable-check").checked;
  const host = document.getElementById("smtp-host").value.trim();
  const port = document.getElementById("smtp-port").value.trim();

  localStorage.setItem("campusvoice_smtp_enabled", isEnabled ? "true" : "false");
  localStorage.setItem("campusvoice_smtp_host", host);
  localStorage.setItem("campusvoice_smtp_port", port);

  closeModal("modal-gateway-config");
  showToast("Gateway Saved", `SMTP dispatcher configuration persisted (${host}:${port}).`, "success");
}

function testGatewayDispatch() {
  if (!isMasterAdmin()) {
    showToast("Access Restricted", "Security Policy: Only the Master Administrator can dispatch gateway test transmissions.", "error");
    return;
  }

  const testOtp = String(Math.floor(100000 + Math.random() * 900000));
  showToast("Testing Connection...", "Attempting TLS handshake with configured SMTP host...", "info");
  setTimeout(() => {
    showToast("Gateway Online", `Test Dispatch Successful! Simulated OTP: ${testOtp} delivered.`, "success");
  }, 1000);
}

// -------------------------------------------------------------
// 14. Modals Controller
// -------------------------------------------------------------
function openModal(id) {
  if (id === "modal-add-admin" && !isMasterAdmin()) {
    showToast("Access Restricted", "Only the Master Administrator has privileges to provision new administrators.", "error");
    return;
  }

  const modal = document.getElementById(id);
  if (!modal) return;
  modal.classList.remove("hidden");

  if (id === "modal-verify") {
    const token = currentStudent ? `CV${currentStudent.id}2026` : DEFAULT_DEMO_TOKEN;
    document.getElementById("verify-token-input").value = isTokenUsed ? "" : token;
    updateVerifyModalStats();
  } else if (id === "modal-audit-log") {
    renderAuditTrailTable();
  } else if (id === "modal-gateway-config") {
    const master = isMasterAdmin();
    const banner = document.getElementById("smtp-master-only-banner");
    const saveBtn = document.getElementById("btn-smtp-save");
    const testBtn = document.getElementById("btn-smtp-test");
    const chk = document.getElementById("smtp-enable-check");
    const host = document.getElementById("smtp-host");
    const port = document.getElementById("smtp-port");
    const user = document.getElementById("smtp-user");
    const from = document.getElementById("smtp-from");

    if (banner) {
      if (master) banner.classList.add("hidden");
      else banner.classList.remove("hidden");
    }
    if (saveBtn) saveBtn.disabled = !master;
    if (testBtn) testBtn.disabled = !master;
    if (chk) chk.disabled = !master;
    if (host) host.disabled = !master;
    if (port) port.disabled = !master;
    if (user) user.disabled = !master;
    if (from) from.disabled = !master;
  }
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.add("hidden");
}

window.addEventListener("click", (e) => {
  if (e.target.classList.contains("modal-overlay")) {
    e.target.classList.add("hidden");
  }
});

window.addEventListener("keydown", (e) => {
  if (e.key === "Escape") {
    document.querySelectorAll(".modal-overlay").forEach(m => m.classList.add("hidden"));
  }
});

// -------------------------------------------------------------
// 15. Toast Notification System
// -------------------------------------------------------------
function showToast(title, message, type = "info") {
  const container = document.getElementById("toast-container");
  if (!container) return;

  const toast = document.createElement("div");
  toast.className = `toast ${type}`;

  const icons = {
    success: "fa-solid fa-circle-check text-emerald-400",
    error: "fa-solid fa-circle-xmark text-rose-400",
    warning: "fa-solid fa-triangle-exclamation text-amber-400",
    info: "fa-solid fa-circle-info text-blue-400"
  };

  toast.innerHTML = `
    <i class="${icons[type] || icons.info} text-xl mt-0.5"></i>
    <div class="flex-1">
      <h5 class="text-xs font-black uppercase tracking-wider">${title}</h5>
      <p class="text-xs text-slate-200 mt-0.5">${message}</p>
    </div>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateX(50px)";
    toast.style.transition = "all 0.3s ease";
    setTimeout(() => toast.remove(), 300);
  }, 4500);
}

// -------------------------------------------------------------
// 16. App Bootstrapping
// -------------------------------------------------------------
document.addEventListener("DOMContentLoaded", async () => {
  initTheme();
  refreshDashboard();

  // Establish full-stack connection with backend API
  await syncWithBackend();

  const urlParams = new URLSearchParams(window.location.search);
  const trackParam = urlParams.get("track");
  if (trackParam) {
    setTimeout(() => {
      quickTrack(trackParam);
    }, 300);
  }
});
