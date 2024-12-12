CREATE SCHEMA IF NOT EXISTS attendifydatabase;

USE attendifydatabase;

DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS blocks;
DROP TABLE IF EXISTS subjects;

CREATE TABLE IF NOT EXISTS subjects (
                                        subject_id VARCHAR(100) PRIMARY KEY,
                                        subject_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS blocks (
                                      block_id VARCHAR(100) PRIMARY KEY,
                                      subject_id VARCHAR(100) NOT NULL,
                                      block_name VARCHAR(100) NOT NULL,
                                      start_time TIME NOT NULL,
                                      end_time TIME NOT NULL,
                                      FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS students (
                                        student_id VARCHAR(100) NOT NULL,
                                        student_name VARCHAR(255) NOT NULL,
                                        block_id VARCHAR(100) NOT NULL,
                                        PRIMARY KEY (student_id, block_id),
                                        FOREIGN KEY (block_id) REFERENCES blocks(block_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attendance (
                                          attendance_id INT AUTO_INCREMENT PRIMARY KEY,
                                          session_id VARCHAR(100) NOT NULL,
                                          student_id VARCHAR(100) NOT NULL,
                                          subject_id VARCHAR (100) NOT NULL,
                                          block_id VARCHAR(100) NOT NULL,
                                          date VARCHAR(100) NOT NULL,
                                          time_in TIME NOT NULL,
                                          status ENUM('P', 'L', 'A') NOT NULL,
                                          FOREIGN KEY (student_id, block_id) REFERENCES students(student_id, block_id) ON DELETE CASCADE,
                                          FOREIGN KEY (block_id) REFERENCES blocks(block_id) ON DELETE CASCADE,
                                          FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
                                          UNIQUE (student_id, block_id, session_id)
);

INSERT INTO subjects VALUES
                         ('CPE405','DISCRETE MATHEMATICS'),
                         ('CS211','OBJECT-ORIENTED PROGRAMMING'),
                         ('CS212','COMPUTER ORGANIZATION WITH ASSEMBLY LANGUAGE'),
                         ('GED109','SCIENCE, TECHNOLOGY, AND SOCIETY'),
                         ('IT211','DATABASE MANAGEMENT SYSTEM'),
                         ('IT212','COMPUTER NETWORKING 1')
;
INSERT INTO blocks VALUES
                       ('CPE405_CS2101','CPE405','CS2101','09:00:00','11:00:00'),
                       ('CS211_CS2101','CS211','CS2101','07:00:00','10:00:00'),
                       ('GED109_CS2101','GED109','CS2101','11:30:00','13:00:00'),
                       ('IT212_CS2101','IT212','CS2101','13:00:00','18:00:00')
;

INSERT INTO students VALUES
                         ('23-01292', 'AFRICA, KIARRA FRANCESCA GABRIELLE S.', 'CS211_CS2101'),
                         ('23-00562 ', 'AGUZAR, JOEL LAZERNIE A.', 'CS211_CS2101'),
                         ('23-07302', 'ALCARAZ, JOHN C.', 'CS211_CS2101'),
                         ('23-09909', 'ALCARAZ, PAUL C.', 'CS211_CS2101'),
                         ('23-05942', 'ANTENOR, JUSTIN MIGUEL G.', 'CS211_CS2101'),
                         ('23-03029', 'BALAGTAS, MICHAELLA P.', 'CS211_CS2101'),
                         ('23-06630', 'BALMES, GENRIQUE SEAN ARKIN D.', 'CS211_CS2101'),
                         ('23-03102', 'BUNQUIN, THEODORE VON JOSHUA M.', 'CS211_CS2101'),
                         ('23-05464', 'LALU, JERZHA ARA RAMIL C.', 'CS211_CS2101'),
                         ('23-06458', 'MALLEN, JAN MAYEN D.', 'CS211_CS2101'),
                         ('23-07848', 'NUÑEZ, NIGEL HANS G.', 'CS211_CS2101'),
                         ('23-02989', 'PANGANIBAN, LENARD ANDREI V.', 'CS211_CS2101'),
                         ('23-02148', 'TORRES, RICHARD CRUE R.', 'CS211_CS2101'),
                         ('23-01484', 'VILLAR, VINCE ANJO R.', 'CS211_CS2101'),

                         ('23-01292', 'AFRICA, KIARRA FRANCESCA GABRIELLE S.', 'CPE405_CS2101'),
                         ('23-00562 ', 'AGUZAR, JOEL LAZERNIE A.', 'CPE405_CS2101'),
                         ('23-07302', 'ALCARAZ, JOHN C.', 'CPE405_CS2101'),
                         ('23-09909', 'ALCARAZ, PAUL C.', 'CPE405_CS2101'),
                         ('23-05942', 'ANTENOR, JUSTIN MIGUEL G.', 'CPE405_CS2101'),
                         ('23-03029', 'BALAGTAS, MICHAELLA P.', 'CPE405_CS2101'),
                         ('23-06630', 'BALMES, GENRIQUE SEAN ARKIN D.', 'CPE405_CS2101'),
                         ('23-03102', 'BUNQUIN, THEODORE VON JOSHUA M.', 'CPE405_CS2101'),
                         ('23-05464', 'LALU, JERZHA ARA RAMIL C.', 'CPE405_CS2101'),
                         ('23-06458', 'MALLEN, JAN MAYEN D.', 'CPE405_CS2101'),
                         ('23-07848', 'NUÑEZ, NIGEL HANS G.', 'CPE405_CS2101'),
                         ('23-02989', 'PANGANIBAN, LENARD ANDREI V.', 'CPE405_CS2101'),
                         ('23-02148', 'TORRES, RICHARD CRUE R.', 'CPE405_CS2101'),
                         ('23-01484', 'VILLAR, VINCE ANJO R.', 'CPE405_CS2101'),

                         ('23-01292', 'AFRICA, KIARRA FRANCESCA GABRIELLE S.', 'GED109_CS2101'),
                         ('23-00562 ', 'AGUZAR, JOEL LAZERNIE A.', 'GED109_CS2101'),
                         ('23-07302', 'ALCARAZ, JOHN C.', 'GED109_CS2101'),
                         ('23-09909', 'ALCARAZ, PAUL C.', 'GED109_CS2101'),
                         ('23-05942', 'ANTENOR, JUSTIN MIGUEL G.', 'GED109_CS2101'),
                         ('23-03029', 'BALAGTAS, MICHAELLA P.', 'GED109_CS2101'),
                         ('23-06630', 'BALMES, GENRIQUE SEAN ARKIN D.', 'GED109_CS2101'),
                         ('23-03102', 'BUNQUIN, THEODORE VON JOSHUA M.', 'GED109_CS2101'),
                         ('23-05464', 'LALU, JERZHA ARA RAMIL C.', 'GED109_CS2101'),
                         ('23-06458', 'MALLEN, JAN MAYEN D.', 'GED109_CS2101'),
                         ('23-07848', 'NUÑEZ, NIGEL HANS G.', 'GED109_CS2101'),
                         ('23-02989', 'PANGANIBAN, LENARD ANDREI V.', 'GED109_CS2101'),
                         ('23-02148', 'TORRES, RICHARD CRUE R.', 'GED109_CS2101'),
                         ('23-01484', 'VILLAR, VINCE ANJO R.', 'GED109_CS2101'),

                         ('23-01292', 'AFRICA, KIARRA FRANCESCA GABRIELLE S.', 'IT212_CS2101'),
                         ('23-00562 ', 'AGUZAR, JOEL LAZERNIE A.', 'IT212_CS2101'),
                         ('23-07302', 'ALCARAZ, JOHN C.', 'IT212_CS2101'),
                         ('23-09909', 'ALCARAZ, PAUL C.', 'IT212_CS2101'),
                         ('23-05942', 'ANTENOR, JUSTIN MIGUEL G.', 'IT212_CS2101'),
                         ('23-03029', 'BALAGTAS, MICHAELLA P.', 'IT212_CS2101'),
                         ('23-06630', 'BALMES, GENRIQUE SEAN ARKIN D.', 'IT212_CS2101'),
                         ('23-03102', 'BUNQUIN, THEODORE VON JOSHUA M.', 'IT212_CS2101'),
                         ('23-05464', 'LALU, JERZHA ARA RAMIL C.', 'IT212_CS2101'),
                         ('23-06458', 'MALLEN, JAN MAYEN D.', 'IT212_CS2101'),
                         ('23-07848', 'NUÑEZ, NIGEL HANS G.', 'IT212_CS2101'),
                         ('23-02989', 'PANGANIBAN, LENARD ANDREI V.', 'IT212_CS2101'),
                         ('23-02148', 'TORRES, RICHARD CRUE R.', 'IT212_CS2101'),
                         ('23-01484', 'VILLAR, VINCE ANJO R.', 'IT212_CS2101')
;
