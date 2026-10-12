const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
const crypto = require('crypto');

const app = express();

// Middlewares
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// -------------------------------------------------------------
// Database Persistence Layer (JSON file or In-Memory)
// -------------------------------------------------------------
const DATA_DIR = path.join(__dirname, 'data');
const DB_FILE = path.join(DATA_DIR, 'campusvoice_db.json');

const ESTIMATED_CLASS_SIZE = 60;

const DEFAULT_SEED_DATA = {
  issues: [
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
      reverify: { resolved: 0, partially: 0, stillFacing: 0 },
      voterHashes: []
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
      reverify: { resolved: 0, partially: 0, stillFacing: 0 },
      voterHashes: []
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
      reverify: { resolved: 8, partially: 1, stillFacing: 0 },
      voterHashes: []
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
      reverify: { resolved: 0, partially: 0, stillFacing: 0 },
      voterHashes: []
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
      reverify: { resolved: 0, partially: 0, stillFacing: 0 },
      voterHashes: []
    }
  ],
  students: [
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
  ],
  admins: [
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
  ],
  auditLogs: [
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
  ],
  notices: [
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
  ]
};

// In-Memory Database Store
let db = JSON.parse(JSON.stringify(DEFAULT_SEED_DATA));

function loadDB() {
  try {
    if (fs.existsSync(DB_FILE)) {
      const content = fs.readFileSync(DB_FILE, 'utf-8');
      db = JSON.parse(content);
    } else {
      saveDB();
    }
  } catch (err) {
    console.warn('Could not read persistent DB file, using memory store:', err.message);
  }
}

function saveDB() {
  try {
    if (!fs.existsSync(DATA_DIR)) {
      fs.mkdirSync(DATA_DIR, { recursive: true });
    }
    fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2), 'utf-8');
  } catch (err) {
    // In read-only serverless environments (like Vercel Lambda), disk write might fail gracefully
    console.warn('Disk save warning (normal in serverless):', err.message);
  }
}

// Initial DB load
loadDB();

// Helper: Append audit log
function addAuditLog(issueId, action, actor, remarks) {
  const now = new Date();
  const dateStr = now.toISOString().replace('T', ' ').substring(0, 16);
  db.auditLogs.unshift({
    timestamp: dateStr,
    issueId,
    action,
    actor,
    remarks
  });
  saveDB();
}

// Helper: Department routing dictionary
const DEPT_MAP = {
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

// -------------------------------------------------------------
// REST API ROUTES
// -------------------------------------------------------------

// 1. Health & Server Info
app.get('/api/health', (req, res) => {
  res.json({
    status: 'ok',
    system: 'CampusVoice Core Engine & API',
    version: '2.5.0',
    fullStack: true,
    platform: process.env.VERCEL ? 'Vercel Serverless' : (process.env.RENDER ? 'Render Web Service' : 'Local Node.js'),
    timestamp: new Date().toISOString()
  });
});

// 2. Authentication: Student & Admin Login
app.post('/api/auth/login', (req, res) => {
  const { studentId, username, password } = req.body;
  const userIdentifier = (studentId || username || '').trim();
  const userPassword = (password || '').trim();

  if (!userIdentifier || !userPassword) {
    return res.status(400).json({ success: false, message: 'Identifier and password are required.' });
  }

  // Check admin first
  const admin = db.admins.find(
    a => a.username.toLowerCase() === userIdentifier.toLowerCase() && a.password === userPassword
  );

  if (admin) {
    addAuditLog(`ADMIN-${admin.username}`, 'LOGIN', admin.name, `Administrator logged in: ${admin.username}`);
    return res.json({
      success: true,
      role: 'admin',
      user: {
        username: admin.username,
        name: admin.name,
        email: admin.email,
        department: admin.department,
        role: admin.role,
        isPrimary: admin.isPrimary
      },
      message: `Welcome back, ${admin.name}!`
    });
  }

  // Check student
  const student = db.students.find(
    s => s.id.toLowerCase() === userIdentifier.toLowerCase() && s.password === userPassword
  );

  if (student) {
    return res.json({
      success: true,
      role: 'student',
      user: {
        id: student.id,
        name: student.name,
        roll: student.roll,
        email: student.email,
        dept: student.dept,
        section: student.section
      },
      message: `Welcome ${student.name}!`
    });
  }

  return res.status(401).json({
    success: false,
    message: 'Invalid credentials. Please verify your Student ID/Username and password.'
  });
});

// 3. Student Registration
app.post('/api/auth/register', (req, res) => {
  const { name, id, roll, email, dept, section, password } = req.body;

  if (!name || !id || !roll || !password) {
    return res.status(400).json({ success: false, message: 'All mandatory fields are required.' });
  }

  if (password.length < 4) {
    return res.status(400).json({ success: false, message: 'Password must be at least 4 characters long.' });
  }

  if (db.students.some(s => s.id.toLowerCase() === id.toLowerCase())) {
    return res.status(409).json({ success: false, message: `Student ID "${id}" is already registered. Please login.` });
  }

  const newStudent = {
    id: id.trim(),
    password: password.trim(),
    name: name.trim(),
    roll: roll.trim(),
    email: (email || `${id}@aryacollege.in`).trim(),
    dept: dept || 'B.Tech Artificial Intelligence & Data Science',
    section: section || 'C'
  };

  db.students.push(newStudent);
  saveDB();

  return res.status(201).json({
    success: true,
    message: `Account created successfully for ${newStudent.name}.`,
    user: newStudent
  });
});

// 4. Logout
app.post('/api/auth/logout', (req, res) => {
  res.json({ success: true, message: 'Signed out successfully.' });
});

// 5. Password Reset / OTP Simulation
let resetTokens = {}; // token -> { email, studentId, expires }

app.post('/api/auth/forgot-password', (req, res) => {
  const { identifier } = req.body;
  if (!identifier) {
    return res.status(400).json({ success: false, message: 'Roll number, Student ID, or Username required.' });
  }

  const student = db.students.find(
    s => s.id.toLowerCase() === identifier.toLowerCase() || s.roll.toLowerCase() === identifier.toLowerCase()
  );

  const admin = db.admins.find(a => a.username.toLowerCase() === identifier.toLowerCase());

  if (!student && !admin) {
    return res.status(404).json({ success: false, message: 'No registered student or administrative account found.' });
  }

  const otp = Math.floor(100000 + Math.random() * 900000).toString();
  const key = identifier.toLowerCase();
  resetTokens[key] = {
    otp,
    isStudent: !!student,
    account: student || admin,
    expires: Date.now() + 10 * 60 * 1000 // 10 minutes
  };

  return res.json({
    success: true,
    otp, // Delivered in payload for demo/academic testing
    email: student ? student.email : admin.email,
    message: `A verification code has been dispatched to ${student ? student.email : admin.email}`
  });
});

app.post('/api/auth/reset-password', (req, res) => {
  const { identifier, otp, newPassword } = req.body;
  const record = resetTokens[identifier.toLowerCase()];

  if (!record || record.otp !== otp || Date.now() > record.expires) {
    return res.status(400).json({ success: false, message: 'Invalid or expired OTP verification code.' });
  }

  if (record.isStudent) {
    const student = db.students.find(s => s.id === record.account.id);
    if (student) student.password = newPassword;
  } else {
    const admin = db.admins.find(a => a.username === record.account.username);
    if (admin) admin.password = newPassword;
  }

  delete resetTokens[identifier.toLowerCase()];
  saveDB();

  return res.json({ success: true, message: 'Password updated successfully. Please login with your new password.' });
});

// 6. Issues: Fetch All / Filtered
app.get('/api/issues', (req, res) => {
  const { category, status, priority, search } = req.query;
  let results = [...db.issues];

  if (category && category !== 'ALL') {
    results = results.filter(i => i.category.toLowerCase() === category.toLowerCase());
  }

  if (status && status !== 'ALL') {
    results = results.filter(i => i.status.toUpperCase() === status.toUpperCase());
  }

  if (priority && priority !== 'ALL') {
    results = results.filter(i => i.priority.toUpperCase() === priority.toUpperCase());
  }

  if (search) {
    const q = search.toLowerCase().trim();
    results = results.filter(i =>
      i.id.toLowerCase().includes(q) ||
      i.title.toLowerCase().includes(q) ||
      i.category.toLowerCase().includes(q) ||
      (i.assignedDept && i.assignedDept.toLowerCase().includes(q))
    );
  }

  res.json({
    success: true,
    total: results.length,
    issues: results
  });
});

// 7. Issues: Class Feed (For Peer Voting)
app.get('/api/issues/class', (req, res) => {
  res.json({
    success: true,
    classSize: ESTIMATED_CLASS_SIZE,
    issues: db.issues
  });
});

// 8. Issues: Track Single Issue
app.get('/api/issues/track', (req, res) => {
  const { id } = req.query;
  if (!id) {
    return res.status(400).json({ success: false, message: 'Issue ID required (e.g. CV-1847).' });
  }

  const issue = db.issues.find(i => i.id.toUpperCase() === id.toUpperCase().trim());
  if (!issue) {
    return res.status(404).json({ success: false, message: `No grievance found with tracking ID "${id}".` });
  }

  const relatedLogs = db.auditLogs.filter(l => l.issueId && l.issueId.toUpperCase() === issue.id.toUpperCase());

  res.json({
    success: true,
    issue,
    auditLogs: relatedLogs
  });
});

// 9. Issues: Submit Anonymous Grievance (Strict Identity Decoupling)
app.post('/api/issues', (req, res) => {
  const { category, priority, title, description, evidence } = req.body;

  if (!title || !description) {
    return res.status(400).json({ success: false, message: 'Title and detailed description are required.' });
  }

  // Generate unique CV-XXXX identifier
  let newId;
  do {
    const rand = Math.floor(1000 + Math.random() * 9000);
    newId = `CV-${rand}`;
  } while (db.issues.some(i => i.id === newId));

  const today = new Date().toISOString().split('T')[0];
  const assignedDept = DEPT_MAP[category] || "General Administration";

  const newIssue = {
    id: newId,
    category: category || "Other",
    priority: priority || "MEDIUM",
    assignedDept,
    title: title.trim(),
    description: description.trim(),
    evidence: evidence || "",
    date: today,
    status: "REPORTED",
    adminRemarks: "New issue filed anonymously. Pending initial review or class voting.",
    votes: { frequently: 0, sometimes: 0, never: 0 },
    reverify: { resolved: 0, partially: 0, stillFacing: 0 },
    voterHashes: []
  };

  db.issues.unshift(newIssue);
  addAuditLog(newId, "ISSUE_REPORTED", "ANONYMOUS_STUDENT", `Filed anonymous ${category} grievance (${priority} priority).`);
  saveDB();

  return res.status(201).json({
    success: true,
    message: 'Anonymous grievance filed successfully.',
    issueId: newId,
    issue: newIssue
  });
});

// 10. Issues: Democratic Class-Level Peer Verification Voting
// Algorithmic Core: Participation Quorum >= 30%, Weighted Confirmation Score >= 60%
app.post('/api/issues/vote', (req, res) => {
  const { issueId, voteOption, token, studentId } = req.body;

  if (!issueId || !voteOption) {
    return res.status(400).json({ success: false, message: 'Issue ID and Vote Option are required.' });
  }

  const issue = db.issues.find(i => i.id.toUpperCase() === issueId.toUpperCase());
  if (!issue) {
    return res.status(404).json({ success: false, message: 'Issue not found.' });
  }

  if (!['Frequently', 'Sometimes', 'Never'].includes(voteOption)) {
    return res.status(400).json({ success: false, message: 'Vote must be Frequently, Sometimes, or Never.' });
  }

  // Token hashing for double-voting prevention without storing voter identity
  const voterKey = token || studentId || 'DEMO_USER_' + req.ip;
  const voterHash = crypto.createHash('sha256').update(`${issue.id}:${voterKey}:campusvoice_salt`).digest('hex');

  if (!issue.voterHashes) issue.voterHashes = [];
  if (issue.voterHashes.includes(voterHash)) {
    return res.status(409).json({ success: false, message: 'Single-use daily verification limit reached for this issue.' });
  }

  // Record vote
  if (voteOption === 'Frequently') issue.votes.frequently++;
  if (voteOption === 'Sometimes') issue.votes.sometimes++;
  if (voteOption === 'Never') issue.votes.never++;
  issue.voterHashes.push(voterHash);

  // Algorithmic Promotion Logic (Module-2)
  const total = issue.votes.frequently + issue.votes.sometimes + issue.votes.never;
  const participationRate = (total / ESTIMATED_CLASS_SIZE) * 100;
  const weightedConfirmRate = ((issue.votes.frequently * 1.0 + issue.votes.sometimes * 0.5) / total) * 100;

  let stateChanged = false;
  let statusRemarks = "";

  if (participationRate >= 30 && weightedConfirmRate >= 60) {
    if (issue.status !== "CLASS VERIFIED") {
      issue.status = "CLASS VERIFIED";
      stateChanged = true;
      statusRemarks = `Class Quorum achieved (${Math.round(participationRate)}% turnout, ${Math.round(weightedConfirmRate)}% confirmation). Promoted to CLASS VERIFIED.`;
      addAuditLog(issue.id, "STATUS_PROMOTED", "VERIFICATION_ENGINE", statusRemarks);
    }
  } else if (total > 0 && issue.status === "REPORTED") {
    issue.status = "UNDER VERIFICATION";
    stateChanged = true;
    statusRemarks = "Voting active. State transitioned to UNDER VERIFICATION.";
    addAuditLog(issue.id, "STATUS_CHANGE", "VERIFICATION_ENGINE", statusRemarks);
  }

  saveDB();

  return res.json({
    success: true,
    message: `Anonymous vote (${voteOption}) recorded for ${issue.id}.`,
    issue,
    stats: {
      totalVotes: total,
      participationRate: Math.round(participationRate),
      confirmationScore: Math.round(weightedConfirmRate),
      currentStatus: issue.status,
      stateChanged
    }
  });
});

// 11. Issues: Post-Resolution Student Re-verification & Auto-Reopening
app.post('/api/issues/reverify', (req, res) => {
  const { issueId, response } = req.body;

  if (!issueId || !response) {
    return res.status(400).json({ success: false, message: 'Issue ID and feedback response required.' });
  }

  const issue = db.issues.find(i => i.id.toUpperCase() === issueId.toUpperCase());
  if (!issue) {
    return res.status(404).json({ success: false, message: 'Issue not found.' });
  }

  if (!issue.reverify) {
    issue.reverify = { resolved: 0, partially: 0, stillFacing: 0 };
  }

  if (response === 'Resolved Completely') {
    issue.reverify.resolved++;
    addAuditLog(issue.id, "REVERIFY_CONFIRM", "STUDENT", "Student verified complete issue resolution.");
  } else if (response === 'Partially Resolved') {
    issue.reverify.partially++;
    addAuditLog(issue.id, "REVERIFY_PARTIAL", "STUDENT", "Student reported partial resolution.");
  } else if (response === 'Still Facing Problem') {
    issue.reverify.stillFacing++;
    // Auto-Reopen algorithm triggered
    issue.status = "REOPENED";
    issue.priority = "CRITICAL";
    issue.adminRemarks = "AUTO-REOPENED: Recurring student complaints detected following management fix.";
    addAuditLog(issue.id, "AUTO_REOPEN_TRIGGER", "REVERIFICATION_ENGINE", "Auto-reopen triggered due to ongoing dissatisfaction report. Escalated to CRITICAL priority.");
  }

  saveDB();

  return res.json({
    success: true,
    message: response === 'Still Facing Problem' ? 'Issue escalated and auto-reopened!' : 'Re-verification feedback recorded.',
    issue
  });
});

// 12. Admin Dashboard Metrics
app.get('/api/admin/dashboard', (req, res) => {
  let reported = 0, verifying = 0, verified = 0, investigating = 0, resolved = 0, reopened = 0;

  db.issues.forEach(i => {
    const s = i.status.toUpperCase();
    if (s === "REPORTED") reported++;
    if (s === "UNDER VERIFICATION") verifying++;
    if (s === "CLASS VERIFIED") verified++;
    if (s === "UNDER INVESTIGATION") investigating++;
    if (s === "RESOLVED") resolved++;
    if (s === "REOPENED") reopened++;
  });

  res.json({
    success: true,
    kpi: {
      total: db.issues.length,
      reported,
      verifying,
      verified,
      investigating,
      resolved,
      reopened
    },
    recentIssues: db.issues.slice(0, 10),
    recentAudit: db.auditLogs.slice(0, 10)
  });
});

// 13. Admin Grievances List (with filtering)
app.get('/api/admin/issues', (req, res) => {
  const { category, status, search, department } = req.query;
  let results = [...db.issues];

  if (category && category !== 'ALL') {
    results = results.filter(i => i.category.toLowerCase() === category.toLowerCase());
  }

  if (status && status !== 'ALL') {
    results = results.filter(i => i.status.toUpperCase() === status.toUpperCase());
  }

  if (department && department !== 'ALL') {
    results = results.filter(i => i.assignedDept && i.assignedDept.toLowerCase() === department.toLowerCase());
  }

  if (search) {
    const q = search.toLowerCase().trim();
    results = results.filter(i =>
      i.id.toLowerCase().includes(q) ||
      i.title.toLowerCase().includes(q) ||
      i.category.toLowerCase().includes(q) ||
      (i.assignedDept && i.assignedDept.toLowerCase().includes(q))
    );
  }

  res.json({ success: true, issues: results });
});

// 14. Admin Issue Status & Department Update
app.post('/api/admin/issues/update', (req, res) => {
  const { issueId, status, department, remarks, adminName } = req.body;

  if (!issueId) {
    return res.status(400).json({ success: false, message: 'Issue ID required.' });
  }

  const issue = db.issues.find(i => i.id.toUpperCase() === issueId.toUpperCase());
  if (!issue) {
    return res.status(404).json({ success: false, message: 'Issue not found.' });
  }

  const oldStatus = issue.status;
  if (status) issue.status = status;
  if (department) issue.assignedDept = department;
  if (remarks !== undefined) issue.adminRemarks = remarks;

  addAuditLog(
    issue.id,
    "ADMIN_UPDATE",
    adminName || "ADMINISTRATOR",
    `Updated status to ${issue.status}${department ? ` & assigned to ${department}` : ''}.`
  );

  saveDB();

  return res.json({
    success: true,
    message: `Issue ${issue.id} updated successfully.`,
    issue
  });
});

// 15. Admin Team Management
app.get('/api/admin/admins', (req, res) => {
  const safeAdmins = db.admins.map(a => ({
    username: a.username,
    name: a.name,
    email: a.email,
    department: a.department,
    role: a.role,
    dateAdded: a.dateAdded,
    isPrimary: a.isPrimary
  }));
  res.json({ success: true, admins: safeAdmins });
});

app.post('/api/admin/admins', (req, res) => {
  const { username, password, name, email, department, role } = req.body;

  if (!username || !password || !name) {
    return res.status(400).json({ success: false, message: 'Username, password and name are required.' });
  }

  if (db.admins.some(a => a.username.toLowerCase() === username.toLowerCase())) {
    return res.status(409).json({ success: false, message: `Admin username "${username}" already exists.` });
  }

  const newAdmin = {
    username: username.trim(),
    password: password.trim(),
    name: name.trim(),
    email: (email || `${username}@aryacollege.in`).trim(),
    department: department || 'General Grievance Board',
    role: role || 'Department Reviewer',
    dateAdded: new Date().toISOString().split('T')[0],
    isPrimary: false
  };

  db.admins.push(newAdmin);
  saveDB();

  return res.status(201).json({
    success: true,
    message: `Administrator "${newAdmin.name}" onboarded successfully.`,
    admin: {
      username: newAdmin.username,
      name: newAdmin.name,
      email: newAdmin.email,
      department: newAdmin.department,
      role: newAdmin.role
    }
  });
});

// 16. Audit Logs
app.get('/api/audit-logs', (req, res) => {
  res.json({ success: true, logs: db.auditLogs });
});

// 17. Campus Notices
app.get('/api/notices', (req, res) => {
  res.json({ success: true, notices: db.notices });
});

// -------------------------------------------------------------
// STATIC ASSETS WITH FAIL-SAFE IN-MEMORY CACHE & DIRECT MIME HANDLING
// -------------------------------------------------------------
function getAssetPath(filename) {
  const candidates = [
    path.join(__dirname, filename),
    path.join(process.cwd(), filename),
    path.join(__dirname, 'public', filename),
    path.join(process.cwd(), 'public', filename)
  ];
  return candidates.find(p => fs.existsSync(p)) || path.join(__dirname, filename);
}

// Pre-read assets so NFT traces them and caches in memory
let cachedHtml = '';
let cachedCss = '';
let cachedJs = '';

try {
  cachedHtml = fs.readFileSync(getAssetPath('index.html'), 'utf8');
} catch (e) {
  console.warn('HTML cache load warning:', e.message);
}

try {
  cachedCss = fs.readFileSync(getAssetPath('style.css'), 'utf8');
} catch (e) {
  console.warn('CSS cache load warning:', e.message);
}

try {
  cachedJs = fs.readFileSync(getAssetPath('app.js'), 'utf8');
} catch (e) {
  console.warn('JS cache load warning:', e.message);
}

// Explicit endpoints for critical frontend assets
app.get('/style.css', (req, res) => {
  res.setHeader('Content-Type', 'text/css; charset=utf-8');
  if (cachedCss) return res.send(cachedCss);
  const p = getAssetPath('style.css');
  if (fs.existsSync(p)) return res.sendFile(p);
  res.status(404).send('/* CSS not found */');
});

app.get('/app.js', (req, res) => {
  res.setHeader('Content-Type', 'application/javascript; charset=utf-8');
  if (cachedJs) return res.send(cachedJs);
  const p = getAssetPath('app.js');
  if (fs.existsSync(p)) return res.sendFile(p);
  res.status(404).send('/* JS not found */');
});

app.get('/', (req, res) => {
  res.setHeader('Content-Type', 'text/html; charset=utf-8');
  if (cachedHtml) return res.send(cachedHtml);
  const p = getAssetPath('index.html');
  if (fs.existsSync(p)) return res.sendFile(p);
  res.status(404).send('Index not found');
});

// Standard static middleware for images or other assets
app.use(express.static(path.join(__dirname)));
app.use(express.static(process.cwd()));

// Fallback SPA routing for HTML5 pushState
app.use((req, res) => {
  if (req.path.startsWith('/api/')) {
    return res.status(404).json({ success: false, message: 'API endpoint not found' });
  }
  res.setHeader('Content-Type', 'text/html; charset=utf-8');
  if (cachedHtml) return res.send(cachedHtml);
  res.sendFile(getAssetPath('index.html'));
});

// Start listening if run directly (Render, localhost, Docker)
const PORT = process.env.PORT || 3000;
if (require.main === module) {
  app.listen(PORT, () => {
    console.log('====================================================');
    console.log('  CampusVoice Full-Stack Web Application Active     ');
    console.log(`  Live URL: http://localhost:${PORT}                `);
    console.log('  REST API: http://localhost:' + PORT + '/api/health');
    console.log('====================================================');
  });
}

// Export for Vercel Serverless
module.exports = app;
