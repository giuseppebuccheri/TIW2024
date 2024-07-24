-- MySQL dump 10.13  Distrib 8.2.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tiw24
-- ------------------------------------------------------
-- Server version	8.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `album_image`
--

DROP TABLE IF EXISTS `album_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album_image` (
  `album_id` int NOT NULL,
  `image_id` int NOT NULL,
  PRIMARY KEY (`album_id`,`image_id`),
  KEY `image_id` (`image_id`),
  CONSTRAINT `album_image_ibfk_1` FOREIGN KEY (`album_id`) REFERENCES `albums` (`id_album`) ON DELETE CASCADE,
  CONSTRAINT `album_image_ibfk_2` FOREIGN KEY (`image_id`) REFERENCES `images` (`id_image`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album_image`
--

LOCK TABLES `album_image` WRITE;
/*!40000 ALTER TABLE `album_image` DISABLE KEYS */;
INSERT INTO `album_image` (`album_id`, `image_id`) VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1),(12,1),(13,1),(14,1),(15,1),(16,1),(17,1),(18,1),(19,1),(20,1),(21,1),(22,1),(23,1),(24,1),(25,1),(26,1),(27,1),(1,2),(2,2),(3,2),(4,2),(5,2),(6,2),(7,2),(10,2),(12,2),(13,2),(14,2),(15,2),(16,2),(17,2),(18,2),(21,2),(22,2),(23,2),(24,2),(25,2),(26,2),(27,2),(1,3),(2,3),(3,3),(4,3),(5,3),(6,3),(10,3),(12,3),(13,3),(14,3),(15,3),(16,3),(17,3),(21,3),(22,3),(23,3),(24,3),(25,3),(26,3),(27,3),(1,4),(2,4),(3,4),(5,4),(6,4),(10,4),(13,4),(14,4),(15,4),(16,4),(17,4),(21,4),(23,4),(24,4),(25,4),(26,4),(27,4),(1,5),(2,5),(3,5),(6,5),(10,5),(13,5),(14,5),(16,5),(17,5),(21,5),(23,5),(24,5),(26,5),(27,5),(1,6),(2,6),(3,6),(10,6),(14,6),(16,6),(21,6),(24,6),(26,6),(1,7),(3,7),(14,7),(24,7),(1,8),(1,9),(1,11),(1,12);
/*!40000 ALTER TABLE `album_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `albums`
--

DROP TABLE IF EXISTS `albums`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `albums` (
  `id_album` int NOT NULL AUTO_INCREMENT,
  `id_author` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `imagesOrder` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_album`),
  UNIQUE KEY `albums_pk` (`title`),
  KEY `id_author` (`id_author`),
  CONSTRAINT `albums_ibfk_1` FOREIGN KEY (`id_author`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `albums`
--

LOCK TABLES `albums` WRITE;
/*!40000 ALTER TABLE `albums` DISABLE KEYS */;
INSERT INTO `albums` (`id_album`, `id_author`, `title`, `date`, `imagesOrder`) VALUES (1,1,'holidays','2024-07-12','1,3,11,4,7,6,8,9,2,5,12'),(2,2,'trips','2028-07-06',NULL),(3,4,'italy','2020-07-04',NULL),(4,1,'test','2024-07-12','1,11,8,4,7,3,9,2,5,12,6'),(5,1,'food','2024-07-12','6,8,1,11,4,7,3,9,2,5,12'),(6,1,'football','2024-07-12','6,8,1,11,4,7,3,9,2,5,12'),(7,2,'movies','2020-10-28',NULL),(8,1,'Vacation','2023-06-15','6,8,1,11,4,7,3,9,2,5,12'),(9,2,'Birthday Party','2023-08-20',NULL),(10,3,'Wedding','2022-09-30',NULL),(11,4,'Graduation','2021-07-10',NULL),(12,5,'Hiking Trip','2023-05-12',NULL),(13,6,'Christmas','2022-12-25',NULL),(14,7,'Family Reunion','2021-11-02',NULL),(15,1,'New Year','2023-01-01',NULL),(16,2,'Beach Day','2023-04-18',NULL),(17,3,'Concert','2023-07-05',NULL),(18,4,'Art Gallery','2023-03-14',NULL),(19,5,'Road Trip','2023-02-28',NULL),(20,6,'Halloween','2022-10-31',NULL),(21,7,'Easter','2023-04-09',NULL),(22,1,'Spring Festival','2023-05-05','6,8,1,11,4,7,3,9,2,5,12'),(23,2,'Autumn Leaves','2022-11-15',NULL),(24,3,'City Tour','2022-09-22',NULL),(25,4,'Museum Visit','2023-06-20',NULL),(26,5,'Picnic','2023-07-25',NULL),(27,6,'Zoo Trip','2023-08-14',NULL);
/*!40000 ALTER TABLE `albums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id_comment` int NOT NULL AUTO_INCREMENT,
  `text` varchar(50) NOT NULL,
  `image` int NOT NULL,
  `user` int NOT NULL,
  PRIMARY KEY (`id_comment`),
  KEY `image` (`image`),
  KEY `comments_users_id_user_fk` (`user`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`image`) REFERENCES `images` (`id_image`),
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user`) REFERENCES `users` (`id_user`),
  CONSTRAINT `comments_users_id_user_fk` FOREIGN KEY (`user`) REFERENCES `users` (`id_user`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` (`id_comment`, `text`, `image`, `user`) VALUES (2,'wow',1,1),(5,'bella',4,1),(6,'amazing',7,1),(8,'cool',2,1),(9,'Cool picture!',1,1),(10,'Amazing!',2,2),(11,'I love it!',3,3),(12,'Nice shot!',4,4),(13,'Beautiful!',5,5),(14,'Great work!',6,6),(15,'Stunning!',7,7),(16,'Wow!',8,1),(17,'Fantastic!',9,2),(18,'Impressive!',10,3),(19,'Excellent!',11,4),(20,'Remarkable!',12,5),(21,'So cool!',1,2),(22,'Love this!',2,3),(23,'Fantastic shot!',3,4),(24,'Wonderful!',4,5),(25,'Great detail!',5,6),(26,'Spectacular!',6,7),(27,'Breathtaking!',7,1),(28,'Love the colors!',8,2),(29,'So beautiful!',9,3),(30,'Incredible!',10,4),(31,'Mind-blowing!',11,5),(32,'So detailed!',12,6),(33,'Outstanding!',1,3),(34,'Superb!',2,4),(35,'Marvelous!',3,5),(36,'Unbelievable!',4,6),(37,'Mesmerizing!',5,7),(38,'Fascinating!',6,1),(39,'Phenomenal!',7,2),(40,'Great shot!',8,3),(41,'Love the perspective!',9,4),(42,'So artistic!',10,5),(43,'Very creative!',11,6),(44,'Such a great image!',12,7);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `images` (
  `id_image` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `description` text NOT NULL,
  `path` varchar(255) NOT NULL,
  `author` int NOT NULL,
  PRIMARY KEY (`id_image`),
  KEY `id_author` (`author`),
  CONSTRAINT `images_ibfk_1` FOREIGN KEY (`author`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` (`id_image`, `title`, `date`, `description`, `path`, `author`) VALUES (1,'pizza','2024-07-01','pizza photo','images/pizza.jpg',1),(2,'ferrari','2023-10-18','ferrari photo','images/ferrari.jpg',1),(3,'wall','2024-07-11','wall','images/wall.jpg',1),(4,'venice','2024-07-24','venice photo','images/venice.jpg',4),(5,'new york','2023-07-01','new york skyline','images/newyork.jpg',2),(6,'road','2019-07-17','us road','images/road.jpg',1),(7,'sparkles','2024-07-13','universe','images/sparkles.jpg',1),(8,'lake','2021-05-06','lake','images/lake.jpg',1),(9,'bycicle','2024-07-10','bycicle','images/bycicle.jpg',1),(10,'crab','2023-10-17','mr crab','images/crab.jpg',1),(11,'volcano','2010-07-03','vesuvio','images/volcano.jpg',1),(12,'tree','2019-12-13','tree','images/tree.jpg',1);
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `email` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id_user`, `email`, `username`, `password`) VALUES (1,'admin','admin','pwd'),(2,'45@gmail.com','elly','444'),(3,'dsadsa@gmail.com','john','1'),(4,'fsafoa@gmail.com','marshall','sas'),(5,'dasdasfff@gmail.com','ted','1'),(6,'4aasdsadasd5@gmail.com','robert','w'),(7,'dd@sada.it','walter','s');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-24 14:07:48
