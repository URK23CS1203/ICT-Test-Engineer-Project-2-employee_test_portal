# Employee Test Portal – Clone and Setup Instructions

## GitHub Repository

Repository:
https://github.com/URK23CS1203/ICT-Test-Engineer-Project-2-employee_test_portal.git

---

# 1. Clone the Project

Open IntelliJ IDEA.

Select:

**File → New → Project from Version Control**

Enter:

```text
https://github.com/URK23CS1203/ICT-Test-Engineer-Project-2-employee_test_portal.git
```

Choose a location on your computer and click **Clone**.

Alternatively, clone from the terminal:

```bash
git clone https://github.com/URK23CS1203/ICT-Test-Engineer-Project-2-employee_test_portal.git
cd ICT-Test-Engineer-Project-2-employee_test_portal
```

Open the cloned project in IntelliJ IDEA.

---

# 2. Check Java and Maven

Open the project in IntelliJ and wait for Maven to load.

Make sure the project uses the Java version specified in `pom.xml`.

If Maven dependencies are not loaded:

1. Open the **Maven** panel on the right side of IntelliJ.
2. Click **Reload All Maven Projects** (refresh icon).

---

# 3. Start MySQL

Open **MySQL Workbench**.

Connect to your local MySQL Server.

Make sure the MySQL Server is running.

Open a new SQL tab and run:

```sql
CREATE DATABASE IF NOT EXISTS employee_test_portal;
USE employee_test_portal;
```

If the database already exists, that is OK.

Do NOT run `DROP DATABASE` unless you intentionally want to delete the existing database and its data.

---

# 4. Change MySQL Password in application.properties

In IntelliJ, open:

```text
src/main/resources/application.properties
```

Find the MySQL configuration.

It should look similar to:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_test_portal
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Replace:

```text
YOUR_MYSQL_PASSWORD
```

with the password of your local MySQL `root` user.

Example:

```properties
spring.datasource.password=123456
```

Use your own MySQL password, not the example password.

## Important

Do not commit your real MySQL password to a public GitHub repository.

For a shared/public repository, keep a safe example configuration such as:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_test_portal
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Each person who clones the project should replace `YOUR_MYSQL_PASSWORD` with their own local MySQL password.

---

# 5. Run MySQL Database

In MySQL Workbench, run:

```sql
USE employee_test_portal;
```

Keep MySQL Server running while the Spring Boot application is running.

The project uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Therefore, when the application starts, Hibernate can create/update the required tables based on the entity classes.

---

# 6. Refresh Maven

After opening the cloned project or changing `pom.xml`:

1. Open the **Maven** panel in IntelliJ.
2. Click **Reload All Maven Projects**.

If the project still has dependency/build problems, run:

```text
Maven → Lifecycle → clean
```

and then:

```text
Maven → Lifecycle → install
```

---

# 7. Run the Spring Boot Application

In IntelliJ, find the main Spring Boot application class.

For example:

```text
src/main/java/.../EmployeeTestPortalApplication.java
```

Open the class and click the green **Run ▶** button.

Wait until the console shows that the Spring Boot application has started successfully.

---

# 8. Open the Application in Browser

If the application uses port `8080`, open:

```text
http://localhost:8080/login
```

If a different port is configured in `application.properties`, use that port instead.

Example:

```text
http://localhost:8000/login
```

---

# 9. Complete Setup Order

Always follow this order after cloning:

```text
1. Clone GitHub repository
       ↓
2. Open project in IntelliJ
       ↓
3. Check application.properties
       ↓
4. Change MySQL password
       ↓
5. Open MySQL Workbench
       ↓
6. Start MySQL Server
       ↓
7. Run:
   CREATE DATABASE IF NOT EXISTS employee_test_portal;
   USE employee_test_portal;
       ↓
8. Refresh Maven
       ↓
9. Run Spring Boot application
       ↓
10. Open localhost URL in browser
```

---

# 10. Common Problems

## MySQL Access Denied

If you see:

```text
Access denied for user 'root'
```

Check the password in:

```text
src/main/resources/application.properties
```

Make sure it matches your local MySQL password.

---

## Unknown Database

If you see:

```text
Unknown database 'employee_test_portal'
```

Run:

```sql
CREATE DATABASE IF NOT EXISTS employee_test_portal;
USE employee_test_portal;
```

Then restart the Spring Boot application.

---

## Maven Dependencies Not Found

Open the Maven panel and click:

```text
Reload All Maven Projects
```

If necessary, run:

```text
clean
install
```

from Maven Lifecycle.

---

## Port Already in Use

If port `8080` is already being used, either stop the other application or change the port in:

```properties
server.port=8080
```

For example:

```properties
server.port=8081
```

Then open:

```text
http://localhost:8081/login
```

---

# Quick Commands

## Clone

```bash
git clone https://github.com/URK23CS1203/ICT-Test-Engineer-Project-2-employee_test_portal.git
```

## Enter Project

```bash
cd ICT-Test-Engineer-Project-2-employee_test_portal
```

## Git commands for the project owner when pushing changes

```bash
git add .
git commit -m "Update Employee Test Portal"
git push
```

---

# MySQL Commands

```sql
CREATE DATABASE IF NOT EXISTS employee_test_portal;
USE employee_test_portal;
```

---

# Application Configuration

File:

```text
src/main/resources/application.properties
```

Set your local MySQL password:

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Then:

**Start MySQL → Refresh Maven → Run Spring Boot → Open localhost in browser.**
