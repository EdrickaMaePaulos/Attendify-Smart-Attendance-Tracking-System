-- Use the attendifydatabase
USE attendifydatabase;

-- Drop tables if they exist
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS blocks;
DROP TABLE IF EXISTS subjects;

-- Create subjects table
CREATE TABLE IF NOT EXISTS subjects (
    subject_id VARCHAR(100) PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL
);

-- Create blocks table with foreign key reference to subjects
CREATE TABLE IF NOT EXISTS blocks (
    block_id VARCHAR(100) PRIMARY KEY,
    subject_id VARCHAR(100) NOT NULL,
    block_name VARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);

-- Create students table with foreign key reference to blocks
CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(100) NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    block_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (student_id, block_id),  -- Composite primary key
    FOREIGN KEY (block_id) REFERENCES blocks(block_id) ON DELETE CASCADE
);

-- Create attendance table with foreign key references to students, blocks, and subjects
CREATE TABLE IF NOT EXISTS attendance (
   attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(100) NOT NULL,
    block_id VARCHAR(100) NOT NULL,
	session_id VARCHAR(100) NOT NULL,  -- new column to store blockId_date
	date VARCHAR(100) NOT NULL,
    time_in TIME NOT NULL,
    status ENUM('P', 'L', 'A') NOT NULL,
    FOREIGN KEY (student_id, block_id) REFERENCES students(student_id, block_id) ON DELETE CASCADE,
	FOREIGN KEY (block_id) REFERENCES blocks(block_id) ON DELETE CASCADE,
    UNIQUE (student_id, block_id, session_id)  -- ensures a student can only check in once per session
);

-- Indexes for improved performance
CREATE INDEX idx_student_id ON attendance(student_id);
CREATE INDEX idx_block_id ON attendance(block_id);




