--
-- Table structure for table `mm_account`
--
CREATE TABLE `mm_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `mobile` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `encrypted_pwd` varchar(100) DEFAULT NULL,
  `role_type` int(2) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ACCOUNT_MOBILE_UNIQUE` (`mobile`),
  UNIQUE KEY `ACCOUNT_NAME_UNIQUE` (`name`),
  UNIQUE KEY `ACCOUNT_EMAIL_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Data for table `mm_account`
--
LOCK TABLES `mm_account` WRITE;
INSERT INTO `mm_account` VALUES (1, '云众可信', '', 'yzkx@cloudcrowd.com.cn', NULL, 1, 0, sysdate(), sysdate());
UNLOCK TABLES;

--
-- Table structure for table `mm_website`
--
CREATE TABLE `mm_website` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `api_key` varchar(64) NOT NULL,
  `web_key` varchar(64) NOT NULL,
  `sec_level` int(2) DEFAULT NULL,
  `sec_mode` int(2) DEFAULT NULL,
  `theme_num` int(2) DEFAULT NULL,
  `scaling_ratio` decimal(2,1) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `WEBSITE_API_KEY_UNIQUE` (`api_key`),
  UNIQUE KEY `WEBSITE_WEB_KEY_UNIQUE` (`web_key`),
  KEY `WEBSITE_ACCOUNT_ID_IND` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Data for table `mm_website`
--
LOCK TABLES `mm_website` WRITE;
INSERT INTO `mm_website` (`account_id`, `url`, `api_key`, `web_key`, `sec_level`, `sec_mode`, `theme_num`, `scaling_ratio`, `state`, `created_at`, `updated_at`)
VALUES (1, 'www.cloudcrowd.com.cn', '0fcf531775584889bbdcabb35f6c1080', 'bd2b9468833b4043beeb1b8624b5d76c', 3, 0, NULL, 1.5, 0, sysdate(), sysdate()),
	(1, 'www.sec-in.com', 'f3c387fff4fc470e872590dc7414f493', 'b30a6b9baa444d6c98a21cf37eee8897', 3, 0, NULL, 1.5, 0, sysdate(), sysdate()),
	(1, '*.cloudcrowd.com.cn', '3488413af5f644569536f39b1e05ee37', '643b1dd9ddb2477d9963f036a988ed7d', 1, 0, NULL, 1.5,0, sysdate(), sysdate()),
	(1, '*.sec-in.com', '51d74ae35d9e40b5b8f883f9b650f2f0', 'b7db153be64749799ab48a61ebcf7a1c', 1, 0, NULL, 1.5, 0, sysdate(), sysdate());
UNLOCK TABLES;

--
-- Table structure for table `mm_theme`
--
CREATE TABLE `mm_theme` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `sr_no` int(2) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `THEME_SR_NO_UNIQ_IND` (`sr_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `mm_background`
--
CREATE TABLE `mm_background` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `theme_id` int(11) DEFAULT NULL,
  `sr_no` int(2) DEFAULT NULL,
  `display_sr_no` int(2) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `BG_SR_NO_UNIQ_IND` (`sr_no`),
  KEY `BG_THEME_ID_IND` (`theme_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `mm_captcha`
--
CREATE TABLE `mm_captcha` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `api_key` varchar(64) DEFAULT NULL,
  `web_key` varchar(64) DEFAULT NULL,
  `_key` varchar(64) DEFAULT NULL,
  `sec_level` int(2) DEFAULT NULL,
  `sec_mode` int(2) DEFAULT NULL,
  `verify_times` int(2) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `CAPTCHA_KEY_UNIQUE` (`_key`),
  KEY `CAPTCHA_ACCOUNT_ID_IND` (`api_key`),
  KEY `CAPTCHA_API_KEY_IND` (`api_key`),
  KEY `CAPTCHA_WEB_KEY_IND` (`web_key`)
) ENGINE=InnoDB AUTO_INCREMENT=298 DEFAULT CHARSET=utf8;

/* 20191223 */
ALTER TABLE mm_website
ADD COLUMN icon_type INT(2) NULL AFTER sec_mode;

UPDATE mm_website SET icon_type = 0;

/* 20200310 */
INSERT INTO `mm_website` (`account_id`, `url`, `api_key`, `web_key`, `sec_level`, `sec_mode`, `icon_type`, `theme_num`, `scaling_ratio`, `state`, `created_at`, `updated_at`)
VALUES (1, 'www.cloudcrowd.com.cn', '029088ad4a7a4dfd9b7acc7c03722054', 'd67a608596d04eedba84c4b3c6564971', 3, 1, 0, 0, 1.0, 0, sysdate(), sysdate()),
	(1, '*.cloudcrowd.com.cn', '6b87fa888fe84114991bc6c44fb02aea', '28e7c0b54c934934b9519c651b699f9b', 2, 1, 0, 0, 1.0, 0, sysdate(), sysdate());
