CREATE DATABASE  IF NOT EXISTS `dbverbalizzazione` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `dbverbalizzazione`;
-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dbverbalizzazione
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` int NOT NULL,
  `name` varchar(64) NOT NULL,
  `matricolaTeacher` varchar(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `matricolaTeacher_idx` (`matricolaTeacher`),
  CONSTRAINT `matricolaTeacherCourse` FOREIGN KEY (`matricolaTeacher`) REFERENCES `teacher` (`matricola`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'Calculus 1','321098'),(2,'Calculus 2','321098'),(3,'Probability','321098'),(4,'Statistics','210987'),(5,'Linear Algebra','210987'),(6,'Computer Programming','210987'),(7,'Theory of Computation','109876'),(8,'Software Engineening','109876'),(9,'DBMS','098765'),(10,'Chemistry','098765');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_students`
--

DROP TABLE IF EXISTS `course_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_students` (
  `matricolaStudent` varchar(6) NOT NULL,
  `courseId` int NOT NULL,
  PRIMARY KEY (`matricolaStudent`,`courseId`),
  KEY `courseId_idx` (`courseId`) /*!80000 INVISIBLE */,
  CONSTRAINT `courseIdStudent` FOREIGN KEY (`courseId`) REFERENCES `course` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `matricolaStudentStudent` FOREIGN KEY (`matricolaStudent`) REFERENCES `student` (`matricola`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_students`
--

LOCK TABLES `course_students` WRITE;
/*!40000 ALTER TABLE `course_students` DISABLE KEYS */;
INSERT INTO `course_students` VALUES ('012345',1),('012345',5),('123456',1),('123456',2),('123456',6),('123456',9),('234567',1),('234567',2),('234567',6),('234567',9),('345678',1),('345678',2),('345678',7),('432109',1),('432109',6),('456789',1),('456789',3),('456789',7),('543210',1),('543210',5),('567890',1),('567890',3),('567890',7),('654321',1),('654321',5),('678901',1),('678901',3),('678901',8),('789012',1),('789012',4),('789012',8),('890123',1),('890123',4),('890123',8),('901234',1),('901234',4),('901234',9);
/*!40000 ALTER TABLE `course_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam` (
  `courseId` int NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`courseId`,`date`),
  KEY `date_idx` (`date`) /*!80000 INVISIBLE */,
  KEY `courseId_idx` (`courseId`),
  CONSTRAINT `courseIdExam` FOREIGN KEY (`courseId`) REFERENCES `course` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam`
--

LOCK TABLES `exam` WRITE;
/*!40000 ALTER TABLE `exam` DISABLE KEYS */;
INSERT INTO `exam` VALUES (1,'2023-01-01'),(1,'2023-02-01'),(1,'2023-03-01'),(2,'2023-01-10'),(2,'2023-02-10'),(3,'2023-01-11'),(4,'2023-01-11'),(5,'2023-02-15'),(6,'2023-03-15'),(7,'2023-04-15'),(8,'2023-05-15'),(9,'2023-06-15'),(10,'2023-07-15');
/*!40000 ALTER TABLE `exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_students`
--

DROP TABLE IF EXISTS `exam_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_students` (
  `matricolaStudent` varchar(6) NOT NULL,
  `courseId` int NOT NULL,
  `examDate` date NOT NULL,
  `result` enum('ASSENTE','RIMANDATO','RIPROVATO','18','19','20','21','22','23','24','25','26','27','28','29','30','30L') DEFAULT NULL,
  `resultState` enum('NON INSERITO','INSERITO','PUBBLICATO','VERBALIZZATO','RIFIUTATO') NOT NULL DEFAULT 'NON INSERITO',
  PRIMARY KEY (`matricolaStudent`,`courseId`,`examDate`),
  KEY `examDate_idx` (`examDate`),
  KEY `courseId_idx` (`courseId`),
  KEY `matricolaStudentExamSt_idx` (`matricolaStudent`),
  CONSTRAINT `courseIdExamSt` FOREIGN KEY (`courseId`) REFERENCES `exam` (`courseId`) ON UPDATE CASCADE,
  CONSTRAINT `examDateExamSt` FOREIGN KEY (`examDate`) REFERENCES `exam` (`date`) ON UPDATE CASCADE,
  CONSTRAINT `matricolaStudentExamSt` FOREIGN KEY (`matricolaStudent`) REFERENCES `student` (`matricola`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_students`
--

LOCK TABLES `exam_students` WRITE;
/*!40000 ALTER TABLE `exam_students` DISABLE KEYS */;
INSERT INTO `exam_students` VALUES ('012345',1,'2023-01-01',NULL,'NON INSERITO'),('012345',5,'2023-02-15',NULL,'NON INSERITO'),('123456',1,'2023-01-01',NULL,'NON INSERITO'),('123456',1,'2023-02-01',NULL,'NON INSERITO'),('123456',2,'2023-01-10','22','INSERITO'),('234567',1,'2023-01-01',NULL,'NON INSERITO'),('234567',2,'2023-01-10','27','INSERITO'),('345678',1,'2023-01-01',NULL,'NON INSERITO'),('345678',2,'2023-01-10','30','INSERITO'),('432109',1,'2023-01-01',NULL,'NON INSERITO'),('456789',1,'2023-01-01',NULL,'NON INSERITO'),('456789',3,'2023-01-11',NULL,'NON INSERITO'),('543210',1,'2023-01-01',NULL,'NON INSERITO'),('567890',1,'2023-01-01',NULL,'NON INSERITO'),('567890',3,'2023-01-11',NULL,'NON INSERITO'),('654321',1,'2023-01-01',NULL,'NON INSERITO'),('678901',1,'2023-01-01',NULL,'NON INSERITO'),('678901',3,'2023-01-11',NULL,'NON INSERITO'),('789012',1,'2023-01-01',NULL,'NON INSERITO'),('789012',4,'2023-01-11','RIMANDATO','PUBBLICATO'),('890123',1,'2023-01-01',NULL,'NON INSERITO'),('890123',4,'2023-01-11','RIPROVATO','PUBBLICATO'),('901234',1,'2023-01-01',NULL,'NON INSERITO'),('901234',4,'2023-01-11','30L','PUBBLICATO');
/*!40000 ALTER TABLE `exam_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `matricola` varchar(6) NOT NULL,
  `username` varchar(32) NOT NULL,
  `password` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL,
  `surname` varchar(64) NOT NULL,
  `degree` varchar(64) NOT NULL,
  `email` varchar(64) NOT NULL,
  PRIMARY KEY (`matricola`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES ('012345','s.kare','s.kare','Susan','Kare','Electrical  Engineering','susan.kare@polimi.it'),('123456','b.liskov','b.liskov','Barbara','Liskov','Computer Engineering','barbara.liskov@polimi.it'),('234567','g.hopper','g.hopper','Grace','Hopper','Computer Engineering','grace.hopper@polimi.it'),('345678','j.jhonson','j.jhonson','Katherine','Johnson','Electrical  Engineering','katherine.jhonson@polimi.it'),('432109','a.borg','a.borg','Anita','Borg','Computer Engineering','anita.borg@polimi.it'),('456789','j.clarke','j.clarke','Joan','Clarke','Mathematics','joan.clarke@polimi.it'),('543210','l.conway','l.conway','Lynn','Conway','Mathematics','lynn.conway@polimi.it'),('567890','j.perlman','j.perlman','Radia','Perlman','Phisics','radia.perlman@polimi.it'),('654321','s.wilson','s.wilson','Sophie ','Wilson','Electronic Engineering','sophie.wilson@polimi.it'),('678901','j.sammet','j.sammet','Jean','Sammet','Electronic Engineering','jean.sammet@polimi.it'),('789012','c.shaw','c.shaw','Carol','Shaw','Computer Science','carol.shaw@polimi.it'),('890123','m.hamilton','m.hamilton','Margaret ','Hamilton','Mathematics','margaret.hamilton@polimi.it'),('901234','a.lovelace','a.lovelace','Ada','Lovelace','Phisics','ada.lovelace@polimi.it');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `matricola` varchar(6) NOT NULL,
  `username` varchar(32) NOT NULL,
  `password` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL,
  `surname` varchar(64) NOT NULL,
  PRIMARY KEY (`matricola`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
INSERT INTO `teacher` VALUES ('098765','r.saujani','r.saujani','Reshma','Saujani'),('109876','m.mayer','m.mayer','Marissa','Mayer'),('210987','c.braezeal','c.braezeal','Cynthia','Braezeal'),('321098','s.goldwasser','s.goldwasser','Shafi','Goldwasser');
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verbal`
--

DROP TABLE IF EXISTS `verbal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verbal` (
  `id` int NOT NULL AUTO_INCREMENT,
  `examDate` date NOT NULL,
  `courseId` int NOT NULL,
  `dateTime` datetime NOT NULL,
  `matricolaTeacher` varchar(6) NOT NULL,
  PRIMARY KEY (`id`,`examDate`,`courseId`),
  KEY `examDate_idx` (`examDate`),
  KEY `courseId_idx` (`courseId`),
  KEY `matricolaTeacher_idx` (`matricolaTeacher`),
  CONSTRAINT `courseIdVerbal` FOREIGN KEY (`courseId`) REFERENCES `exam` (`courseId`) ON UPDATE CASCADE,
  CONSTRAINT `examDateVerbal` FOREIGN KEY (`examDate`) REFERENCES `exam` (`date`) ON UPDATE CASCADE,
  CONSTRAINT `matricolaTeacherVerbal` FOREIGN KEY (`matricolaTeacher`) REFERENCES `course` (`matricolaTeacher`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verbal`
--

LOCK TABLES `verbal` WRITE;
/*!40000 ALTER TABLE `verbal` DISABLE KEYS */;
/*!40000 ALTER TABLE `verbal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'dbverbalizzazione'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-07 19:30:13
