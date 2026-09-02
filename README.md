# CyberShield Incident Management

A beginner-friendly Java full-stack web application for reporting, managing, monitoring, assigning, and auditing cybersecurity incidents.

## Project Overview

CyberShield Incident Management provides a comprehensive incident-management workflow:

```text
Register / Login (USER or ADMIN)
   ↓
Dashboard & Statistics
   ↓
Report Incident (USER / ADMIN)
   ↓
Manage Incidents (Search, Filters, My Incidents)
   ↓
Incident Assignment (ADMIN only)
   ↓
Update / Delete Incidents (ADMIN only)
   ↓
Admin User Management (Roles, Active/Inactive Status)
   ↓
Automated Activity / Audit Logging
```

The project is intentionally built without Spring Boot so that the core concepts of Java backend development, JDBC, HTTP APIs, sessions, server-side authorization, and frontend-backend communication remain easy to understand.

---

## Technologies Used

### Frontend
- HTML5
- CSS3 (Vanilla CSS with custom properties, dark/light theme support)
- Vanilla JavaScript (Fetch API, Local Storage)

### Backend
- Java (Core Java JDK)
- `com.sun.net.httpserver.HttpServer`
- REST-style HTTP APIs
- JDBC (`java.sql.PreparedStatement`, `java.sql.Connection`)
- In-memory session management (`SessionManager`)

### Database
- MySQL Database
- MySQL Connector/J JDBC Driver (`mysql-connector-j-9.x.x.jar` / `sql.jar`)

### Development Tools
- Visual Studio Code
- MySQL Workbench
- VS Code Live Server extension

---

## Project Structure

```text
CyberShield
│
├── backend
│   ├── lib
│   │   └── sql.jar (or mysql-connector-j-9.x.x.jar)
│   ├── out
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── cybershield
│                       └── incidentmanagement
│                           ├── config
│                           ├── controller
│                           │   ├── DashboardController.java
│                           │   ├── IncidentController.java
│                           │   ├── LoginController.java
│                           │   ├── LogoutController.java
│                           │   ├── RegisterController.java
│                           │   ├── TestController.java
│                           │   ├── UserManagementController.java
│                           │   └── UserProfileController.java
│                           ├── database
│                           │   ├── DatabaseConnection.java
│                           │   ├── DatabaseInitializer.java
│                           │   └── DatabaseTest.java
│                           ├── entity
│                           │   ├── ActivityLog.java
│                           │   ├── Incident.java
│                           │   └── User.java
│                           ├── repository
│                           │   ├── ActivityLogRepository.java
│                           │   ├── IncidentRepository.java
│                           │   ├── IncidentRepositoryTest.java
│                           │   └── UserRepository.java
│                           ├── service
│                           │   ├── IncidentService.java
│                           │   ├── SessionManager.java
│                           │   └── UserService.java
│                           └── server
│                               └── CyberShieldServer.java
│
├── frontend
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── css
│   │   └── style.css
│   ├── js
│   │   ├── create-incident.js
│   │   ├── dashboard.js
│   │   ├── incident-details.js
│   │   ├── incidents.js
│   │   ├── login.js
│   │   ├── profile.js
│   │   ├── register.js
│   │   ├── script.js
│   │   ├── theme.js
│   │   └── users.js
│   └── pages
│       ├── create-incident.html
│       ├── dashboard.html
│       ├── incident-details.html
│       ├── incidents.html
│       ├── profile.html
│       └── users.html
│
└── database
    └── database.sql
```

---

## Main Features

### 1. User Authentication & Roles
- **Registration**: New users register with default `USER` role.
- **Login / Logout**: Authenticates user against MySQL database. Returns a `Session-Id` header.
- **Session Management**: In-memory `SessionManager` tracks logged-in users, emails, and roles.
- **Account Status**: Checks whether user account is active before allowing login.
- **Roles**:
  - `USER`: Can register, login, view incidents, create incidents, view own incidents (*My Incidents*), view profile, and view incident activity logs.
  - `ADMIN`: Has all `USER` permissions plus update incidents, delete incidents, assign/unassign incidents, and manage users.

### 2. Default Admin Protection
When the server starts, `DatabaseInitializer` automatically creates the default development admin if it does not already exist:
- **Name**: CyberShield Admin
- **Email**: `admin@cybershield.com`
- **Password**: `Admin@12345`
- **Role**: `ADMIN`
- **Protection Rule**: The default admin account is protected in backend logic (`UserManagementController`) from being demoted or disabled through user management APIs.

### 3. Incident Management
- **Create Incident**: Report incident with title, description, category, severity (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), and status.
- **Incident Listing & Views**: View all incidents or toggle to *My Incidents* view.
- **Search & Filters**: Search by title, category, severity, status, reporter name, or assigned user name. Filter dropdowns for severity and status.
- **Incident Details**: Displays Incident ID, Title, Category, Severity, Status, Reported By (user name), Assigned To (user name), Created At timestamp, Updated At timestamp, Description, and Activity Log.
- **Update & Delete**: Admin users can edit incident details or delete incidents.

### 4. Incident Assignment (Batch 7)
- **Admin Assignment**: Admins can view available users and assign an incident to any `USER` or `ADMIN` account.
- **Change & Remove Assignment**: Admins can change the assigned user or unassign the incident (`assignedTo: null`).
- **Authorization**: Server strictly enforces `403 Forbidden` if a non-admin attempts assignment.

### 5. Activity / Audit Log (Batch 8)
- **Automatic Backend Auditing**: Operations trigger automatic audit entries in `activity_logs`:
  - `CREATED`: Recorded on incident creation.
  - `UPDATED`: Recorded on incident updates (e.g., status/severity changes).
  - `ASSIGNED`: Recorded when assigned to a user.
  - `UNASSIGNED`: Recorded when an incident is unassigned.
  - `DELETED`: Recorded prior to incident deletion.
- **Activity API**: `GET /api/incidents/{id}/activity` fetches audit history.
- **UI Display**: Detailed audit table on `incident-details.html` showing Action, Performed By, Description, and Date/Time.

### 6. Admin User Management (Batch 6)
- **User List**: Admins can view all registered users (`GET /api/users`).
- **Role Change**: Admins can change user roles (`PUT /api/users/{id}/role`).
- **Status Change**: Admins can activate or disable user accounts (`PUT /api/users/{id}/status`).

### 7. Dashboard & Statistics
- Metrics for Total, Open, In Progress, Resolved, and Critical incidents calculated dynamically from MySQL database.
- Visual statistics presentation built with clean HTML/CSS/JS without third-party chart libraries.

---

## API Documentation

| HTTP Method | Endpoint | Purpose | Authorization | Body / Parameters |
|---|---|---|---|---|
| `GET` | `/api/test` | Backend health check | Public | None |
| `POST` | `/api/register` | User registration | Public | `{"name","email","password"}` |
| `POST` | `/api/login` | User login | Public | `{"email","password"}` |
| `POST` | `/api/logout` | User logout | Authenticated | Header `Session-Id` |
| `GET` | `/api/profile` | Get logged-in profile | Authenticated | Header `Session-Id` |
| `GET` | `/api/dashboard` | Dashboard statistics | Authenticated | Header `Session-Id` |
| `GET` | `/api/incidents` | List all incidents | Authenticated | Header `Session-Id` |
| `POST` | `/api/incidents` | Create incident | Authenticated | `{"title","description","category","severity","reportedBy"}` |
| `GET` | `/api/incidents/{id}` | Get incident details | Authenticated | Header `Session-Id` |
| `PUT` | `/api/incidents/{id}` | Update incident | ADMIN | `{"title","description","category","severity","status"}` |
| `DELETE` | `/api/incidents/{id}` | Delete incident | ADMIN | Header `Session-Id` |
| `PUT` | `/api/incidents/{id}/assignment` | Assign/unassign incident | ADMIN | `{"assignedTo": 5}` or `{"assignedTo": null}` |
| `GET` | `/api/incidents/{id}/activity` | View incident audit log | Authenticated | Header `Session-Id` |
| `GET` | `/api/users` | List all users | ADMIN | Header `Session-Id` |
| `PUT` | `/api/users/{id}/role` | Update user role | ADMIN | `{"role": "ADMIN"}` or `{"role": "USER"}` |
| `PUT` | `/api/users/{id}/status` | Enable/disable user | ADMIN | `{"status": "true"}` or `{"status": "false"}` |

---

## Database Schema

The database `cybershield` contains three main tables:

```sql
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    status BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS incidents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(30) DEFAULT 'OPEN',
    reported_by INT NOT NULL,
    assigned_to INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES users(id),
    FOREIGN KEY (assigned_to) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS activity_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    incident_id INT NULL,
    user_id INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Feature & Batch Progress

- [x] **Batch 1**: Search and Filters — **COMPLETED**
- [x] **Batch 2**: Incident Details and Timestamps — **COMPLETED**
- [x] **Batch 3**: Dashboard Statistics & Charts — **COMPLETED**
- [x] **Batch 4**: My Incidents View — **COMPLETED**
- [x] **Batch 5**: Server-Side Role-Based Access Control — **COMPLETED**
- [x] **Batch 6**: Admin User Management — **COMPLETED**
- [x] **Batch 7**: Incident Assignment — **COMPLETED**
- [x] **Batch 8**: Automatic Activity / Audit Log — **COMPLETED**

---

## Installation, Compilation & Execution

### 1. Requirements
- Java JDK (Version 17+ recommended)
- MySQL Server & MySQL Workbench
- VS Code & Live Server Extension

### 2. Database Setup
1. Start MySQL Server.
2. Execute `database/database.sql` in MySQL Workbench to create `cybershield` database and tables.
3. Verify connection credentials in `backend/src/main/java/com/cybershield/incidentmanagement/database/DatabaseConnection.java`.

### 3. Compile & Run Java Backend
Open terminal at project root (`CyberShield`):

**Compile:**
```powershell
javac -cp "backend\lib\*" -d backend\out (Get-ChildItem -Recurse backend\src\main\java\*.java).FullName
```

**Run Server:**
```powershell
java -cp "backend\out;backend\lib\*" com.cybershield.incidentmanagement.server.CyberShieldServer
```

The backend server starts on `http://localhost:8080`.

### 4. Serve Frontend
Open `frontend/pages/dashboard.html` or `frontend/index.html` in VS Code, right-click, and select **Open with Live Server**. Access the frontend at `http://127.0.0.1:5500/`.

> [!IMPORTANT]
> Do NOT open HTML files using `file:///` directly in the browser. Serving through Live Server or an HTTP server prevents browser CORS policy issues with header validation.
