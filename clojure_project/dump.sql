/*
SQLyog Community v12.09 (64 bit)
MySQL - 5.6.17 : Database - sandrafruit
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`sandrafruit` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `sandrafruit`;

/*Table structure for table `fruit` */

DROP TABLE IF EXISTS `fruit`;

CREATE TABLE `fruit` (
  `id` int(11) NOT NULL,
  `name` varchar(10) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `unit` varchar(10) DEFAULT NULL,
  `descent` varchar(15) DEFAULT NULL,
  `currency` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `fruit` */

insert  into `fruit`(`id`,`name`,`price`,`quantity`,`unit`,`descent`,`currency`) values (1,'pomegranat',130,2,'kg','serbia','RSD'),(2,'apple',90,6,'kg','serbia','RSD'),(3,'orange',120,8,'kg','greece','RSD'),(4,'pear',140,9,'kg','serbia','RSD');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
