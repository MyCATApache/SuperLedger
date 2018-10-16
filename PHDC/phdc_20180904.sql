-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 39.104.99.78    Database: phdc
-- ------------------------------------------------------
-- Server version	5.7.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tb_institution`
--

DROP TABLE IF EXISTS `tb_institution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_institution` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1' COMMENT '启用状态 1)启用 2)未启用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='机构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_institution`
--

LOCK TABLES `tb_institution` WRITE;
/*!40000 ALTER TABLE `tb_institution` DISABLE KEYS */;
INSERT INTO `tb_institution` VALUES (1001,'ho1',1,'2018-08-30 08:51:02');
/*!40000 ALTER TABLE `tb_institution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_institution_depart`
--

DROP TABLE IF EXISTS `tb_institution_depart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_institution_depart` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '科室ID',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '科室名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2351 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_institution_depart`
--

LOCK TABLES `tb_institution_depart` WRITE;
/*!40000 ALTER TABLE `tb_institution_depart` DISABLE KEYS */;
INSERT INTO `tb_institution_depart` VALUES (2345,'血糖'),(2346,'血糖总结'),(2347,'血脂'),(2348,'血脂总结'),(2349,'血压'),(2350,'血压总结');
/*!40000 ALTER TABLE `tb_institution_depart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_institution_dictionary`
--

DROP TABLE IF EXISTS `tb_institution_dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_institution_dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `depart_id` int(11) DEFAULT NULL COMMENT '科室ID',
  `inst_id` int(11) DEFAULT NULL COMMENT '所属机构ID',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '项目名称',
  PRIMARY KEY (`id`),
  KEY `depart_id_index` (`depart_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2351 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='机构体检项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_institution_dictionary`
--

LOCK TABLES `tb_institution_dictionary` WRITE;
/*!40000 ALTER TABLE `tb_institution_dictionary` DISABLE KEYS */;
INSERT INTO `tb_institution_dictionary` VALUES (2345,2345,1001,'血糖'),(2346,2346,1001,'血糖总结'),(2347,2347,1001,'血脂'),(2348,2348,1001,'血脂总结'),(2349,2349,1001,'血压'),(2350,2350,1001,'血压总结');
/*!40000 ALTER TABLE `tb_institution_dictionary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_member`
--

DROP TABLE IF EXISTS `tb_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_member` (
  `id` int(11) NOT NULL COMMENT '用户表',
  `name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '姓名',
  `gender` tinyint(4) DEFAULT '0' COMMENT '性别 0)未知 1)男 2)女',
  `mobile` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_member`
--

LOCK TABLES `tb_member` WRITE;
/*!40000 ALTER TABLE `tb_member` DISABLE KEYS */;
INSERT INTO `tb_member` VALUES (1,'zhan',1,'15088923179','1999-06-22','2018-09-01 13:14:08'),(4567,'kaiz',1,'12345678912','2009-09-23','2018-08-31 13:19:06');
/*!40000 ALTER TABLE `tb_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_member_invitation`
--

DROP TABLE IF EXISTS `tb_member_invitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_member_invitation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `report_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '申请的报告',
  `member_id` int(11) DEFAULT NULL COMMENT '邀请人ID',
  `invitation_member_id` int(11) DEFAULT NULL COMMENT '被邀请的用户ID',
  `mobile` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '姓名',
  `code` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '邀请码',
  `status` tinyint(4) DEFAULT NULL COMMENT '数据状态 1)已生成 2)已申请 3)拒绝申请 4)同意申请 5)审核中',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='邀请数据访问表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_member_invitation`
--

LOCK TABLES `tb_member_invitation` WRITE;
/*!40000 ALTER TABLE `tb_member_invitation` DISABLE KEYS */;
INSERT INTO `tb_member_invitation` VALUES (1,'1',1,1,'15088923179','zhan','1',4,'2018-08-30 13:30:30','2018-08-30 13:30:30'),(2,'2f1358f1300000b',4567,4567,'12345678912','kaiz','4567',4,'2018-09-02 14:05:37','2018-09-04 12:18:47'),(3,'2f135d37c00000b',4567,4567,'12345678912','kaiz','4567',2,'2018-09-02 14:08:32','2018-09-02 14:08:32'),(4,'2f1388cdb000006',4567,4567,'12345678912','kaiz','4567',2,'2018-09-02 14:38:16','2018-09-02 14:38:16'),(5,'2f13baf96000006',4567,4567,'12345678912','kaiz','4567',2,'2018-09-02 15:12:31','2018-09-02 15:12:31'),(6,'2f13c156f000006',4567,4567,'12345678912','kaiz','4567',2,'2018-09-02 15:16:52','2018-09-02 15:16:52'),(7,'2f19ebe4a000007',4567,4567,'12345678912','kaiz','4567',5,'2018-09-03 09:14:26','2018-09-03 09:14:26'),(8,'2f19f305c000007',4567,4567,'12345678912','kaiz','4567',5,'2018-09-03 09:19:20','2018-09-03 09:19:20'),(9,'2f1a0498e000007',4567,4567,'12345678912','kaiz','4567',5,'2018-09-03 09:31:20','2018-09-03 09:31:20'),(10,'2f1a0ea76000007',4567,4567,'12345678912','kaiz','4567',2,'2018-09-03 09:38:11','2018-09-03 09:38:11'),(11,'2f1a7d7dd000007',4567,4567,'12345678912','kaiz','4567',2,'2018-09-03 10:53:54','2018-09-03 10:53:54'),(12,'2f1a919b9000007',1,1,'15088923179','zhan','1',2,'2018-09-03 11:07:38','2018-09-03 11:07:38'),(13,'2f22c09d6000007',4567,4567,'12345678912','kaiz','4567',2,'2018-09-04 10:57:50','2018-09-04 10:57:50'),(14,'2f232f476000007',4567,4567,'12345678912','kaiz','4567',2,'2018-09-04 12:13:22','2018-09-04 12:13:22'),(15,'2f237332a000007',4567,4567,'12345678912','kaiz','4567',4,'2018-09-04 12:59:44','2018-09-04 13:35:21'),(16,'2f239f74c000007',4567,4567,'12345678912','kaiz','4567',4,'2018-09-04 13:29:57','2018-09-04 13:35:05'),(17,'2f23c6ace000007',4567,4567,'12345678912','kaiz','4567',4,'2018-09-04 13:56:44','2018-09-04 14:01:58'),(18,'2f240124d000007',4567,4567,'12345678912','kaiz','4567',4,'2018-09-04 14:36:38','2018-09-04 14:42:23'),(19,'2f240d92b000007',4567,4567,'12345678912','kaiz','4567',4,'2018-09-04 14:45:08','2018-09-04 14:50:08');
/*!40000 ALTER TABLE `tb_member_invitation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_report`
--

DROP TABLE IF EXISTS `tb_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '报告ID',
  `depart_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '科室名称',
  `check_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '体检时间',
  `result` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '体检结果',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='报告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_report`
--

LOCK TABLES `tb_report` WRITE;
/*!40000 ALTER TABLE `tb_report` DISABLE KEYS */;
INSERT INTO `tb_report` VALUES (1,'2f1358f1300000b','血糖','2018-09-01 16:00:00','未见异常'),(2,'2f1358f1300000b','血脂','2018-09-01 16:00:00','未见异常'),(3,'2f1358f1300000b','XS','2018-09-01 16:00:00','XXXXXXX'),(4,'2f1358f1300000b','XSGGG','2018-09-01 16:00:00','SDFDSA'),(5,'2f1358f1300000b','血糖','2018-09-01 16:00:00','未见异常'),(6,'2f1358f1300000b','血糖','2018-09-03 16:00:00','未见异常'),(7,'2f1358f1300000b','血脂','2018-09-01 16:00:00','未见异常'),(8,'2f1358f1300000b','血压','2018-09-03 16:00:00','未见异常'),(9,'2f1358f1300000b','血糖','2018-09-01 16:00:00','未见异常'),(10,'2f1358f1300000b','血糖','2018-09-03 16:00:00','未见异常'),(11,'2f1358f1300000b','血脂','2018-09-01 16:00:00','未见异常'),(12,'2f1358f1300000b','血压','2018-09-03 16:00:00','未见异常'),(13,'2f1358f1300000b','血糖','2018-09-01 16:00:00','未见异常'),(14,'2f1358f1300000b','血糖','2018-09-03 16:00:00','未见异常'),(15,'2f1358f1300000b','血脂','2018-09-01 16:00:00','未见异常'),(16,'2f1358f1300000b','血压','2018-09-03 16:00:00','未见异常'),(17,'2f237332a000007','血糖','2018-09-01 16:00:00','未见异常'),(18,'2f237332a000007','血糖','2018-09-03 16:00:00','未见异常'),(19,'2f237332a000007','血脂','2018-09-01 16:00:00','未见异常'),(20,'2f237332a000007','血压','2018-09-03 16:00:00','未见异常'),(21,'2f239f74c000007','血糖','2018-09-01 16:00:00','未见异常'),(22,'2f239f74c000007','血糖','2018-09-03 16:00:00','未见异常'),(23,'2f239f74c000007','血脂','2018-09-01 16:00:00','未见异常'),(24,'2f239f74c000007','血压','2018-09-03 16:00:00','未见异常'),(25,'2f239f74c000007','血糖','2018-09-01 16:00:00','未见异常'),(26,'2f239f74c000007','血糖','2018-09-03 16:00:00','未见异常'),(27,'2f239f74c000007','血脂','2018-09-01 16:00:00','未见异常'),(28,'2f239f74c000007','血压','2018-09-03 16:00:00','未见异常'),(29,'2f237332a000007','血糖','2018-09-01 16:00:00','未见异常'),(30,'2f237332a000007','血糖','2018-09-03 16:00:00','未见异常'),(31,'2f237332a000007','血脂','2018-09-01 16:00:00','未见异常'),(32,'2f237332a000007','血压','2018-09-03 16:00:00','未见异常'),(33,'2f23c6ace000007','血糖','2018-09-01 16:00:00','未见异常'),(34,'2f23c6ace000007','血糖','2018-09-03 16:00:00','未见异常'),(35,'2f23c6ace000007','血脂','2018-09-01 16:00:00','未见异常'),(36,'2f23c6ace000007','血压','2018-09-03 16:00:00','未见异常'),(37,'2f23c6ace000007','血糖','2018-09-01 16:00:00','未见异常'),(38,'2f23c6ace000007','血糖','2018-09-03 16:00:00','未见异常'),(39,'2f23c6ace000007','血脂','2018-09-01 16:00:00','未见异常'),(40,'2f23c6ace000007','血压','2018-09-03 16:00:00','未见异常'),(41,'2f23c6ace000007','血糖','2018-09-01 16:00:00','未见异常'),(42,'2f23c6ace000007','血糖','2018-09-03 16:00:00','未见异常'),(43,'2f23c6ace000007','血脂','2018-09-01 16:00:00','未见异常'),(44,'2f23c6ace000007','血压','2018-09-03 16:00:00','未见异常'),(45,'2f23c6ace000007','血糖','2018-09-01 16:00:00','未见异常'),(46,'2f23c6ace000007','血糖','2018-09-03 16:00:00','未见异常'),(47,'2f23c6ace000007','血脂','2018-09-01 16:00:00','未见异常'),(48,'2f23c6ace000007','血压','2018-09-03 16:00:00','未见异常'),(49,'2f23c6ace000007','血糖','2018-09-01 16:00:00','未见异常'),(50,'2f23c6ace000007','血糖','2018-09-03 16:00:00','未见异常'),(51,'2f23c6ace000007','血脂','2018-09-01 16:00:00','未见异常'),(52,'2f23c6ace000007','血压','2018-09-03 16:00:00','未见异常'),(53,'2f240124d000007','血糖','2018-09-01 16:00:00','未见异常'),(54,'2f240124d000007','血糖','2018-09-03 16:00:00','未见异常'),(55,'2f240124d000007','血脂','2018-09-01 16:00:00','未见异常'),(56,'2f240124d000007','血压','2018-09-03 16:00:00','未见异常'),(57,'2f240d92b000007','血糖','2018-09-01 16:00:00','未见异常'),(58,'2f240d92b000007','血糖','2018-09-03 16:00:00','未见异常'),(59,'2f240d92b000007','血脂','2018-09-01 16:00:00','未见异常'),(60,'2f240d92b000007','血压','2018-09-03 16:00:00','未见异常');
/*!40000 ALTER TABLE `tb_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_reserve`
--

DROP TABLE IF EXISTS `tb_reserve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_reserve` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `dictionary_id` int(11) DEFAULT NULL COMMENT '预约机构ID',
  `institution_id` int(11) DEFAULT NULL COMMENT '机构ID',
  `gender` tinyint(4) DEFAULT NULL COMMENT '性别 0保密 1男 2女',
  `mobile` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '姓名',
  `member_id` int(11) DEFAULT NULL COMMENT '预约人ID',
  `check_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检查日期',
  `status` tinyint(4) DEFAULT '1' COMMENT '预约状态 1)已预约 2）已到检 3）未到检 4) 已过期',
  `reserve_time` timestamp NULL DEFAULT NULL COMMENT '预约时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `member_id_index` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_reserve`
--

LOCK TABLES `tb_reserve` WRITE;
/*!40000 ALTER TABLE `tb_reserve` DISABLE KEYS */;
INSERT INTO `tb_reserve` VALUES (4,2345,1001,1,'123456','kaiz',4567,'2018-08-31 08:06:09',2,'2018-08-31 16:00:00','2018-08-31 08:06:09'),(5,2345,1001,1,'123456','kaiz',4567,'2018-08-31 08:14:50',2,'2018-08-31 16:00:00','2018-08-31 08:14:50'),(6,2345,1001,1,'123456','kaiz',4567,'2018-08-31 08:15:41',2,'2018-08-31 16:00:00','2018-08-31 08:15:42'),(7,2345,1001,1,'123456789','kaiz',4567,'2018-08-31 08:16:44',2,'2018-08-31 16:00:00','2018-08-31 08:16:44'),(8,2345,1001,1,'123456','kaiz',4567,'2018-08-31 08:17:29',2,'2018-08-31 16:00:00','2018-08-31 08:17:29'),(9,2345,1001,1,'123456','kaiz',4567,'2018-08-31 08:21:09',2,'2018-08-31 16:00:00','2018-08-31 08:21:09'),(10,2345,1001,1,'123456789','kaiz',4567,'2018-08-31 09:29:28',1,'2018-08-31 16:00:00','2018-08-31 09:29:29'),(12,2345,1001,1,'123456789','kaiz',4567,'2018-08-31 13:28:04',2,'2018-08-31 16:00:00','2018-08-31 13:28:04'),(13,2345,1001,1,'123123','15088923179',4567,'2018-09-01 07:44:11',2,'2018-09-06 16:00:00','2018-09-01 07:44:12'),(14,2345,1001,1,'123456789','kaiz',4567,'2018-09-01 10:09:23',2,'2018-09-01 16:00:00','2018-09-01 10:09:23'),(15,2347,1001,1,'123456789','kaiz',4567,'2018-09-02 10:02:23',2,'2018-09-19 16:00:00','2018-09-02 10:02:23'),(16,2345,1001,1,'12345678912','kaiz',4567,'2018-09-04 02:04:22',2,'2018-09-04 16:00:00','2018-09-04 02:04:23'),(17,1,1,1,'111111111','raindropsOxO',1,'2018-09-04 05:45:18',1,'2018-09-04 05:45:18','2018-09-04 05:45:18'),(18,1,1,1,'111111111','raindropsOxO',1,'2018-09-04 05:53:44',1,'2018-09-04 05:53:44','2018-09-04 05:53:44'),(19,1,1,1,'111111111','raindropsOxO',1,'2018-09-04 07:44:11',1,'2018-09-04 07:44:11','2018-09-04 07:44:11'),(20,1,1,1,'111111111','raindropsOxO',1,'2018-09-04 08:10:05',1,'2018-09-04 08:10:05','2018-09-04 08:10:05'),(21,1,1,1,'111111111','raindropsOxO',1,'2018-09-04 08:33:10',1,'2018-09-04 08:33:10','2018-09-04 08:33:10'),(22,1,1,1,'111111111','raindropsOxO',1,'2018-09-04 08:57:19',1,'2018-09-04 08:57:19','2018-09-04 08:57:19'),(23,2349,1001,1,'12345678912','kaiz',4567,'2018-09-04 09:51:56',2,'2018-09-04 16:00:00','2018-09-04 09:51:56');
/*!40000 ALTER TABLE `tb_reserve` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_result`
--

DROP TABLE IF EXISTS `tb_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `reserve_id` int(11) DEFAULT NULL COMMENT '预约ID',
  `member_id` int(11) DEFAULT NULL COMMENT '预约人ID',
  `exam_dict_id` int(11) DEFAULT NULL COMMENT '检查项ID',
  `depart_doctor` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '科室医生',
  `result` text COLLATE utf8_unicode_ci COMMENT '检查结果',
  `check_doctor` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '医生',
  `summary` text COLLATE utf8_unicode_ci COMMENT '结论',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '结果状态 1)未出结果 2)已出结果',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='结果表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_result`
--

LOCK TABLES `tb_result` WRITE;
/*!40000 ALTER TABLE `tb_result` DISABLE KEYS */;
INSERT INTO `tb_result` VALUES (1,9,4567,2345,'12121','3123123','21312','1212','2018-08-31 14:02:45',2),(2,4,4567,2345,NULL,'','',NULL,'2018-08-31 15:03:33',2),(3,5,4567,2345,NULL,NULL,NULL,NULL,'2018-08-31 15:03:35',1),(4,1,1,2345,NULL,NULL,NULL,NULL,'2018-08-31 15:03:38',1),(5,8,4567,2345,'周医生','88','王医生','正常','2018-08-31 15:03:40',2),(6,6,4567,2345,'请问我','水电费水电费是','水电费水电费','请问','2018-09-01 07:47:49',2),(7,13,4567,2345,'zz','77777','kk','ok','2018-09-01 10:41:48',2),(8,14,4567,2345,'ZZ','88888','kk','未见异常','2018-09-01 11:24:17',2),(9,7,4567,2345,'赵医生','90','李医生','正常','2018-09-01 11:26:18',2),(10,12,4567,2345,'zz','12345','kk','正常','2018-09-01 12:38:44',2),(11,15,4567,2347,'Zhao','12','KK','未见异常','2018-09-02 10:34:47',2),(12,16,4567,2345,'zz','123','KK','未见异常','2018-09-04 02:08:20',2),(13,23,4567,2349,'赵医生','23456','赵医生','未见异常','2018-09-04 09:59:51',2);
/*!40000 ALTER TABLE `tb_result` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-09-04 22:59:06
