# EduTrack - Student Productivity & Social Platform

**EduTrack** is a comprehensive desktop application designed to help university students manage their academic life. It combines productivity tools like Task Management and Pomodoro Timer with social features like Forums and Leaderboards to create a gamified study environment.

This project was developed as part of the **CS-102 Object-Oriented Programming** course.

## 🚀 Features

* **User Authentication:** Secure Login and Register system with email validation.
* **Dashboard:** Overview of user progress, levels, XP, and upcoming tasks.
* **Task Management:** Add, edit, delete, and track tasks (To-Do, In Progress, Completed).
* **Pomodoro Timer:**
    * **Individual Mode:** Customizable study/break intervals.
    * **Group Mode:** Real-time synchronized timer for study groups (Synced via Database Polling).
* **Social Forum:** Course-based discussion threads (e.g., CS 101, CS 102) with real-time-like messaging.
* **Gamification:**
    * **XP & Leveling:** Earn XP by completing tasks and study sessions.
    * **Badges:** Automatic badge unlocking system (e.g., "Early Bird", "Social Butterfly").
    * **Leaderboard:** Compare progress with friends.
* **Profile Management:** Customizable bio, university details, and profile stats.

## 🛠️ Tech Stack & Dependencies

The project is built using **Java 17+** and follows the **MVC (Model-View-Controller)** architectural pattern.

### Core Dependencies
* **JavaFX (17.0.6+):** Used for the Graphical User Interface (GUI).
    * `javafx-controls`
    * `javafx-fxml`
* **MySQL Connector/J (8.0+):** JDBC driver for database connectivity.
* **Apache Maven:** Used for build automation and dependency management.

### Database
* **Remote MySQL Database:** The application connects to a remote database hosted on **Clever Cloud**.
* *Note: No local database setup is required to run the application as it uses the pre-configured remote connection.*

## ⚙️ Installation & Setup

### Prerequisites
1.  **Java Development Kit (JDK):** Version 17 or higher.
2.  **Maven:** Installed and added to your system path.
3.  **IDE:** VS Code with "Extension Pack for Java".

### How to Run the Project

#### Option 1: Running via VS Code or IntelliJ (Recommended)
1.  Clone or download the project repository.
2.  Open the folder in your IDE.
3.  Wait for Maven to download dependencies (check `pom.xml`).
4.  Navigate to:
    `src/main/java/com/edutrack/Launcher.java`
5.  Click **Run** (or press F5).
    * *Note: Always run `Launcher.java` instead of `Main.java` to avoid JavaFX runtime module errors.*

#### Option 2: Running via Terminal (Maven)
1.  Open your terminal/command prompt in the project root directory.
2.  Clean and compile the project:
    ```bash
    mvn clean javafx:run
    ```

## 📂 Project Structure

```text
src/main/java/com/edutrack/
├── controller/       # Handles UI logic (e.g., DashboardController, PomodoroController)
├── dao/              # Data Access Objects for Database operations (e.g., UserDAO, TaskDAO)
├── model/            # POJO classes representing data (e.g., User, Task, Badge)
├── util/             # Helper classes (e.g., DatabaseManager, SessionManager, BadgeService)
├── view/             # FXML files and Assets (Images/Icons)
├── Launcher.java     # Entry point for the JAR
└── Main.java         # JavaFX Application class