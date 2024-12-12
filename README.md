<p align="center">
  <img src = "https://github.com/EdrickaMaePaulos/Attendify-Smart-Attendance-Tracking-System/blob/main/AttendifyFinalProj/logo.gif"/>
</p>
<p align="center">
    <h1 align="center"></h1>
</p>
<p align="center">
    <em><code>An Automated Attendance Management System</code></em>
</p>
<p align="center">
	<b>CS-2202</b><br>
	<a href="https://github.com/EdrickaMaePaulos">Edricka Mae H. Paulos</a><br>
</p>
<hr>

##  Overview
Attendify is a simple Java program that helps track student attendance easily and accurately. It uses a MySQL database to store and organize attendance records for each subject and block.

##  Key Features
1. **Student Attendance Recording:** &nbsp;Students can log their attendance by entering their course code, block, and student ID. Attendance is automatically recorded with a timestamp, and the system determines whether the student is:<br>
&emsp;&emsp;a. **Present:** Checked in within the first 30 minutes of the scheduled start time.<br>
&emsp;&emsp;b. **Late:** Checked in from 31 minutes after the start time until the end of the session.<br>
&emsp;&emsp;c. **Absent:** Did not check in or checked in after the session ended.<br>

2. **Automated Management:** &nbsp;Attendance data is stored in a MySQL database, ensuring secure, centralized, and reliable record-keeping. The system dynamically creates tables for each subject and block, enabling seamless data organization.<br>

3. **Teacher Accessibility:** &nbsp;Teachers can access detailed attendance reports for every session, block, and subject. Features include:<br>
&emsp;- Viewing daily attendance reports.<br>
&emsp;- Managing student records (adding/removing students).<br>
&emsp;- Managing course and block schedules.<br>
4. **Multi-Subject and Multi-Block Support:** &nbsp; Attendify accommodates multiple subjects, each with unique blocks and timeframes, making it versatile for various academic setups.<br>

5. **Efficiency and User Experience:** &nbsp; The console-based interface is intuitive and straightforward, making attendance management fast, efficient, and error-free.<br>

## Application of the 4 Principles of Object-Oriented Programming
### 1. **Encapsulation**
&emsp;&emsp;Implementation:<br>
- &emsp;&emsp;Hides data (e.g., *id*, *name*) and provides access through getter methods for control over how data is accessed.<br>
- &emsp;&emsp;Data (like studentId, blockId, and subjectId) and behavior (methods like listAll() and displayById()) are bundled within their respective classes, and access is controlled using private fields and public methods.<br>
- &emsp;&emsp;Manages admin-specific actions (e.g., adding/removing subjects, blocks, and students) by encapsulating them into methods like addSubject(), removeSubject(), etc., hiding the details of database interactions.<br>

### 2. **Polymophism**
&emsp;&emsp;Implementation:<br>
- &emsp;&emsp;Subclasses override the **toString()** method to provide specific behavior, demonstrating method overriding.<br>
- &emsp;&emsp;The **AttendanceManager**, **BlockManager**, **StudentManager**, and **SubjectManager** classes override the methods *displayOptions()*, *listAll()*, and *displayById()* from the **Displayables interface**.<br>

### 3. **Inheritance**
&emsp;&emsp;Implementation:<br>
- &emsp;&emsp;The Classes **Block**, **Student**, and **Subject** inherit from **BaseClass**<br>
- &emsp;&emsp;The BlockManager, StudentManager, and SubjectManager classes inherit from the Displayables interface, ensuring they share common behavior while implementing their own specific versions of the methods.<br>
- &emsp;&emsp;Inherits functionality from StudentManager, BlockManager, and SubjectManager classes for handling different entities (students, blocks, and subjects) without duplicating code.<br>

### 4. **Abstraction**
&emsp;&emsp;Implementation:<br>
- &emsp;&emsp;**BaseClass** defines common features, hiding details and simplifying usage for subclasses.<br>
- &emsp;&emsp;**Displayables interface** represents an abstraction, defining the structure of display-related functionality without specifying how the functionality is implemented. The concrete classes **(AttendanceManager, BlockManager, StudentManager, SubjectManager)** provide the implementation details.<br>


## Sustainable Development Goal

### 1. **SDG 4: Quality Education** 
- This system helps improve the management of student attendance, making it easier for educators to track and monitor student participation. This supports better educational practices and ensures that all students are fairly and accurately marked for attendance, which contributes to overall quality education.

### 2. **SDG 17: Partnerships for the Goals** 
- This system encourages collaboration among students, teachers, and administrators by bringing them together in one platform. This promotes teamwork and supports the partnership needed to achieve educational goals.

## Tools Used
- &emsp;**IntelliJ IDEA:** The integrated development environment (IDE) used for both programming in Java and managing the MySQL database, providing an all-in-one platform for development, debugging, and database management.

- &emsp;**Java:** The programming language used to build the core functionality of the Attendify application, implementing object-oriented principles for efficient data handling.

- &emsp;**MySQL:** The relational database used for storing and managing subjects, blocks, students, and attendance records, ensuring secure and real-time data operations.

## Installation
1. Clone the repository.
2. Install dependencies.
3. Run the project.

## Project File Structure 
```
src/                           
└── main/
    └── java/
        └── Attendify/ 
        |    ├── models/  
        |    │   ├── BaseClass.java
        |    │   ├── Block.java
        |    │   ├── Student.java
        |    │   ├── Subject.java
        |    │   
        |    ├── AdminMenu.java 
        |    ├── AttendanceManager.java 
        |    ├── AttendanceSystem.java
        |    ├── BlockManager.java 
        |    ├── Displayables.java
        |    ├── Main.java   
        |    ├── StudentMenu.java
        |    └── SubjectManager.java
        └── db/                  
             ├── DatabaseConnection.java 
             ├── init.sql
             └── schema.png         
 
```
##	Database Diagram
<p align="center">
  <img src = "hehehe wait"/>
</p>

##  Acknowledgments

- Ms. Fatima Marie Agdon [Object-Oriented Programming Prof]
- Mr. Owen Patrick Falculan [Database Management System Prof]
- Yosef [aking babyloves so sweet]
- Sa getch() at ang aming ; na nakikinig sa iyak q pag nag-eerror code q

Uno Cutie PLS PLS PAMASKO NA PO. BIRTHDAY Q DIN SA DEC 20!!!
---
