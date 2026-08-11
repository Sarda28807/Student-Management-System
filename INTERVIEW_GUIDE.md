# NovaStudent: Interview Preparation Guide

When presenting this project in a software engineering interview, interviewers will likely ask specific questions about your design choices, database knowledge, and Java fundamentals based on this code. Use this guide to prepare.

---

### Q1: "Why did you choose a Layered Architecture (Controller -> Service -> DAO) instead of putting all the logic in the Controller?"
**How to Answer:** 
"I used a layered architecture to adhere to the Single Responsibility Principle and ensure separation of concerns. If I put SQL queries directly in the UI controllers, the code becomes tightly coupled and impossible to unit test. By separating it into DAOs (Database Access), Services (Business Logic like validation), and Controllers (UI handling), I can easily swap out the UI or the Database in the future without rewriting the core business rules."

### Q2: "I see you used PreparedStatement in your DAO classes. Why not just use a standard Statement and concatenate strings?"
**How to Answer:** 
"Using string concatenation for SQL queries (e.g., `"SELECT * FROM users WHERE username = '" + user + "'"`) makes the application highly vulnerable to SQL Injection attacks. `PreparedStatement` pre-compiles the SQL query and treats the input parameters strictly as data, not executable code. Additionally, `PreparedStatement` is faster when executing the same query multiple times because the database caches the execution plan."

### Q3: "How do you handle database connections? Did you use a connection pool?"
**How to Answer:**
"For this project, I used the Singleton Design Pattern for the `DatabaseConnection` class to ensure only one instance of the connection manager exists. *However*, for a production environment under heavy load, I would upgrade this to use a Connection Pool like HikariCP. Connection pooling maintains a cache of database connections that can be reused, which prevents the massive overhead of opening and closing a TCP connection to the database for every single query."

### Q4: "You mentioned using BCrypt for passwords. Why is BCrypt better than MD5 or SHA-256?"
**How to Answer:**
"MD5 and standard SHA algorithms are designed to be extremely fast. While that's good for verifying file integrity, it's terrible for passwords because hackers can compute billions of hashes per second using GPUs to crack them via brute force or rainbow tables. BCrypt is specifically designed to be *slow* (key stretching) and includes a 'salt' by default. You can also configure its 'work factor' to make it even slower as hardware gets faster, keeping the passwords secure."

### Q5: "How did you ensure the UI didn't freeze when loading large amounts of data?"
**How to Answer:**
"JavaFX, like most UI frameworks, operates on a single 'Application Thread'. If you run a heavy database query on this thread, the UI freezes. To fix this, I wrapped all my database calls in separate background threads (e.g., `new Thread(() -> { ... }).start()`). Once the data was retrieved, I used `Platform.runLater()` to safely pass that data back to the main UI thread to update the tables and charts."

### Q6: "Why did you use Triggers and Generated Columns in MySQL instead of calculating grades in Java?"
**How to Answer:**
"I used a Generated Column for `total_marks` and Triggers for `calculate_grade` to ensure data integrity at the database level. If another application (like a web portal) connects to this same database, I want to guarantee that the grading logic remains identical. By pushing this specific logic down to the database, it acts as a single, undeniable source of truth, rather than relying on every client application to implement the math correctly."

### Q7: "What was the most challenging part of this project?"
**How to Answer:**
*(Customize this based on your experience. A good technical answer is below:)*
"The most challenging part was designing the custom Glassmorphism UI without relying on external UI libraries or FXML. I had to learn how to manipulate JavaFX CSS deeply—using linear gradients, drop shadows, and semi-transparent RGBA backgrounds—and programmatically generate complex layouts using a `GlassComponents` factory to ensure the code remained clean and reusable."
