# Proper Instructions to Priya – CyberShield Understanding Guide

## 1. Project-na Simple-aa Understand Pannanum

Priya, indha project peru:

**CyberShield Incident Management**

Idhu oru beginner-friendly Java full-stack project.

Main purpose:

> Users security incidents-a report panna, view panna, update panna, delete panna, and dashboard-la incident statistics paakka use aagum.

Project intentionally simple-aa build pannirukkom.

Spring Boot use pannala.

Instead:

```text
HTML
CSS
JavaScript
   ↓
Core Java HTTP Server
   ↓
JDBC
   ↓
MySQL
```

---

# 2. Project-la Enna Technologies Use Pannirukkom?

## Frontend

Use pannirukkiradhu:

- HTML
- CSS
- JavaScript
- Fetch API
- Local Storage

Frontend user interface handle pannum.

Example:

```text
Login Page
Register Page
Dashboard
Incident Page
Profile Page
```

---

## Backend

Backend-ku normal Core Java use pannirukkom.

Main HTTP server:

```java
com.sun.net.httpserver.HttpServer
```

Spring Boot illa.

Backend request receive pannum:

```text
POST /api/login
GET /api/dashboard
POST /api/incidents
```

etc.

---

## Database

MySQL use pannirukkom.

Java → JDBC → MySQL

connection handle pannradhu:

```text
DatabaseConnection.java
```

---

# 3. Folder Structure-a Understand Pannu

Main folder:

```text
CyberShield
```

Inside:

```text
backend
frontend
database
```

---

# 4. Backend Folder

Backend-la main Java code irukkum.

```text
backend
└── src
    └── main
        └── java
            └── com
                └── cybershield
                    └── incidentmanagement
```

Inside different packages irukkum.

---

## controller

Controller API requests handle pannum.

Example:

```text
LoginController.java
RegisterController.java
IncidentController.java
DashboardController.java
```

For example:

```text
GET /api/dashboard
```

request vandha:

```text
DashboardController
```

handle pannum.

---

## database

```text
DatabaseConnection.java
```

Java application MySQL-kku connect aaga indha file use aagum.

Simple flow:

```text
Java
 ↓
DatabaseConnection
 ↓
MySQL
```

---

## entity

Entity classes database data-va represent pannum.

Main classes:

```text
User.java
Incident.java
```

Example:

```text
Incident
 ├── id
 ├── title
 ├── description
 ├── category
 ├── severity
 ├── status
 └── reportedBy
```

---

## repository

Repository database operations handle pannum.

Main:

```text
UserRepository.java
IncidentRepository.java
```

Incident repository-la:

```text
CREATE
READ
UPDATE
DELETE
```

operations irukkum.

---

## service

Business logic inga irukkum.

Example:

```text
UserService.java
IncidentService.java
SessionManager.java
```

SessionManager simple-aa:

```text
Session ID → User Email
```

map maintain pannum.

---

## server

```text
CyberShieldServer.java
```

Idhu main backend server.

Application start pannumbodhu indha class run aagum.

Simple flow:

```text
CyberShieldServer
       ↓
Controllers
       ↓
Services
       ↓
Repositories
       ↓
MySQL
```

---

# 5. Frontend Folder

Frontend-la pages and JavaScript irukkum.

```text
frontend
├── index.html
├── login.html
├── register.html
├── css
├── js
└── pages
```

---

## Main Pages

```text
index.html
```

Landing/home page.

```text
login.html
```

User login.

```text
register.html
```

New user registration.

```text
pages/dashboard.html
```

Main dashboard.

```text
pages/incidents.html
```

Incident list.

```text
pages/create-incident.html
```

New incident create panna.

```text
pages/profile.html
```

User profile.

---

# 6. JavaScript Files

Important files:

```text
login.js
register.js
dashboard.js
incidents.js
create-incident.js
profile.js
theme.js
```

Each page-kku related JavaScript functionality handle pannum.

Example:

```text
dashboard.html
      ↓
dashboard.js
      ↓
GET /api/dashboard
      ↓
Java backend
```

---

# 7. Database Folder

```text
database
└── database.sql
```

Indha file database setup-ku use pannuvom.

Main tables:

```text
users
incidents
```

---

# 8. Complete Application Flow

Full project flow-a ipdi remember pannunga:

```text
USER
 ↓
HTML Page
 ↓
JavaScript
 ↓
Fetch API
 ↓
Java HTTP Server
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
JDBC
 ↓
MySQL
```

Response reverse direction-la varum:

```text
MySQL
 ↓
Repository
 ↓
Service
 ↓
Controller
 ↓
JavaScript
 ↓
HTML
 ↓
USER
```

Idhu dhaan project-oda most important architecture.

---

# 9. Registration Flow

User:

```text
Register Page
```

open pannuvanga.

Details enter pannuvanga:

```text
Name
Email
Password
```

JavaScript:

```text
POST /api/register
```

send pannum.

Backend:

```text
RegisterController
      ↓
UserService
      ↓
UserRepository
      ↓
MySQL
```

User database-la save aagum.

Registration success aana login page-ku redirect aagum.

Entered email login page-la prefill aagum.

---

# 10. Login Flow

Login page-la:

```text
Email
Password
```

enter pannuvom.

Request:

```text
POST /api/login
```

Backend user details verify pannum.

Success aana session create pannum.

Simple concept:

```text
Session ID
    ↓
User Email
```

browser-la:

```text
sessionId
userEmail
```

store pannuvom.

Then dashboard open aagum.

---

# 11. Dashboard Flow

Dashboard open aagumbodhu:

```text
dashboard.js
```

run aagum.

Adhu:

```text
GET /api/dashboard
```

request send pannum.

Request-la:

```text
Session-Id
```

header send pannum.

Backend database-la incidents count pannum.

Example:

```text
Total = 4
Open = 4
In Progress = 0
Resolved = 0
Critical = 1
```

Response JavaScript-ku varum.

Then HTML values update aagum.

---

# 12. Dashboard Statistics Eppadi Calculate Aagudhu?

Backend `DashboardController.java` use pannum.

### Total

```sql
SELECT COUNT(*) FROM incidents
```

### Open

```sql
SELECT COUNT(*)
FROM incidents
WHERE status = 'OPEN'
```

### In Progress

```sql
SELECT COUNT(*)
FROM incidents
WHERE status = 'IN_PROGRESS'
```

### Resolved

```sql
SELECT COUNT(*)
FROM incidents
WHERE status = 'RESOLVED'
```

### Critical

```sql
SELECT COUNT(*)
FROM incidents
WHERE severity = 'CRITICAL'
```

So dashboard-la numbers hardcoded illa.

Database based-aa dynamically calculate aagum.

---

# 13. Incident Create Flow

User:

```text
Report Incident
```

click pannuvanga.

Form:

```text
Title
Description
Category
Severity
Status
```

fill pannuvanga.

JavaScript:

```text
POST /api/incidents
```

send pannum.

Current logged-in user's ID profile API moolama retrieve pannitu incident-la `reportedBy` use pannuvom.

Backend:

```text
IncidentController
      ↓
IncidentService
      ↓
IncidentRepository
      ↓
MySQL
```

incident save aagum.

---

# 14. Incident CRUD

Incident management-la four main operations:

```text
CREATE
READ
UPDATE
DELETE
```

### Create

```text
POST /api/incidents
```

### Read All

```text
GET /api/incidents
```

### Read One

```text
GET /api/incidents/{id}
```

### Update

```text
PUT /api/incidents/{id}
```

### Delete

```text
DELETE /api/incidents/{id}
```

---

# 15. Important Database Relationship

`incidents` table-la:

```text
reported_by
assigned_to
```

user table-kku connected.

Concept:

```text
users
  |
  | id
  ↓
incidents.reported_by
```

So incident yaar report pannanga-nu identify panna mudiyum.

---

# 16. Why `Session-Id` Important?

Login pannina apram backend user-a identify panna session use pannuvom.

Frontend request:

```text
Session-Id: <current session>
```

send pannum.

Backend session manager:

```text
Session ID
    ↓
User Email
```

find pannum.

Then logged-in user details retrieve panna mudiyum.

---

# 17. CORS Problem – Important

Frontend and backend different ports-la run aagudhu.

Example:

```text
Frontend
http://127.0.0.1:5500

Backend
http://localhost:8080
```

Browser security reason nala CORS configuration required.

Particularly dashboard request-la:

```text
Session-Id
```

custom request header.

So backend-la:

```java
Access-Control-Allow-Headers
```

inside:

```text
Content-Type, Session-Id
```

irukkanum.

Illana browser:

```text
Request header field session-id is not allowed
```

nu error kudukkum.

---

# 18. `file://` Use Pannakoodadhu

HTML file-a direct-aa open panna:

```text
file:///D:/CyberShield/...
```

browser origin `null` aa treat pannum.

Idhu CORS/security issues create pannalaam.

So frontend-ku:

**Live Server** use pannunga.

Example:

```text
http://127.0.0.1:5500/
```

---

# 19. Backend Run Pannradhu

VS Code terminal-la project root:

```text
CyberShield
```

irukkumbodhu PowerShell-la:

```powershell
javac -cp "backend\lib\*" -d backend\out (Get-ChildItem -Recurse backend\src\main\java\*.java).FullName; if ($?) { java -cp "backend\out;backend\lib\*" com.cybershield.incidentmanagement.server.CyberShieldServer }
```

run pannunga.

Backend:

```text
http://localhost:8080
```

la run aagum.

Terminal close panna koodadhu.

---

# 20. Backend Test

Browser-la:

```text
http://localhost:8080/api/test
```

open pannunga.

Backend working-aa irukka check panna idhu easiest test.

Dashboard API:

```text
http://localhost:8080/api/dashboard
```

open pannunga.

Correct JSON response varanum.

---

# 21. Database Test

MySQL-la:

```sql
SELECT * FROM incidents;
```

run pannunga.

Incident records irukka check pannunga.

Status:

```sql
SELECT status, COUNT(*)
FROM incidents
GROUP BY status;
```

Severity:

```sql
SELECT severity, COUNT(*)
FROM incidents
GROUP BY severity;
```

---

# 22. Dashboard 0 Problem Vandha Enna Pannanum?

Dashboard-la:

```text
Total = 0
Open = 0
...
```

nu vandha immediately frontend problem-nu assume pannadheenga.

First:

```text
http://localhost:8080/api/dashboard
```

open pannunga.

### API correct numbers kudutha:

Frontend issue.

Check:

```text
dashboard.js
```

### API-um zero kudutha:

Database/backend issue.

Check:

```sql
SELECT * FROM incidents;
```

---

# 23. Theme Feature

Project-la light/dark mode irukku.

Theme JavaScript:

```text
theme.js
```

use pannum.

Selected theme:

```text
localStorage
```

la save aagum.

So page change pannalum theme preference maintain aagum.

---

# 24. Enter Key Feature

Forms-la Enter press pannumbodhu next field-ku move aagura behavior implement pannirukkom.

So:

```text
Input 1
   ↓ Enter
Input 2
   ↓ Enter
Input 3
```

madhiri navigate pannalaam.

---

# 25. Registration → Login Feature

Registration success aana:

```text
registeredEmail
```

temporary-aa localStorage-la store pannuvom.

Login page open aana email automatic-aa fill aagum.

Password field focus aagum.

Flow:

```text
Register
   ↓
Success
   ↓
Login
   ↓
Email already filled
   ↓
Password focus
```

---

# 26. ZIP Handover Panna Enna Pannanum?

Project-a ZIP-aa share pannumbodhu:

```text
CyberShield.zip
```

inside full project structure irukkanum.

Important:

```text
backend/lib/mysql-connector-j-9.x.x.jar
database/database.sql
frontend/
backend/src/
```

miss aaga koodadhu.

Real MySQL password public ZIP-la share pannadheenga.

---

# 27. New Computer-la Setup Order

New system-la indha order follow pannunga:

```text
1. Install Java JDK
        ↓
2. Install MySQL
        ↓
3. Install VS Code
        ↓
4. Install Live Server
        ↓
5. Extract CyberShield ZIP
        ↓
6. Open project in VS Code
        ↓
7. Start MySQL
        ↓
8. Run database.sql
        ↓
9. Check DatabaseConnection.java
        ↓
10. Start Java backend
        ↓
11. Test /api/test
        ↓
12. Start frontend using Live Server
        ↓
13. Register
        ↓
14. Login
        ↓
15. Create Incident
        ↓
16. Check Dashboard
```

---

# 28. Project-a Explain Panna Interview-la

Interview-la simple-aa ipdi explain pannalaam:

> "CyberShield Incident Management is a Java full-stack application developed for managing cybersecurity incidents. The frontend is built using HTML, CSS and JavaScript. The backend uses Core Java's HttpServer with REST APIs, and JDBC is used to communicate with MySQL. Users can register, login, create incidents, view, update and delete incidents. The dashboard dynamically displays total, open, in-progress, resolved and critical incidents based on database records. I also implemented simple session management, CORS handling and a light/dark theme."

---

# 29. Architecture-a Interview-la Explain Panna

Simple-aa:

```text
Frontend
HTML + CSS + JavaScript
        ↓
REST API
        ↓
Core Java HTTP Server
        ↓
Controller
        ↓
Service
        ↓
Repository
        ↓
JDBC
        ↓
MySQL
```

Indha architecture purinjirundha project explain panna easy.

---

# 30. Most Important Files to Remember

Project full-aa memorize panna thevai illa.

First indha files purpose mattum understand pannunga:

```text
CyberShieldServer.java
→ Backend server start pannum

DatabaseConnection.java
→ MySQL connection

User.java
→ User data

Incident.java
→ Incident data

UserRepository.java
→ User DB operations

IncidentRepository.java
→ Incident DB operations

SessionManager.java
→ Login session manage pannum

LoginController.java
→ Login API

RegisterController.java
→ Register API

IncidentController.java
→ Incident APIs

DashboardController.java
→ Dashboard statistics

dashboard.js
→ Dashboard API call + numbers update

create-incident.js
→ Incident creation

theme.js
→ Light/Dark mode
```

---

# 31. Final Mental Model

Indha one flow-a remember pannunga:

```text
User
 ↓
Website
 ↓
JavaScript
 ↓
API
 ↓
Java Controller
 ↓
Service
 ↓
Repository
 ↓
JDBC
 ↓
MySQL
```

Database-la data change aana:

```text
MySQL
 ↓
API Response
 ↓
JavaScript
 ↓
Dashboard
```

So dashboard numbers database-la irukkura actual incident records based on dynamically update aagum.

---

# 32. Final Checklist

Project run pannumbodhu:

- [ ] MySQL running
- [ ] Database created
- [ ] `DatabaseConnection.java` correct
- [ ] MySQL connector JAR present
- [ ] Backend compiled
- [ ] Backend running on port 8080
- [ ] `/api/test` works
- [ ] `/api/dashboard` returns JSON
- [ ] Frontend opened using Live Server
- [ ] Register works
- [ ] Login works
- [ ] Session ID exists
- [ ] Incident creation works
- [ ] Incident appears in MySQL
- [ ] Dashboard counts update
- [ ] Logout works
- [ ] Theme toggle works

**Main concept:** Frontend just displays the data. Backend calculates and retrieves the data. MySQL stores the actual data.
