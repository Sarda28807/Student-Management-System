
<div align="center">
  
  <br />
  
  <h1>NovaStudent</h1>
  <a href="https://git.io/typing-svg">
    <img src="https://readme-typing-svg.herokuapp.com?font=Fira+Code&weight=600&size=20&pause=1000&color=22D3EE&center=true&vCenter=true&width=435&lines=Smart+Student+Management+System;Built+with+JavaFX+Glassmorphism;Powered+by+MySQL+8.0" alt="Typing SVG" />
  </a>
  
  <p>
    A high-performance, enterprise-grade university administration platform built with a strictly typed Java backend and a futuristic JavaFX interface.
  </p>

  <p>
    <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white" alt="Java" />
    <img src="https://img.shields.io/badge/JavaFX-17.0.2-22668D?style=for-the-badge" alt="JavaFX" />
    <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
    <img src="https://img.shields.io/badge/Maven-3.8-C71A22?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
  </p>

</div>

<br />

## Project Overview

NovaStudent is designed to resolve the administrative bottleneck of managing student lifecycles, attendance, and academic results. Moving away from traditional legacy interfaces, this application introduces a modern, high-fidelity **Glassmorphism** aesthetic while maintaining robust data integrity through a highly normalized backend infrastructure.

---

## Architectural Workflow

The application follows a rigorous **Layered Model-View-Controller (MVC)** architecture. This ensures a clean separation of concerns, providing high cohesion, low coupling, and inherent scalability.

<div align="center">
  <img src="assets/architecture_workflow.png" alt="Architecture Workflow" width="700" />
</div>

<br />

### Layer Breakdown

1. **Presentation Layer (UI):** Programmatically generated JavaFX components utilizing a central factory pattern for consistency, avoiding FXML constraints.
2. **Controller Layer:** Bridges user interactions and the underlying logic without containing direct database queries.
3. **Service Layer:** Enforces business constraints, manages transaction boundaries, and validates payload integrity.
4. **Data Access Layer (DAO):** Pure JDBC implementation leveraging `PreparedStatement` to mitigate SQL injection vulnerabilities.
5. **Database Engine:** MySQL 8.0 acting as the single source of truth, heavily relying on constraints, cascaded deletions, and aggregate views.

---

## Core Capabilities

<table align="center">
  <tr>
    <td width="50%">
      <strong>Dynamic Dashboard</strong><br />
      Real-time metrics aggregating active students, daily attendance rates, departmental distribution, and GPA spread.
    </td>
    <td width="50%">
      <strong>Academic Intelligence</strong><br />
      Database-level triggers auto-calculate GPA metrics on a strict 10-point academic scale immediately upon data entry.
    </td>
  </tr>
  <tr>
    <td width="50%">
      <strong>Smart Attendance Tracking</strong><br />
      Bulk processing interfaces for rapid attendance logging, paired with historical trend analysis charts.
    </td>
    <td width="50%">
      <strong>Automated Reporting</strong><br />
      Integration with iText 7 to dynamically generate and export formatted PDF reports and CSV datasets.
    </td>
  </tr>
</table>

---

## Technical Specifications

### Data Integrity & Security
* **Password Encryption:** Integration with `jBCrypt` applying a high work factor for robust password hashing.
* **Database Triggers:** `BEFORE INSERT` and `BEFORE UPDATE` mechanisms applied natively in MySQL to compute dynamic grades.
* **Asynchronous Processing:** Heavy I/O operations and database queries are shifted to background daemon threads to guarantee 60fps UI performance.

---

## Initialization Instructions

### Prerequisites
* JDK 17 or higher
* Apache Maven 3.6+
* MySQL Server 8.0+

### Database Provisioning
Connect to your local MySQL instance and execute the initialization scripts:
```sql
source database/schema.sql
source database/sample_data.sql
```

### Environment Configuration
Update the internal configuration file `config.properties` located in the root directory to match your environment:
```properties
db.url=jdbc:mysql://localhost:3306/novastudent_db
db.username=root
db.password=your_secure_password
```

### Build and Execute
Deploy the application via Maven:
```bash
mvn clean compile javafx:run
