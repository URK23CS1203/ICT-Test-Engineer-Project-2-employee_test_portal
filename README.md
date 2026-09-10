# EmployeeTestPortal

Small Spring Boot full-stack application for the 4-stage practical software testing course.

## Environment
- JDK 23
- Spring Boot 4.1.1
- IntelliJ IDEA Community 2024.3.5
- Maven
- MySQL 8.0
- MySQL Workbench 8.0
- Chrome
- Postman

## Initializr dependencies
- Spring Web
- Thymeleaf
- Spring Security
- Spring Data JPA
- Validation
- MySQL Driver
- Lombok
- Spring Boot DevTools
- Spring Boot Starter Test

## Database
CREATE DATABASE employee_test_portal;

Edit `src/main/resources/application.properties` and replace `YOUR_MYSQL_PASSWORD`.

## Run
Open the project in IntelliJ and run `EmployeeTestPortalApplication`.

URL: http://localhost:8080/login

## Demo users
admin / Admin@123
employee1 / Employee@123

## UI pages
- /login
- /dashboard
- /employees
- /leaves
- /attendance

## REST APIs
- GET /api/employees
- POST /api/employees
- PUT /api/employees/{id}/deactivate
- PUT /api/employees/{id}/activate
- GET /api/leaves
- POST /api/leaves
- PUT /api/leaves/{id}/approve
- PUT /api/leaves/{id}/reject
- GET /api/attendance
- POST /api/attendance

## Important training note
CSRF is disabled only to keep this small training project and Postman flow simple. A production web application should use appropriate CSRF and API authentication protections.

Testing layers are added later under `src/test` and `postman/` without removing the application.


## Role-Based Authorization

The application contains two training users:

- `admin / Admin@123` → `ROLE_ADMIN`
- `employee1 / Employee@123` → `ROLE_EMPLOYEE`

### Browser authorization

| Operation | ADMIN | EMPLOYEE |
|---|---:|---:|
| Login | YES | YES |
| Dashboard | YES | YES |
| View Employees | YES | YES |
| Add Employee | YES | NO |
| Deactivate Employee | YES | NO |
| Activate Employee | YES | NO |
| View Leave | YES | YES |
| Apply Leave | YES | YES |
| Approve Leave | YES | NO |
| Reject Leave | YES | NO |
| View Attendance | YES | YES |
| Mark Attendance | YES | YES |

The UI hides ADMIN-only actions for employees, and Spring Security independently protects the backend URLs. If an employee manually requests an ADMIN-only URL, Spring Security sends the request to `/access-denied`.

### REST authorization

| HTTP operation | ADMIN | EMPLOYEE |
|---|---:|---:|
| GET `/api/employees` (directory) | YES | YES |
| GET `/api/leaves` (own requests) | YES | YES
| GET `/api/attendance` (own records) | YES | YES |
| POST `/api/employees` | YES | NO |
| PUT `/api/employees/{id}/deactivate` | YES | NO |
| PUT `/api/employees/{id}/activate` | YES | NO |
| POST `/api/leaves` | YES | YES |
| PUT `/api/leaves/{id}/approve` | YES | NO |
| PUT `/api/leaves/{id}/reject` | YES | NO |
| POST `/api/attendance` | YES | YES |

HTTP Basic authentication is enabled in addition to browser form login so the REST APIs can be tested in Postman with either training user's credentials.


## Employee Activate / Deactivate

Employee lifecycle uses a soft-deactivation model rather than normal hard deletion.

- `Employee.active = true/false` controls employment availability.
- A linked user's `enabled` flag is synchronized when an administrator activates or deactivates the employee.
- Historical leave and attendance records are preserved.
- ADMIN can activate or deactivate employees from the browser or REST API.
- The normal employee UI no longer exposes hard delete.

### Employee status history

Every activation/deactivation creates an `EmployeeStatusHistory` record containing the old status, new status, administrator username, timestamp, and reason.

## User-Employee Relationship

Each EMPLOYEE login is explicitly linked to one Employee record through `users.employee_id`.
The `employee1` login is linked to employee code `EMP001`.

Attendance ownership uses this relationship. EMPLOYEE can mark only the linked employee's attendance; ADMIN can mark attendance for any employee.

## Ownership and Business Rules

- EMPLOYEE can view only their own leave requests and attendance records.
- EMPLOYEE can apply leave only for the Employee record linked to their login.
- EMPLOYEE can mark attendance only for the Employee record linked to their login.
- ADMIN can view all leave and attendance records and can act on any active employee.
- Rejected and cancelled leave requests do not block a new overlapping request; pending and approved requests do.
- Attendance dates cannot be in the future.
- New attendance and leave submissions are rejected for inactive employees.
- Leave approval records the approving username and timestamp. Leave rejection records the rejecting username and timestamp.
- Employee status changes are recorded in `EmployeeStatusHistory`.
- Normal employee management uses Activate/Deactivate rather than hard delete, preserving historical records.

## Existing Database Note

This version uses `spring.jpa.hibernate.ddl-auto=update`. When upgrading an existing database, verify that existing employees have the intended `active` value. Do not manually set an employee inactive unless the linked account should also be prevented from logging in.
