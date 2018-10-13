/*
SQLyog Ultimate v11.33 (64 bit)
MySQL - 5.7.17 : Database - resolvin
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `migrations` */

DROP TABLE IF EXISTS `migrations`;

CREATE TABLE `migrations` (
  `migration` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `migrations` */

insert  into `migrations`(`migration`,`batch`) values ('2014_10_12_000000_create_users_table',1),('2014_10_12_100000_create_password_resets_table',1),('2017_02_02_071031_create_table_bank',1),('2017_02_02_071118_create_table_comment',1),('2017_02_02_071130_create_table_grade',1),('2017_02_02_071141_create_table_material',1),('2017_02_02_071151_create_table_mentor',1),('2017_02_02_071201_create_table_notification',1),('2017_02_02_071209_create_table_package',1),('2017_02_02_071216_create_table_question',1),('2017_02_02_071224_create_table_registertmp',1),('2017_02_02_071233_create_table_solution',1),('2017_02_02_071239_create_table_student',1),('2017_02_02_071244_create_table_subject',1),('2017_02_02_071254_create_table_transaction',1),('2017_02_02_071303_create_table_transactionconfirm',1),('2017_02_04_142059_CreateTableRedeem',1),('2017_02_04_142112_CreateTableBalance',1);

/*Table structure for table `password_resets` */

DROP TABLE IF EXISTS `password_resets`;

CREATE TABLE `password_resets` (
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `password_resets_email_index` (`email`),
  KEY `password_resets_token_index` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `password_resets` */

/*Table structure for table `solvin_balance` */

DROP TABLE IF EXISTS `solvin_balance`;

CREATE TABLE `solvin_balance` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `solution_id` int(10) unsigned NOT NULL,
  `deal_payment` decimal(19,0) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_balance` */

insert  into `solvin_balance`(`id`,`solution_id`,`deal_payment`,`status`,`created_at`,`updated_at`) values (1,1,'1000',1,'2017-02-04 15:29:39','2017-02-04 17:21:33'),(2,1,'1000',1,'2017-02-04 17:01:21','2017-02-04 17:21:33'),(3,1,'1000',1,'2017-02-04 17:01:24','2017-02-04 17:21:33'),(4,2,'1000',1,'2017-02-04 17:01:31','2017-02-04 17:21:33'),(5,3,'1000',1,'2017-02-04 17:01:35','2017-02-04 17:21:33'),(6,4,'1000',1,'2017-02-04 17:01:38','2017-02-04 17:21:33'),(7,5,'1000',1,'2017-02-04 17:02:18','2017-02-04 17:21:34'),(8,6,'1000',1,'2017-02-04 17:02:22','2017-02-04 17:21:34'),(9,7,'1000',1,'2017-02-04 17:02:30','2017-02-04 17:21:34'),(10,7,'1000',1,'2017-02-04 17:02:35','2017-02-04 17:21:34'),(11,8,'1000',0,'2017-02-04 17:04:19','2017-02-04 17:04:19'),(12,9,'1000',0,'2017-02-04 17:04:25','2017-02-04 17:04:25');

/*Table structure for table `solvin_bank` */

DROP TABLE IF EXISTS `solvin_bank`;

CREATE TABLE `solvin_bank` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `account_owner` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `account_number` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `logo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_bank` */

insert  into `solvin_bank`(`id`,`name`,`account_owner`,`account_number`,`logo`,`created_at`,`updated_at`) values (1,'BNI','Solvin','0001-01-12312','bni.png','2017-02-10 17:37:14','2017-02-10 17:37:14'),(2,'BCA','Solvin','0012-01-291923','nca.png','2017-02-10 17:37:14','2017-02-10 17:37:14'),(3,'BRI','Solvin','129-38-219310-9','bri.png','2017-02-10 17:37:14','2017-02-10 17:37:14'),(4,'MANDIRI','Solvin','21-3-1319-12','mandiri.png','2017-02-10 17:37:14','2017-02-10 17:37:14');

/*Table structure for table `solvin_comment` */

DROP TABLE IF EXISTS `solvin_comment`;

CREATE TABLE `solvin_comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` int(10) unsigned NOT NULL,
  `auth_id` int(10) unsigned NOT NULL,
  `auth_type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `content` text COLLATE utf8_unicode_ci NOT NULL,
  `image` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_comment` */

insert  into `solvin_comment`(`id`,`question_id`,`auth_id`,`auth_type`,`content`,`image`,`created_at`,`updated_at`) values (1,9,1,'student','Coba test koment resolvin','','2017-02-11 17:59:59','2017-02-11 17:59:59'),(2,9,1,'mentor','<p >Lorem ipsum hwhja</p>\n','','2017-02-11 17:59:59','2017-02-21 22:52:13'),(3,9,20,'student','Love me like yu du','','2017-02-11 17:59:59','2017-02-11 17:59:59'),(4,7,1,'student','Coba test koment resolvin','','2017-02-21 09:32:21','2017-02-21 09:32:21'),(5,12,20,'student','<p >jwjwjwj</p>\n','','2017-02-22 20:53:17','2017-02-22 20:53:17'),(6,13,20,'student','<p >jbbh</p>\n','','2017-02-24 21:39:01','2017-02-24 21:39:01'),(7,13,1,'student','Coba test koment resolvin','','2017-02-25 13:49:38','2017-02-25 13:49:38'),(8,13,1,'student','Coba test koment resolvin','','2017-02-25 13:51:42','2017-02-25 13:51:42'),(9,13,1,'student','Coba test koment resolvin','','2017-02-25 13:57:22','2017-02-25 13:57:22'),(10,13,1,'student','Coba test koment resolvin','','2017-02-25 14:05:21','2017-02-25 14:05:21'),(11,13,20,'student','<p >yxgxtx</p>\n','','2017-02-25 14:13:14','2017-02-25 14:13:14'),(12,16,1,'mentor','<p >dudjid</p>\n','','2017-02-25 14:52:48','2017-02-25 14:52:48'),(13,16,1,'student','<p >hshahaha</p>\n','','2017-02-25 14:59:16','2017-02-25 14:59:16');

/*Table structure for table `solvin_grade` */

DROP TABLE IF EXISTS `solvin_grade`;

CREATE TABLE `solvin_grade` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_grade` */

insert  into `solvin_grade`(`id`,`name`,`created_at`,`updated_at`) values (1,'SD','2017-02-04 14:40:56','2017-02-04 14:40:56'),(2,'SMP','2017-02-04 14:40:56','2017-02-04 14:40:56'),(3,'SMA','2017-02-04 14:40:56','2017-02-04 14:40:56');

/*Table structure for table `solvin_material` */

DROP TABLE IF EXISTS `solvin_material`;

CREATE TABLE `solvin_material` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `grade_id` int(10) unsigned NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_material` */

insert  into `solvin_material`(`id`,`subject_id`,`grade_id`,`name`,`created_at`,`updated_at`) values (1,1,1,'Bangun Datar','2017-02-04 14:40:56','2017-02-04 14:40:56'),(2,1,1,'Bangun Ruang','2017-02-04 14:40:56','2017-02-04 14:40:56'),(3,1,1,'Bidang Koordinat','2017-02-04 14:40:56','2017-02-04 14:40:56'),(4,1,1,'Bilangan','2017-02-04 14:40:57','2017-02-04 14:40:57'),(5,1,1,'Debit','2017-02-04 14:40:57','2017-02-04 14:40:57'),(6,1,1,'Faktor & Kelipatan','2017-02-04 14:40:57','2017-02-04 14:40:57'),(7,1,1,'Pangkat & Akar Bilangan Bulat','2017-02-04 14:40:57','2017-02-04 14:40:57'),(8,1,1,'Pecahan','2017-02-04 14:40:57','2017-02-04 14:40:57'),(9,1,1,'Pengukuran','2017-02-04 14:40:57','2017-02-04 14:40:57'),(10,1,1,'Penyajian & Pengolahan Data','2017-02-04 14:40:58','2017-02-04 14:40:58'),(11,1,1,'Lainnya','2017-02-04 14:40:58','2017-02-04 14:40:58'),(12,1,2,'Aljabar','2017-02-04 14:40:58','2017-02-04 14:40:58'),(13,1,2,'Aritmatika Sosial','2017-02-04 14:40:58','2017-02-04 14:40:58'),(14,1,2,'Bangun Datar','2017-02-04 14:40:58','2017-02-04 14:40:58'),(15,1,2,'Bangun Ruang','2017-02-04 14:40:58','2017-02-04 14:40:58'),(16,1,2,'Bilangan','2017-02-04 14:40:58','2017-02-04 14:40:58'),(17,1,2,'Eksponen','2017-02-04 14:40:58','2017-02-04 14:40:58'),(18,1,2,'Fungsi','2017-02-04 14:40:58','2017-02-04 14:40:58'),(19,1,2,'Garis Segitiga','2017-02-04 14:40:58','2017-02-04 14:40:58'),(20,1,2,'Garis Sejajar','2017-02-04 14:40:58','2017-02-04 14:40:58'),(21,1,2,'Garis Singgung Lingkaran','2017-02-04 14:40:58','2017-02-04 14:40:58'),(22,1,2,'Himpunan','2017-02-04 14:40:58','2017-02-04 14:40:58'),(23,1,2,'Kesebangunan & Kekongruenan','2017-02-04 14:40:58','2017-02-04 14:40:58'),(24,1,2,'Peluang','2017-02-04 14:40:58','2017-02-04 14:40:58'),(25,1,2,'Perbandingan','2017-02-04 14:40:59','2017-02-04 14:40:59'),(26,1,2,'Persamaan Garis Lurus','2017-02-04 14:40:59','2017-02-04 14:40:59'),(27,1,2,'Persamaan Kuadrat','2017-02-04 14:40:59','2017-02-04 14:40:59'),(28,1,2,'Persamaan Linear','2017-02-04 14:40:59','2017-02-04 14:40:59'),(29,1,2,'Pertidaksamaan','2017-02-04 14:40:59','2017-02-04 14:40:59'),(30,1,2,'Pola Bilangan & Deret','2017-02-04 14:40:59','2017-02-04 14:40:59'),(31,1,2,'Statistika','2017-02-04 14:40:59','2017-02-04 14:40:59'),(32,1,2,'Sudut','2017-02-04 14:40:59','2017-02-04 14:40:59'),(33,1,2,'Teorema Pythagoras','2017-02-04 14:40:59','2017-02-04 14:40:59'),(34,1,2,'Lainnya','2017-02-04 14:40:59','2017-02-04 14:40:59'),(35,1,3,'Barisan & Deret','2017-02-04 14:40:59','2017-02-04 14:40:59'),(36,1,3,'Dimensi Tiga','2017-02-04 14:40:59','2017-02-04 14:40:59'),(37,1,3,'Eksponen','2017-02-04 14:40:59','2017-02-04 14:40:59'),(38,1,3,'Integral','2017-02-04 14:40:59','2017-02-04 14:40:59'),(39,1,3,'Komposisi & Invers Fungsi','2017-02-04 14:40:59','2017-02-04 14:40:59'),(40,1,3,'Limit Fungsi','2017-02-04 14:40:59','2017-02-04 14:40:59'),(41,1,3,'Lingkaran','2017-02-04 14:40:59','2017-02-04 14:40:59'),(42,1,3,'Logaritma','2017-02-04 14:40:59','2017-02-04 14:40:59'),(43,1,3,'Logika Matematika','2017-02-04 14:40:59','2017-02-04 14:40:59'),(44,1,3,'Matriks','2017-02-04 14:41:00','2017-02-04 14:41:00'),(45,1,3,'Peluang','2017-02-04 14:41:00','2017-02-04 14:41:00'),(46,1,3,'Persamaan Kuadrat','2017-02-04 14:41:00','2017-02-04 14:41:00'),(47,1,3,'Persamaan Linear','2017-02-04 14:41:00','2017-02-04 14:41:00'),(48,1,3,'Pertidaksamaan','2017-02-04 14:41:00','2017-02-04 14:41:00'),(49,1,3,'Program Linear','2017-02-04 14:41:00','2017-02-04 14:41:00'),(50,1,3,'Statistika','2017-02-04 14:41:00','2017-02-04 14:41:00'),(51,1,3,'Sukubanyak (Polinomial)','2017-02-04 14:41:00','2017-02-04 14:41:00'),(52,1,3,'Transformasi Geometri','2017-02-04 14:41:00','2017-02-04 14:41:00'),(53,1,3,'Trigonometri','2017-02-04 14:41:00','2017-02-04 14:41:00'),(54,1,3,'Turunan','2017-02-04 14:41:00','2017-02-04 14:41:00'),(55,1,3,'Vektor','2017-02-04 14:41:00','2017-02-04 14:41:00'),(56,1,3,'Lainnya','2017-02-04 14:41:00','2017-02-04 14:41:00'),(57,2,2,'Alat Optik','2017-02-04 14:41:00','2017-02-04 14:41:00'),(58,2,2,'Bunyi','2017-02-04 14:41:00','2017-02-04 14:41:00'),(59,2,2,'Cahaya','2017-02-04 14:41:00','2017-02-04 14:41:00'),(60,2,2,'Daya Listrik','2017-02-04 14:41:00','2017-02-04 14:41:00'),(61,2,2,'Energi Listrik','2017-02-04 14:41:00','2017-02-04 14:41:00'),(62,2,2,'Gerak Lurus','2017-02-04 14:41:00','2017-02-04 14:41:00'),(63,2,2,'Getaran & Gelombang','2017-02-04 14:41:00','2017-02-04 14:41:00'),(64,2,2,'Hukum Newton','2017-02-04 14:41:00','2017-02-04 14:41:00'),(65,2,2,'Induksi Elektromagnetik','2017-02-04 14:41:01','2017-02-04 14:41:01'),(66,2,2,'Kalor','2017-02-04 14:41:01','2017-02-04 14:41:01'),(67,2,2,'Kemagnetan','2017-02-04 14:41:01','2017-02-04 14:41:01'),(68,2,2,'Listrik Dinamis','2017-02-04 14:41:01','2017-02-04 14:41:01'),(69,2,2,'Listrik Statis','2017-02-04 14:41:01','2017-02-04 14:41:01'),(70,2,2,'Pemuaian','2017-02-04 14:41:01','2017-02-04 14:41:01'),(71,2,2,'Pengukuran','2017-02-04 14:41:01','2017-02-04 14:41:01'),(72,2,2,'Sistem Tata Surya','2017-02-04 14:41:01','2017-02-04 14:41:01'),(73,2,2,'Suhu','2017-02-04 14:41:01','2017-02-04 14:41:01'),(74,2,2,'Sumber Arus Listrik','2017-02-04 14:41:01','2017-02-04 14:41:01'),(75,2,2,'Tekanan','2017-02-04 14:41:01','2017-02-04 14:41:01'),(76,2,2,'Usaha & Energi','2017-02-04 14:41:01','2017-02-04 14:41:01'),(77,2,2,'Zat & Wujudnya','2017-02-04 14:41:01','2017-02-04 14:41:01'),(78,2,2,'Lainnya','2017-02-04 14:41:01','2017-02-04 14:41:01'),(79,2,3,'Besaran & Satuan','2017-02-04 14:41:01','2017-02-04 14:41:01'),(80,2,3,'Dinamika Rotasi','2017-02-04 14:41:01','2017-02-04 14:41:01'),(81,2,3,'Elastisitas','2017-02-04 14:41:01','2017-02-04 14:41:01'),(82,2,3,'Fisika Atom','2017-02-04 14:41:01','2017-02-04 14:41:01'),(83,2,3,'Fisika Inti','2017-02-04 14:41:01','2017-02-04 14:41:01'),(84,2,3,'Gelombang','2017-02-04 14:41:02','2017-02-04 14:41:02'),(85,2,3,'Gerak Harmonik Sederhana','2017-02-04 14:41:02','2017-02-04 14:41:02'),(86,2,3,'Gerak Lurus','2017-02-04 14:41:02','2017-02-04 14:41:02'),(87,2,3,'Gerak Melingkar','2017-02-04 14:41:02','2017-02-04 14:41:02'),(88,2,3,'Hukum Newton','2017-02-04 14:41:02','2017-02-04 14:41:02'),(89,2,3,'Impuls','2017-02-04 14:41:02','2017-02-04 14:41:02'),(90,2,3,'Induksi Elektromagnetik','2017-02-04 14:41:02','2017-02-04 14:41:02'),(91,2,3,'Kalor','2017-02-04 14:41:02','2017-02-04 14:41:02'),(92,2,3,'Keseimbangan Benda Tegar','2017-02-04 14:41:02','2017-02-04 14:41:02'),(93,2,3,'Listrik Dinamis','2017-02-04 14:41:02','2017-02-04 14:41:02'),(94,2,3,'Listrik Statis','2017-02-04 14:41:02','2017-02-04 14:41:02'),(95,2,3,'Medan Magnetik','2017-02-04 14:41:02','2017-02-04 14:41:02'),(96,2,3,'Mekanika Fluida','2017-02-04 14:41:02','2017-02-04 14:41:02'),(97,2,3,'Momentum','2017-02-04 14:41:02','2017-02-04 14:41:02'),(98,2,3,'Optik','2017-02-04 14:41:02','2017-02-04 14:41:02'),(99,2,3,'Radiasi Benda Hitam','2017-02-04 14:41:02','2017-02-04 14:41:02'),(100,2,3,'Suhu','2017-02-04 14:41:02','2017-02-04 14:41:02'),(101,2,3,'Teori Kinetik Gas','2017-02-04 14:41:02','2017-02-04 14:41:02'),(102,2,3,'Teori Kuantum','2017-02-04 14:41:02','2017-02-04 14:41:02'),(103,2,3,'Teori Relativitas Khusus','2017-02-04 14:41:02','2017-02-04 14:41:02'),(104,2,3,'Termodinamika','2017-02-04 14:41:03','2017-02-04 14:41:03'),(105,2,3,'Usaha & Energi','2017-02-04 14:41:03','2017-02-04 14:41:03'),(106,2,3,'Vektor','2017-02-04 14:41:03','2017-02-04 14:41:03'),(107,2,3,'Lainnya','2017-02-04 14:41:03','2017-02-04 14:41:03');

/*Table structure for table `solvin_mentor` */

DROP TABLE IF EXISTS `solvin_mentor`;

CREATE TABLE `solvin_mentor` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `photo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `address` text COLLATE utf8_unicode_ci NOT NULL,
  `birth` date NOT NULL,
  `device_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `workplace` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `firebase_token` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `member_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `remember_token` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `solvin_mentor_email_unique` (`email`),
  UNIQUE KEY `solvin_mentor_phone_unique` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_mentor` */

insert  into `solvin_mentor`(`id`,`name`,`email`,`password`,`phone`,`photo`,`address`,`birth`,`device_id`,`workplace`,`firebase_token`,`member_code`,`active`,`remember_token`,`created_at`,`updated_at`) values (1,'Edo','mentor1@solvin.dev','$2y$10$28Rn2fEVWzSKFACeIs6YQeqkgGEntaAqwtP2FrSgI7TxRhGD4nEOS','081310000001','','','1994-11-14','','','f_yAFImaPD0:APA91bEAkVmLkgCXxDmm7NN6Llo2Qa3ecPTEFGLmTaZGqI1raGo2cCTpbQpSSe6dNrqD-txNlXd4QZiHiHnltabxRcBFvcnjzF9juuBp302hrFudxnDUfltlSfMfX9UNM1f8kyXgvM-I',NULL,1,'','2017-02-04 13:16:00','2017-02-25 14:17:28');

/*Table structure for table `solvin_notification` */

DROP TABLE IF EXISTS `solvin_notification`;

CREATE TABLE `solvin_notification` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `auth_id` int(10) unsigned NOT NULL,
  `subject_id` int(10) unsigned NOT NULL,
  `status` smallint(6) NOT NULL DEFAULT '0',
  `type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `auth_type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `subject_type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `content` text COLLATE utf8_unicode_ci NOT NULL,
  `sender_id` int(10) unsigned NOT NULL,
  `sender_type` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_notification` */

insert  into `solvin_notification`(`id`,`auth_id`,`subject_id`,`status`,`type`,`auth_type`,`subject_type`,`content`,`sender_id`,`sender_type`,`created_at`,`updated_at`) values (1,1,1,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 15:23:13','2017-02-04 15:23:13'),(2,1,1,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 15:24:50','2017-02-04 15:24:50'),(3,1,1,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 15:29:39','2017-02-04 15:29:39'),(4,1,2,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:55:37','2017-02-04 16:55:37'),(5,1,3,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:55:42','2017-02-04 16:55:42'),(6,1,4,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:55:46','2017-02-04 16:55:46'),(7,1,5,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:55:51','2017-02-04 16:55:51'),(8,1,6,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:55:57','2017-02-04 16:55:57'),(9,1,7,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:56:16','2017-02-04 16:56:16'),(10,1,8,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:56:23','2017-02-04 16:56:23'),(11,1,9,0,'question','mentor','question','edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-04 16:56:50','2017-02-04 16:56:50'),(12,1,2,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:13','2017-02-04 17:00:13'),(13,1,3,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:26','2017-02-04 17:00:26'),(14,1,4,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:29','2017-02-04 17:00:29'),(15,1,5,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:32','2017-02-04 17:00:32'),(16,1,6,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:34','2017-02-04 17:00:34'),(17,1,7,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:37','2017-02-04 17:00:37'),(18,1,8,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:39','2017-02-04 17:00:39'),(19,1,9,0,'solution','student','question','Dani mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-04 17:00:42','2017-02-04 17:00:42'),(20,1,1,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:01:21','2017-02-04 17:01:21'),(21,1,1,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:01:24','2017-02-04 17:01:24'),(22,1,2,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:01:31','2017-02-04 17:01:31'),(23,1,3,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:01:35','2017-02-04 17:01:35'),(24,1,4,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:01:38','2017-02-04 17:01:38'),(25,1,5,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:02:18','2017-02-04 17:02:18'),(26,1,6,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:02:22','2017-02-04 17:02:22'),(27,1,7,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:02:30','2017-02-04 17:02:30'),(28,1,7,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:02:35','2017-02-04 17:02:35'),(29,1,8,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:04:19','2017-02-04 17:04:19'),(30,1,9,0,'solution','mentor','question','edinofri menandai solusi anda sebagai yang terbaik',1,'student','2017-02-04 17:04:25','2017-02-04 17:04:25'),(31,1,10,0,'question','mentor','question','Edinofri mengajukan pertanyaan Materimatika terbaru',1,'student','2017-02-13 21:00:08','2017-02-13 21:00:08'),(32,1,11,0,'question','mentor','question','Edinofri mengajukan pertanyaan Fisika terbaru',1,'student','2017-02-13 21:06:14','2017-02-13 21:06:14'),(33,1,12,0,'question','mentor','question','Ramadiansyah mengajukan pertanyaan Materimatika terbaru',20,'student','2017-02-22 20:14:37','2017-02-22 20:14:37'),(34,1,13,0,'question','mentor','question','Ramadiansyah mengajukan pertanyaan Fisika terbaru',20,'student','2017-02-24 21:12:38','2017-02-24 21:12:38'),(35,20,13,0,'solution','student','question','Edo mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-25 13:47:02','2017-02-25 13:47:02'),(36,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:49:37','2017-02-25 13:49:37'),(37,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:51:42','2017-02-25 13:51:42'),(38,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:55:55','2017-02-25 13:55:55'),(39,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:56:08','2017-02-25 13:56:08'),(40,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:56:18','2017-02-25 13:56:18'),(41,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:56:44','2017-02-25 13:56:44'),(42,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:57:15','2017-02-25 13:57:15'),(43,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 13:57:22','2017-02-25 13:57:22'),(44,20,13,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 14:05:20','2017-02-25 14:05:20'),(45,20,12,0,'solution','student','question','Edo mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-25 14:25:10','2017-02-25 14:25:10'),(46,1,14,0,'question','mentor','question','Paklek mengajukan pertanyaan Materimatika terbaru',20,'student','2017-02-25 14:29:18','2017-02-25 14:29:18'),(47,20,14,0,'solution','student','question','Edo mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-25 14:29:57','2017-02-25 14:29:57'),(48,1,15,0,'question','mentor','question','Paklek mengajukan pertanyaan Materimatika terbaru',20,'student','2017-02-25 14:31:06','2017-02-25 14:31:06'),(49,20,15,0,'solution','student','question','Edo mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-25 14:31:28','2017-02-25 14:31:28'),(50,1,16,0,'question','mentor','question','Paklek mengajukan pertanyaan Fisika terbaru',20,'student','2017-02-25 14:32:01','2017-02-25 14:32:01'),(51,20,16,0,'solution','student','question','Edo mengajukan solusi atas pertanyaan anda',1,'mentor','2017-02-25 14:45:42','2017-02-25 14:45:42'),(52,20,16,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'mentor','2017-02-25 14:52:47','2017-02-25 14:52:47'),(53,20,16,0,'comment','student','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 14:59:15','2017-02-25 14:59:15'),(54,1,16,0,'comment','mentor','question','Pertanyaan anda dikomentari.',1,'student','2017-02-25 14:59:16','2017-02-25 14:59:16');

/*Table structure for table `solvin_package` */

DROP TABLE IF EXISTS `solvin_package`;

CREATE TABLE `solvin_package` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nominal` decimal(19,0) NOT NULL,
  `active` int(10) unsigned NOT NULL,
  `credit` int(10) unsigned NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_package` */

insert  into `solvin_package`(`id`,`nominal`,`active`,`credit`,`created_at`,`updated_at`) values (1,'20000',7,10,'2017-02-10 17:36:47','2017-02-10 17:36:47'),(2,'50000',15,25,'2017-02-10 17:36:47','2017-02-10 17:36:47'),(3,'80000',30,40,'2017-02-10 17:36:47','2017-02-10 17:36:47');

/*Table structure for table `solvin_question` */

DROP TABLE IF EXISTS `solvin_question`;

CREATE TABLE `solvin_question` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `student_id` int(10) unsigned NOT NULL,
  `status` int(11) NOT NULL DEFAULT '-1',
  `material_id` int(10) unsigned NOT NULL,
  `image` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `content` text COLLATE utf8_unicode_ci NOT NULL,
  `other` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_question` */

insert  into `solvin_question`(`id`,`student_id`,`status`,`material_id`,`image`,`content`,`other`,`created_at`,`updated_at`) values (1,1,1,2,'1486196593-1-2.jpg','Just a test','','2017-02-04 15:23:13','2017-02-04 15:25:20'),(2,1,1,3,'1486202137-1-3.jpg','Pertanyaan Baru','','2017-02-04 16:55:37','2017-02-04 17:01:31'),(3,1,1,4,'1486202142-1-4.jpg','Pertanyaan Baru 1','','2017-02-04 16:55:42','2017-02-04 17:01:35'),(4,1,1,5,'1486202145-1-5.jpg','Pertanyaan Baru 2','','2017-02-04 16:55:45','2017-02-04 17:01:37'),(5,1,1,6,'1486202151-1-6.jpg','Pertanyaan Baru 8','','2017-02-04 16:55:51','2017-02-04 17:02:17'),(6,1,1,12,'1486202157-1-12.jpg','Pertanyaan Baru 12','','2017-02-04 16:55:57','2017-02-04 17:02:22'),(7,1,1,12,'1486202176-1-12.jpg','Pertanyaan Baru 12','binary','2017-02-04 16:56:16','2017-02-04 17:02:30'),(8,1,0,12,'1486202183-1-12.jpg','Pertanyaan Baru 32','','2017-02-04 16:56:23','2017-02-04 17:04:19'),(9,1,0,11,'1486202210-1-11.jpg','Pertanyaan Baru 32','binary','2017-02-04 16:56:50','2017-02-04 17:04:25'),(10,1,1,4,'','<p>satu kali satu smaa dengan</p>\n','','2017-02-13 21:00:08','2017-02-13 21:00:08'),(11,1,1,75,'','<p >Dunihaha</p>\n','Lorem ipsum','2017-02-13 21:06:14','2017-02-17 00:39:56'),(12,20,0,15,'','<p>prikitiw</p>\n','','2017-02-22 20:14:36','2017-02-25 14:25:10'),(13,20,0,63,'','<p>gufyiyydoyktdb vuuvyvvy</p>\n','','2017-02-24 21:12:37','2017-02-25 13:47:02'),(14,20,0,15,'','<p>kejwjsisi</p>\n','','2017-02-25 14:29:18','2017-02-25 14:29:57'),(15,20,0,15,'','<p>kejwjsisi</p>\n','','2017-02-25 14:31:06','2017-02-25 14:31:29'),(16,20,0,60,'','<p>bcsbx0bidbidndincdncnsnsonoww</p>\n','','2017-02-25 14:32:01','2017-02-25 14:45:51');

/*Table structure for table `solvin_redeem_balance` */

DROP TABLE IF EXISTS `solvin_redeem_balance`;

CREATE TABLE `solvin_redeem_balance` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mentor_id` int(10) unsigned NOT NULL,
  `redeem_code` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `balance` decimal(19,0) NOT NULL,
  `status` smallint(5) unsigned NOT NULL DEFAULT '0',
  `date_agreement` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_redeem_balance` */

insert  into `solvin_redeem_balance`(`id`,`mentor_id`,`redeem_code`,`balance`,`status`,`date_agreement`,`created_at`,`updated_at`) values (1,1,'SOL.0001','10000',1,'2017-02-04 17:21:33','2017-02-04 17:12:40','2017-02-04 17:21:34');

/*Table structure for table `solvin_register_tmp` */

DROP TABLE IF EXISTS `solvin_register_tmp`;

CREATE TABLE `solvin_register_tmp` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `phone` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `code` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_register_tmp` */

insert  into `solvin_register_tmp`(`id`,`phone`,`code`,`status`,`created_at`,`updated_at`) values (1,'082368338355','08358',1,'2017-02-04 15:21:45','2017-02-04 15:22:15'),(2,'082368338356','00838',1,'2017-02-10 18:38:58','2017-02-10 18:39:17'),(3,'082368338357','09749',1,'2017-02-10 19:42:50','2017-02-10 19:43:13'),(4,'082368338358','02354',1,'2017-02-11 14:59:57','2017-02-11 15:00:49'),(5,'08236833','07346',1,'2017-02-11 15:18:14','2017-02-11 15:18:32'),(6,'081212312312','03682',1,'2017-02-11 18:29:11','2017-02-11 18:29:27'),(7,'085268338355','08637',0,'2017-02-25 14:54:39','2017-02-25 14:54:39'),(8,'082368338376','02962',1,'2017-02-25 14:55:19','2017-02-25 14:56:18');

/*Table structure for table `solvin_security_code` */

DROP TABLE IF EXISTS `solvin_security_code`;

CREATE TABLE `solvin_security_code` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `solvin_security_code` */

/*Table structure for table `solvin_solution` */

DROP TABLE IF EXISTS `solvin_solution`;

CREATE TABLE `solvin_solution` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mentor_id` int(10) unsigned NOT NULL,
  `content` text COLLATE utf8_unicode_ci NOT NULL,
  `image` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `question_id` int(10) unsigned NOT NULL,
  `best` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_solution` */

insert  into `solvin_solution`(`id`,`mentor_id`,`content`,`image`,`question_id`,`best`,`created_at`,`updated_at`) values (1,1,'Just The way you are','',1,1,'2017-02-04 15:24:49','2017-02-04 17:01:24'),(2,1,'Jawaban 1','',2,1,'2017-02-04 17:00:13','2017-02-04 17:01:31'),(3,1,'Jawaban 2','',3,1,'2017-02-04 17:00:26','2017-02-04 17:01:35'),(4,1,'Jawaban 3','',4,1,'2017-02-04 17:00:29','2017-02-04 17:01:38'),(5,1,'Jawaban 3','',5,1,'2017-02-04 17:00:32','2017-02-04 17:02:17'),(6,1,'Jawaban 3','',6,1,'2017-02-04 17:00:34','2017-02-04 17:02:22'),(7,1,'Jawaban 3','',7,1,'2017-02-04 17:00:37','2017-02-04 17:02:35'),(8,1,'Jawaban 3','',8,1,'2017-02-04 17:00:39','2017-02-04 17:04:19'),(9,1,'<p >Jawaban 3shshsh</p>\n','',9,1,'2017-02-04 17:00:42','2017-02-21 22:51:48'),(10,1,'Cuma jawaban sahaja','',13,0,'2017-02-25 13:47:01','2017-02-25 13:47:01'),(11,1,'<p >xjxjxjx</p>\n','',12,0,'2017-02-25 14:25:10','2017-02-25 14:25:10'),(12,1,'<p >xnxnx</p>\n','',14,0,'2017-02-25 14:29:57','2017-02-25 14:29:57'),(13,1,'<p >cydu</p>\n','',15,0,'2017-02-25 14:31:29','2017-02-25 14:31:29'),(14,1,'<p >xhhx</p>\n','',16,0,'2017-02-25 14:45:50','2017-02-25 14:45:50');

/*Table structure for table `solvin_student` */

DROP TABLE IF EXISTS `solvin_student`;

CREATE TABLE `solvin_student` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `photo` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `address` text COLLATE utf8_unicode_ci NOT NULL,
  `birth` date NOT NULL,
  `device_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `school` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `member_code` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `credit` int(11) NOT NULL DEFAULT '0',
  `credit_timelife` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `firebase_token` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `remember_token` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `solvin_student_email_unique` (`email`),
  UNIQUE KEY `solvin_student_phone_unique` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_student` */

insert  into `solvin_student`(`id`,`name`,`email`,`password`,`phone`,`photo`,`address`,`birth`,`device_id`,`school`,`member_code`,`credit`,`credit_timelife`,`firebase_token`,`active`,`remember_token`,`created_at`,`updated_at`) values (1,'Edinofri','tukangbasic@gmail.com','$2y$10$cWGbCTGNBIPdBJ87.mFr8.oKjVKZjMOCV.MXlYP0EKHBw7NGo4rrG','082368338355','','','0000-00-00','','','edi1010001',3,'2017-02-22 19:56:00','f_yAFImaPD0:APA91bEAkVmLkgCXxDmm7NN6Llo2Qa3ecPTEFGLmTaZGqI1raGo2cCTpbQpSSe6dNrqD-txNlXd4QZiHiHnltabxRcBFvcnjzF9juuBp302hrFudxnDUfltlSfMfX9UNM1f8kyXgvM-I',1,NULL,'2017-02-04 15:22:22','2017-02-13 21:06:14'),(20,'Paklek','user1@solvin.dev','$2y$10$dMQN0MERlaLKhRiIYClehOi73WpHkv9IhDPMXtf9uC7KmZq9u.7/i','082368338356','','Jlan pon 3 nomor 25','1994-01-01','','Mikroskil','Ram0020',8,'2017-02-25 14:32:01','f_yAFImaPD0:APA91bEAkVmLkgCXxDmm7NN6Llo2Qa3ecPTEFGLmTaZGqI1raGo2cCTpbQpSSe6dNrqD-txNlXd4QZiHiHnltabxRcBFvcnjzF9juuBp302hrFudxnDUfltlSfMfX9UNM1f8kyXgvM-I',1,NULL,'2017-02-10 18:54:52','2017-02-25 14:32:01'),(21,'Eko Prayetno','user2@solvin.dev','$2y$10$nixFUI27pQwiE6p9YPy86ea5qm39ZX2Usq/8QVokEvZXjj2BbuXAu','082368338357','','','0000-00-00','','','Eko0021',3,'2017-02-10 19:47:31','$2y$10$eazKxRo3VJQ9uDDwzk.C/OPYAoTEpjEROJULGAtr2.mKIsWf7RbP.',1,NULL,'2017-02-10 19:43:35','2017-02-10 19:47:31'),(22,'Edan','user3@solvin.dev','$2y$10$3nqi0sqP.7jw5uxHn0wbqOtE2m5eLvbSTlyZdjWKW1R0sdTzJsNDe','082368338358','','','0000-00-00','','','RES0022',3,'2017-02-12 12:42:28','dxUAmoCIO6w:APA91bGObHH3jjB68EE5Ax4ueydPvwEMyofeH0uMXNKi608a4jzslKWfKHOWZ5xt3Y9i8cPqaZPaxjYEVHF1xr6T2R7WMq56vK2HXM25mazhM8YhLs-dTPGShPd2elstaUtXH5NSAotz',1,NULL,'2017-02-11 15:02:16','2017-02-11 15:02:16'),(23,'Edwar','user4@solvin.dev','$2y$10$IJVmwj6sHEdTyXKJ.5ksdezW5HdCNTEG6KP93gDPj6Jj7VdeWF1W6','08236833','','','0000-00-00','','','PEN0023',3,'2017-02-12 12:42:34','dxUAmoCIO6w:APA91bGObHH3jjB68EE5Ax4ueydPvwEMyofeH0uMXNKi608a4jzslKWfKHOWZ5xt3Y9i8cPqaZPaxjYEVHF1xr6T2R7WMq56vK2HXM25mazhM8YhLs-dTPGShPd2elstaUtXH5NSAotz',1,NULL,'2017-02-11 15:19:07','2017-02-11 15:19:07'),(24,'Penabesi','user5@solvin.dev','$2y$10$JbEoSNFTzfeEPgQMe2so.uPcdgLj2dNQi4.34wbvWpEz4nNR/ibQu','081212312312','','','0000-00-00','','','PEN0024',3,'2017-02-11 18:31:27','f_yAFImaPD0:APA91bEAkVmLkgCXxDmm7NN6Llo2Qa3ecPTEFGLmTaZGqI1raGo2cCTpbQpSSe6dNrqD-txNlXd4QZiHiHnltabxRcBFvcnjzF9juuBp302hrFudxnDUfltlSfMfX9UNM1f8kyXgvM-I',1,NULL,'2017-02-11 18:31:27','2017-02-11 18:31:27');

/*Table structure for table `solvin_subject` */

DROP TABLE IF EXISTS `solvin_subject`;

CREATE TABLE `solvin_subject` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_subject` */

insert  into `solvin_subject`(`id`,`name`,`created_at`,`updated_at`) values (1,'Materimatika','2017-02-04 14:40:56','2017-02-04 14:40:56'),(2,'Fisika','2017-02-04 14:40:56','2017-02-04 14:40:56');

/*Table structure for table `solvin_transaction` */

DROP TABLE IF EXISTS `solvin_transaction`;

CREATE TABLE `solvin_transaction` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `package_id` int(10) unsigned NOT NULL,
  `student_id` int(10) unsigned NOT NULL,
  `bank_id` int(10) unsigned NOT NULL,
  `unique_code` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `status` smallint(6) NOT NULL DEFAULT '0',
  `payment_time` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `clear` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_transaction` */

/*Table structure for table `solvin_transaction_confirm` */

DROP TABLE IF EXISTS `solvin_transaction_confirm`;

CREATE TABLE `solvin_transaction_confirm` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` int(10) unsigned NOT NULL,
  `bank_account_owner` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `bank_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `image` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `solvin_transaction_confirm` */

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `remember_token` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_email_unique` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `users` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
