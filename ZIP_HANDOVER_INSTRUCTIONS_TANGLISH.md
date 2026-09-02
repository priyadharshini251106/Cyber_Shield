# CyberShield Incident Management – ZIP Handover Instructions

## 1. Project-a ZIP-la receive pannina apram enna panna?

Indha document, **CyberShield Incident Management** project-a ZIP file-aa receive pannra person-ku step-by-step setup instructions.

Project-la:

- Frontend → HTML, CSS, JavaScript (Vanilla JS, Fetch API)
- Backend → Core Java `com.sun.net.httpserver.HttpServer`
- Database → MySQL
- Database connection → JDBC (`PreparedStatement`)
- API → REST API
- Session → In-memory session management (`SessionManager`)
- Security → Server-side Role-Based Access Control (`USER`, `ADMIN`)
- Framework → Spring Boot use pannala (No Spring / Hibernate / JPA / JWT)
- Frontend server → VS Code Live Server recommend pannappadudhu (`http://127.0.0.1:5500`)

---

# 2. Required Software

ZIP extract panna munnaadi / setup panna munnaadi indha software irukkanum:

### Java

Java JDK install pannirukkanum (Version 17+ recommended).

Check:

```powershell
java -version
javac -version
```

Rendu command-um version show panna vendum.

### MySQL

MySQL Server install and running-a irukkanum.

MySQL Workbench use pannalaam.

### VS Code

Project-a VS Code-la open pannunga.

### Live Server

VS Code-la **Live Server** extension install pannina frontend easy-aa run pannalaam.

---

# 3. ZIP File Extract Pannunga

ZIP file-a extract pannitu:

```text
CyberShield
```

main project folder-a VS Code-la open pannunga.

Expected structure:

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
│                           ├── database
│                           ├── entity
│                           ├── repository
│                           ├── service
│                           └── server
│
├── frontend
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── css
│   ├── js
│   └── pages
│
└── database
    └── database.sql
```

`sql.jar` (or `mysql-connector-j-9.x.x.jar`) file `backend/lib` inside irukkanum.

---

# 4. Database Setup

First MySQL Server start pannunga.

MySQL Workbench open pannunga.

`database/database.sql` file-a open pannunga.

Andha SQL script-a execute pannunga.

Database and tables create aagum.

Main tables:

```text
users
incidents
activity_logs
```

### Table 1: `users`
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
```

### Table 2: `incidents`
```sql
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
```

### Table 3: `activity_logs`
```sql
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

# 5. Database Connection Check Pannunga

Open:

```text
backend/src/main/java/com/cybershield/incidentmanagement/database/DatabaseConnection.java
```

Indha file-la:

- database name (`cybershield`)
- username (`root`)
- password (`root`)
- host (`localhost`)
- port (`3306`)

correct-aa irukka check pannunga.

Example format:

```text
jdbc:mysql://localhost:3306/cybershield
```

Username/password unga local MySQL setup-ku match aaganum.

**Important:** ZIP handover panna munnaadi real database password share panna koodadhu. Handover person than local MySQL credentials set pannattum.

---

# 6. Default Admin Account

`DatabaseInitializer.java` server start aagumbodhu default admin account check/create pannum:

- **Email**: `admin@cybershield.com`
- **Password**: `Admin@12345`
- **Role**: `ADMIN`

Indha account protected; user-management UI moolama role alter panna mudiyadhu.

---

# 7. Backend Compile and Run Pannunga

VS Code terminal open pannunga.

Terminal current location project root:

```text
CyberShield
```

nu confirm pannunga.

PowerShell use pannina:

### Compile Command:
```powershell
javac -cp "backend\lib\*" -d backend\out (Get-ChildItem -Recurse backend\src\main\java\*.java).FullName
```

### Run Command:
```powershell
java -cp "backend\out;backend\lib\*" com.cybershield.incidentmanagement.server.CyberShieldServer
```

Compile success aana backend server start aagum:

```text
http://localhost:8080
```

Terminal close panna koodadhu. Backend running-a irukkanum.

---

# 8. Backend Test Pannunga

Browser-la open:

```text
http://localhost:8080/api/test
```

Success response varanum.

Dashboard API check panna:

```text
http://localhost:8080/api/dashboard
```

Example:

```json
{
    "total": 5,
    "open": 4,
    "inProgress": 0,
    "resolved": 1,
    "critical": 1
}
```

---

# 9. Frontend Run Pannunga

`frontend` folder-la required HTML file open pannunga.

Best method:

1. VS Code-la `dashboard.html` or `index.html` open pannunga.
2. Right-click pannunga.
3. **Open with Live Server** select pannunga.

Browser URL `file:///...` madhiri irukka koodadhu.

Prefer:

```text
http://127.0.0.1:5500/...
```

or:

```text
http://localhost:5500/...
```

---

# 10. CORS & Authentication Headers

Frontend backend API-ku request send pannumbodhu `Session-Id` header use pannum.

Backend CORS header set pannirukkum:

```java
exchange.getResponseHeaders().set(
    "Access-Control-Allow-Headers",
    "Content-Type, Session-Id"
);
```

---

# 11. Implemented Features & Endpoints

| Feature | Method | Endpoint | Access |
|---|---|---|---|
| User Register | `POST` | `/api/register` | Public |
| User Login | `POST` | `/api/login` | Public |
| User Logout | `POST` | `/api/logout` | Authenticated |
| User Profile | `GET` | `/api/profile` | Authenticated |
| List Incidents | `GET` | `/api/incidents` | Authenticated |
| Create Incident | `POST` | `/api/incidents` | Authenticated |
| View Incident Details | `GET` | `/api/incidents/{id}` | Authenticated |
| Update Incident | `PUT` | `/api/incidents/{id}` | ADMIN |
| Delete Incident | `DELETE` | `/api/incidents/{id}` | ADMIN |
| Incident Assignment | `PUT` | `/api/incidents/{id}/assignment` | ADMIN |
| Incident Activity Log | `GET` | `/api/incidents/{id}/activity` | Authenticated |
| User Management | `GET` | `/api/users` | ADMIN |
| User Role Change | `PUT` | `/api/users/{id}/role` | ADMIN |
| User Status Change | `PUT` | `/api/users/{id}/status` | ADMIN |
| Dashboard Stats | `GET` | `/api/dashboard` | Authenticated |

---

# 12. Complete Testing & Verification Steps

1. **Register User**: Register a new user (`USER` role assigned automatically).
2. **Login as USER**: Login with registered credentials.
3. **Create Incident**: Report incident as user. Check audit log created.
4. **My Incidents**: Click *My Incidents* button to view owned incidents.
5. **Login as ADMIN**: Login using `admin@cybershield.com` / `Admin@12345`.
6. **Assign Incident**: Go to Incident Details page, select a user from dropdown, click **Assign**.
7. **Verify Non-Admin Block**: Standard user calling assignment API returns `403 Forbidden`.
8. **Unassign Incident**: Click **Unassign** button as admin.
9. **Update & Delete Incident**: Edit severity/status or delete incident as admin.
10. **Activity Log View**: Verify activity trail (`CREATED`, `UPDATED`, `ASSIGNED`, `UNASSIGNED`, `DELETED`) on Incident Details page.
11. **User Management**: Go to `users.html` as admin to promote/demote roles or disable/enable accounts.

---

# 13. Final Handover Checklist

- [x] Complete `CyberShield` folder included.
- [x] `backend/lib` contains `sql.jar` (MySQL Connector).
- [x] `database/database.sql` contains `users`, `incidents`, and `activity_logs` tables.
- [x] Default Admin setup (`admin@cybershield.com` / `Admin@12345`).
- [x] Backend compiles clean with zero errors.
- [x] All 8 Batches (Search, Details, Dashboard, My Incidents, RBAC, User Management, Incident Assignment, Audit Log) completed.
- [x] Server-side `403 Forbidden` checks enforced.
- [x] Live Server frontend execution verified.
