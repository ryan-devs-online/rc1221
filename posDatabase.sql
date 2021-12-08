CREATE DATABASE  IF NOT EXISTS `cf-pos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cf-pos`;
-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: localhost    Database: cf-pos
-- ------------------------------------------------------
-- Server version	8.0.27

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
-- Table structure for table `receipts`
--

DROP TABLE IF EXISTS `receipts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipts` (
  `TOOL_CODE` varchar(45) NOT NULL,
  `START_DATE` date NOT NULL,
  `END_DATE` date NOT NULL,
  `FULL_RECIEPT` varchar(2000) NOT NULL,
  `PURCHASE_NUMBER` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`PURCHASE_NUMBER`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipts`
--

LOCK TABLES `receipts` WRITE;
/*!40000 ALTER TABLE `receipts` DISABLE KEYS */;
INSERT INTO `receipts` VALUES ('CHNS','2021-12-06','2021-12-11','Your tool rental receipt \nTool code: CHNS\nTool type: Chainsaw\nTool brand: Stihl\nRental Days: 5\nCheckout date: 12/06/21\nDue date: 12/11/21\nDaily rental Charge: $1.49\nCharge Days: 5\nPre-discount charge: $7.45\nDiscount percent: 0%\nDiscount amount: $0.00\nFinal charge: $7.45',1),('JAKD','2021-12-06','2021-12-07','Your tool rental receipt \nTool code: JAKD\nTool type: Jackhammer\nTool brand: DeWalt\nRental Days: 1\nCheckout date: 12/06/21\nDue date: 12/07/21\nDaily rental Charge: $2.99\nCharge Days: 1\nPre-discount charge: $2.99\nDiscount percent: 0%\nDiscount amount: $0.00\nFinal charge: $2.99',2),('CHNS','2021-12-11','2021-12-14','Your tool rental receipt \nTool code: CHNS\nTool type: Chainsaw\nTool brand: Stihl\nRental Days: 3\nCheckout date: 12/11/21\nDue date: 12/14/21\nDaily rental Charge: $1.49\nCharge Days: 1\nPre-discount charge: $1.49\nDiscount percent: 0%\nDiscount amount: $0.00\nFinal charge: $1.49',3);
/*!40000 ALTER TABLE `receipts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tools`
--

DROP TABLE IF EXISTS `tools`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tools` (
  `TOOL_CODE` varchar(4) NOT NULL,
  `TOOL_TYPE` varchar(45) NOT NULL,
  `BRAND` varchar(45) NOT NULL,
  `DAILY_CHARGE` double NOT NULL,
  `WEEKDAY_CHARGE` tinyint NOT NULL,
  `WEEKEND_CHARGE` tinyint NOT NULL,
  `HOLIDAY_CHARGE` tinyint NOT NULL,
  PRIMARY KEY (`TOOL_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tools`
--

LOCK TABLES `tools` WRITE;
/*!40000 ALTER TABLE `tools` DISABLE KEYS */;
INSERT INTO `tools` VALUES ('CHNS','Chainsaw','Stihl',1.49,1,0,1),('JAKD','Jackhammer','DeWalt',2.99,1,0,0),('JAKR','Jackhammer','Ridgid',2.99,1,0,0),('LADW','Ladder','Werner',1.99,1,1,0);
/*!40000 ALTER TABLE `tools` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-12-08  6:45:13
