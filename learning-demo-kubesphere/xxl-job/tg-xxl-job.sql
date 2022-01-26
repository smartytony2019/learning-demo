/*
SQLyog Trial v13.1.8 (64 bit)
MySQL - 5.7.36 : Database - tg_xxl_job
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`tg_xxl_job` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `tg_xxl_job`;

/*Table structure for table `xxl_job_group` */

DROP TABLE IF EXISTS `xxl_job_group`;

CREATE TABLE `xxl_job_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
  `title` varchar(12) NOT NULL COMMENT '执行器名称',
  `order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` varchar(512) DEFAULT NULL COMMENT '执行器地址列表，多地址逗号分隔',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_group` */

insert  into `xxl_job_group`(`id`,`app_name`,`title`,`order`,`address_type`,`address_list`) values 
(1,'xxl-job-executor-sample','示例执行器',1,0,NULL),
(2,'k8s-jobs','k8s-jobs',0,0,'http://k8s-jobs.tg1:20084/');

/*Table structure for table `xxl_job_info` */

DROP TABLE IF EXISTS `xxl_job_info`;

CREATE TABLE `xxl_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_cron` varchar(128) NOT NULL COMMENT '任务执行CRON',
  `job_desc` varchar(255) NOT NULL,
  `add_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `glue_type` varchar(50) NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
  `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
  `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `trigger_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_info` */

insert  into `xxl_job_info`(`id`,`job_group`,`job_cron`,`job_desc`,`add_time`,`update_time`,`author`,`alarm_email`,`executor_route_strategy`,`executor_handler`,`executor_param`,`executor_block_strategy`,`executor_timeout`,`executor_fail_retry_count`,`glue_type`,`glue_source`,`glue_remark`,`glue_updatetime`,`child_jobid`,`trigger_status`,`trigger_last_time`,`trigger_next_time`) values 
(1,1,'0 0 0 * * ? *','测试任务1','2018-11-03 22:21:31','2018-11-03 22:21:31','XXL','','FIRST','demoJobHandler','','SERIAL_EXECUTION',0,0,'BEAN','','GLUE代码初始化','2018-11-03 22:21:31','',0,0,0),
(2,2,'*/5 * * * * ?','调度测试','2022-01-22 00:27:44','2022-01-22 00:27:44','xx','','FIRST','start','','SERIAL_EXECUTION',0,0,'BEAN','','GLUE代码初始化','2022-01-22 00:27:44','',0,0,0);

/*Table structure for table `xxl_job_lock` */

DROP TABLE IF EXISTS `xxl_job_lock`;

CREATE TABLE `xxl_job_lock` (
  `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_lock` */

insert  into `xxl_job_lock`(`lock_name`) values 
('schedule_lock');

/*Table structure for table `xxl_job_log` */

DROP TABLE IF EXISTS `xxl_job_log`;

CREATE TABLE `xxl_job_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_group` int(11) NOT NULL COMMENT '执行器主键ID',
  `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
  `executor_fail_retry_count` int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
  `trigger_code` int(11) NOT NULL COMMENT '调度-结果',
  `trigger_msg` text COMMENT '调度-日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
  `handle_code` int(11) NOT NULL COMMENT '执行-状态',
  `handle_msg` text COMMENT '执行-日志',
  `alarm_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`id`),
  KEY `I_trigger_time` (`trigger_time`),
  KEY `I_handle_code` (`handle_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_log` */

insert  into `xxl_job_log`(`id`,`job_group`,`job_id`,`executor_address`,`executor_handler`,`executor_param`,`executor_sharding_param`,`executor_fail_retry_count`,`trigger_time`,`trigger_code`,`trigger_msg`,`handle_time`,`handle_code`,`handle_msg`,`alarm_status`) values 
(1,2,2,'http://k8s-jobs.tg1:20084/','start','',NULL,0,'2022-01-22 00:27:49',200,'任务触发类型：手动触发<br>调度机器：10.233.82.223<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://k8s-jobs.tg1:20084/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://k8s-jobs.tg1:20084/<br>code：200<br>msg：null','2022-01-22 00:27:50',200,'',0),
(2,2,2,'http://k8s-jobs.tg1:20084/','start','',NULL,0,'2022-01-22 00:27:59',200,'任务触发类型：手动触发<br>调度机器：10.233.82.223<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://k8s-jobs.tg1:20084/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://k8s-jobs.tg1:20084/<br>code：200<br>msg：null','2022-01-22 00:27:59',200,'',0);

/*Table structure for table `xxl_job_log_report` */

DROP TABLE IF EXISTS `xxl_job_log_report`;

CREATE TABLE `xxl_job_log_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trigger_day` datetime DEFAULT NULL COMMENT '调度-时间',
  `running_count` int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
  `suc_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
  `fail_count` int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_log_report` */

insert  into `xxl_job_log_report`(`id`,`trigger_day`,`running_count`,`suc_count`,`fail_count`) values 
(1,'2022-01-21 00:00:00',0,0,0),
(2,'2022-01-20 00:00:00',0,0,0),
(3,'2022-01-19 00:00:00',0,0,0),
(4,'2022-01-22 00:00:00',0,2,0),
(5,'2022-01-24 00:00:00',0,0,0),
(6,'2022-01-23 00:00:00',0,0,0);

/*Table structure for table `xxl_job_logglue` */

DROP TABLE IF EXISTS `xxl_job_logglue`;

CREATE TABLE `xxl_job_logglue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_id` int(11) NOT NULL COMMENT '任务，主键ID',
  `glue_type` varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
  `add_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_logglue` */

/*Table structure for table `xxl_job_registry` */

DROP TABLE IF EXISTS `xxl_job_registry`;

CREATE TABLE `xxl_job_registry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `registry_group` varchar(50) NOT NULL,
  `registry_key` varchar(255) NOT NULL,
  `registry_value` varchar(255) NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_registry` */

insert  into `xxl_job_registry`(`id`,`registry_group`,`registry_key`,`registry_value`,`update_time`) values 
(7,'EXECUTOR','k8s-jobs','http://k8s-jobs.tg1:20084/','2022-01-26 22:16:16');

/*Table structure for table `xxl_job_user` */

DROP TABLE IF EXISTS `xxl_job_user`;

CREATE TABLE `xxl_job_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `role` tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
  `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Data for the table `xxl_job_user` */

insert  into `xxl_job_user`(`id`,`username`,`password`,`role`,`permission`) values 
(1,'admin','e10adc3949ba59abbe56e057f20f883e',1,NULL),
(2,'guest','084e0343a0486ff05530df6c705c8bb4',0,'4,5,2,3,1');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
