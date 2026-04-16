/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.127.128
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 192.168.127.128:3306
 Source Schema         : smart_agriculture

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 16/04/2026 18:42:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for contractor_material_stock
-- ----------------------------
DROP TABLE IF EXISTS `contractor_material_stock`;
CREATE TABLE `contractor_material_stock`  (
  `contractor_material_id` bigint NOT NULL AUTO_INCREMENT COMMENT '承包人农资ID',
  `material_id` bigint NULL DEFAULT NULL COMMENT '农资ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `stock` int NULL DEFAULT NULL COMMENT '库存',
  `warning_stock` int NULL DEFAULT 0 COMMENT '预警库存',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`contractor_material_id`) USING BTREE,
  INDEX `material_id`(`material_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '承包人农资库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contractor_material_stock
-- ----------------------------
INSERT INTO `contractor_material_stock` VALUES (13, 5, 2214, 10, 6, '2026-04-11 02:47:53');
INSERT INTO `contractor_material_stock` VALUES (14, 6, 2217, 10, 0, '2026-04-11 14:23:25');
INSERT INTO `contractor_material_stock` VALUES (15, 7, 2214, 9, 1, '2026-04-11 18:54:23');

-- ----------------------------
-- Table structure for crop
-- ----------------------------
DROP TABLE IF EXISTS `crop`;
CREATE TABLE `crop`  (
  `crop_id` bigint NOT NULL AUTO_INCREMENT COMMENT '农作物ID',
  `crop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '农作物名',
  `crop_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '农作物类型',
  `growth_cycle` int NULL DEFAULT NULL COMMENT '生长周期（天）',
  `suitable_temperature` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适应温度',
  `suitable_humidity` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适应湿度',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`crop_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '农作物信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of crop
-- ----------------------------
INSERT INTO `crop` VALUES (5, '小麦', '柔麦', 100, '30', '10', '小麦农作物', '2026-03-17 03:01:47');
INSERT INTO `crop` VALUES (6, '水稻', '籼稻', 100, '30', '10', '水稻农作物', '2026-03-17 15:42:13');

-- ----------------------------
-- Table structure for crop_growth_record
-- ----------------------------
DROP TABLE IF EXISTS `crop_growth_record`;
CREATE TABLE `crop_growth_record`  (
  `growth_id` bigint NOT NULL AUTO_INCREMENT COMMENT '农作物生长记录ID',
  `record_id` bigint NULL DEFAULT NULL COMMENT '种植记录ID',
  `collect_time` datetime NULL DEFAULT NULL COMMENT '采集时间',
  `temperature` decimal(10, 2) NULL DEFAULT NULL COMMENT '温度',
  `humidity` decimal(10, 2) NULL DEFAULT NULL COMMENT '湿度',
  `light` decimal(10, 2) NULL DEFAULT NULL COMMENT '光照',
  `ph` decimal(10, 2) NULL DEFAULT NULL COMMENT 'ph值',
  PRIMARY KEY (`growth_id`) USING BTREE,
  INDEX `record_id`(`record_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '农作物生长记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of crop_growth_record
-- ----------------------------

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `equipment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备名',
  `equipment_type_id` bigint NULL DEFAULT NULL COMMENT '设备类型ID',
  `status` smallint NULL DEFAULT 0 COMMENT '状态 0正常闲置 1已被借用 2正在维修 3已损坏',
  PRIMARY KEY (`equipment_id`) USING BTREE,
  INDEX `equipment_type_id`(`equipment_type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '设备1', 5, 2);
INSERT INTO `equipment` VALUES (11, '设备2', 3, 1);
INSERT INTO `equipment` VALUES (12, '设备3', 6, 1);
INSERT INTO `equipment` VALUES (13, '设备4', 5, 0);

-- ----------------------------
-- Table structure for equipment_apply
-- ----------------------------
DROP TABLE IF EXISTS `equipment_apply`;
CREATE TABLE `equipment_apply`  (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '设备ID',
  `applicant_id` bigint NULL DEFAULT NULL COMMENT '申请人ID',
  `apply_time` datetime NULL DEFAULT 'curdate()' COMMENT '申请时间',
  `status` smallint NULL DEFAULT 0 COMMENT '状态 0未审核 1已通过 2未通过',
  PRIMARY KEY (`apply_id`) USING BTREE,
  INDEX `equipment_id`(`equipment_id` ASC) USING BTREE,
  INDEX `applicant_id`(`applicant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment_apply
-- ----------------------------
INSERT INTO `equipment_apply` VALUES (6, 1, 2214, '2026-03-17 16:00:00', 1);
INSERT INTO `equipment_apply` VALUES (7, 11, 2214, '2026-03-02 16:00:00', 1);
INSERT INTO `equipment_apply` VALUES (8, 1, 2214, '2026-03-17 16:00:00', 1);
INSERT INTO `equipment_apply` VALUES (9, 11, 2217, '2026-03-16 16:00:00', 1);
INSERT INTO `equipment_apply` VALUES (10, 12, 2214, '2026-04-14 16:00:00', 1);
INSERT INTO `equipment_apply` VALUES (11, 1, 2214, '2026-04-05 16:00:00', 2);
INSERT INTO `equipment_apply` VALUES (12, 1, 2214, '2026-04-07 16:00:00', 1);
INSERT INTO `equipment_apply` VALUES (14, 12, 2214, '2026-04-17 16:00:00', 0);

-- ----------------------------
-- Table structure for equipment_record
-- ----------------------------
DROP TABLE IF EXISTS `equipment_record`;
CREATE TABLE `equipment_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备记录ID',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '设备ID',
  `owner_id` bigint NULL DEFAULT NULL COMMENT '隶属人ID',
  `status` smallint NOT NULL DEFAULT 0 COMMENT '状态 0正在使用 1报修中',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `equipment_id`(`equipment_id` ASC) USING BTREE,
  INDEX `owner_id`(`owner_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment_record
-- ----------------------------
INSERT INTO `equipment_record` VALUES (7, 11, 2217, 0, NULL);
INSERT INTO `equipment_record` VALUES (8, 12, 2214, 0, NULL);
INSERT INTO `equipment_record` VALUES (9, 1, 2214, 1, NULL);

-- ----------------------------
-- Table structure for equipment_repair_apply
-- ----------------------------
DROP TABLE IF EXISTS `equipment_repair_apply`;
CREATE TABLE `equipment_repair_apply`  (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `applicant_id` bigint NULL DEFAULT NULL COMMENT '申请人ID',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '设备ID',
  `fault_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '故障描述',
  `apply_time` datetime NULL DEFAULT 'curtime()' COMMENT '申请时间',
  PRIMARY KEY (`apply_id`) USING BTREE,
  INDEX `applicant_id`(`applicant_id` ASC) USING BTREE,
  INDEX `equipment_id`(`equipment_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备报修申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment_repair_apply
-- ----------------------------
INSERT INTO `equipment_repair_apply` VALUES (1, 2214, 1, '设备进水', '2026-03-22 14:02:01');
INSERT INTO `equipment_repair_apply` VALUES (2, 2217, 1, '124', '2026-03-22 15:00:21');
INSERT INTO `equipment_repair_apply` VALUES (3, 2214, 1, '123', '2026-03-22 15:17:51');
INSERT INTO `equipment_repair_apply` VALUES (4, 2214, 1, '1321', '2026-03-22 15:18:55');
INSERT INTO `equipment_repair_apply` VALUES (5, 2217, 11, '142', '2026-03-22 15:19:04');
INSERT INTO `equipment_repair_apply` VALUES (6, 2214, 1, '31', '2026-04-11 16:47:06');
INSERT INTO `equipment_repair_apply` VALUES (7, 2214, 1, '11', '2026-04-11 17:51:53');
INSERT INTO `equipment_repair_apply` VALUES (8, 2214, 1, '1', '2026-04-11 17:52:19');
INSERT INTO `equipment_repair_apply` VALUES (9, 2214, 1, '11', '2026-04-11 19:38:48');
INSERT INTO `equipment_repair_apply` VALUES (10, 2214, 1, '故障', '2026-04-11 20:41:10');

-- ----------------------------
-- Table structure for equipment_type
-- ----------------------------
DROP TABLE IF EXISTS `equipment_type`;
CREATE TABLE `equipment_type`  (
  `equipment_type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '设备类型ID',
  `equipment_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型名',
  PRIMARY KEY (`equipment_type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of equipment_type
-- ----------------------------
INSERT INTO `equipment_type` VALUES (3, '类型3');
INSERT INTO `equipment_type` VALUES (5, '类型5');
INSERT INTO `equipment_type` VALUES (6, '类型6');
INSERT INTO `equipment_type` VALUES (7, '类型77');

-- ----------------------------
-- Table structure for farm_operation_record
-- ----------------------------
DROP TABLE IF EXISTS `farm_operation_record`;
CREATE TABLE `farm_operation_record`  (
  `operation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '农事活动记录ID',
  `record_id` bigint NULL DEFAULT NULL COMMENT '种植记录ID',
  `operation_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '农事活动类型（灌溉、施肥、除虫等）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作描述',
  `material_id` bigint NULL DEFAULT NULL COMMENT '承包人农资ID',
  `quantity` int NULL DEFAULT NULL COMMENT '用量',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作责任人ID',
  `operation_time` datetime NULL DEFAULT 'curdate()' COMMENT '操作时间',
  PRIMARY KEY (`operation_id`) USING BTREE,
  INDEX `record_id`(`record_id` ASC) USING BTREE,
  INDEX `operator_id`(`user_id` ASC) USING BTREE,
  INDEX `material_id`(`material_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '农事活动记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of farm_operation_record
-- ----------------------------
INSERT INTO `farm_operation_record` VALUES (41, 24, '播种', '1', 5, 1, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (42, 24, '浇水', '1', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (43, 24, '除草', '1', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (44, 24, '除虫', '', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (45, 24, '收割', '1', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (46, 31, '播种', '1', 5, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (47, 31, '施肥', '1', 7, 1, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (48, 31, '浇水', '1', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (49, 31, '除草', '1', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (50, 31, '除虫', '1', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (51, 31, '除虫', '1', NULL, 0, 2214, '2026-04-11 00:00:00');
INSERT INTO `farm_operation_record` VALUES (52, 31, '收割', '1', NULL, 0, 2214, '2026-04-11 00:00:00');

-- ----------------------------
-- Table structure for land
-- ----------------------------
DROP TABLE IF EXISTS `land`;
CREATE TABLE `land`  (
  `land_id` bigint NOT NULL AUTO_INCREMENT COMMENT '地块ID',
  `land_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地块名',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '位置',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '面积（亩）',
  `soil_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '土壤类型',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`land_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地块信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of land
-- ----------------------------
INSERT INTO `land` VALUES (2, '地块1', '位置1', 10.00, '黑土', '2026-03-10 17:56:35');
INSERT INTO `land` VALUES (5, '地块2', '位置2', 21.00, '红土', '2026-03-10 18:05:12');
INSERT INTO `land` VALUES (13, '地块3', '位置3', 20.00, '黑土', '2026-03-21 14:55:26');
INSERT INTO `land` VALUES (14, '地块4', '位置4', 10.00, '红土', '2026-04-11 01:41:54');

-- ----------------------------
-- Table structure for land_allocation
-- ----------------------------
DROP TABLE IF EXISTS `land_allocation`;
CREATE TABLE `land_allocation`  (
  `allocation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '地块分配ID',
  `land_id` bigint NULL DEFAULT NULL COMMENT '地块ID',
  `contractor_id` bigint NULL DEFAULT NULL COMMENT '承包人ID',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`allocation_id`) USING BTREE,
  INDEX `land_id`(`land_id` ASC) USING BTREE,
  INDEX `contractor_id`(`contractor_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地块分配表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of land_allocation
-- ----------------------------
INSERT INTO `land_allocation` VALUES (36, 2, 2214, '2026-04-07', '2027-04-14', '2026-04-11 18:49:37');
INSERT INTO `land_allocation` VALUES (37, 5, 2214, '2026-04-09', '2027-04-07', '2026-04-11 18:49:51');
INSERT INTO `land_allocation` VALUES (38, 13, 2217, '2026-04-21', '2027-04-24', '2026-04-11 18:50:07');
INSERT INTO `land_allocation` VALUES (39, 14, 2217, '2026-04-07', '2027-04-28', '2026-04-11 18:50:17');

-- ----------------------------
-- Table structure for material
-- ----------------------------
DROP TABLE IF EXISTS `material`;
CREATE TABLE `material`  (
  `material_id` bigint NOT NULL AUTO_INCREMENT COMMENT '农资ID',
  `material_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '农资名',
  `type_id` bigint NULL DEFAULT NULL COMMENT '农资类型ID',
  `stock` int NULL DEFAULT NULL COMMENT '库存',
  `warning_stock` int NULL DEFAULT 0 COMMENT '预警库存',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`material_id`) USING BTREE,
  INDEX `material_type_id`(`type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '农资表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material
-- ----------------------------
INSERT INTO `material` VALUES (5, '小麦种子', 5, 49, 10, '2026-03-16 15:14:42');
INSERT INTO `material` VALUES (6, '水稻种子', 5, 40, 0, '2026-03-16 15:18:45');
INSERT INTO `material` VALUES (7, '无机肥', 7, 30, 25, '2026-03-18 14:33:06');

-- ----------------------------
-- Table structure for material_apply
-- ----------------------------
DROP TABLE IF EXISTS `material_apply`;
CREATE TABLE `material_apply`  (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `applicant_id` bigint NULL DEFAULT NULL COMMENT '申请人ID',
  `material_id` bigint NULL DEFAULT NULL COMMENT '农资ID',
  `quantity` int NULL DEFAULT NULL COMMENT '数量',
  `apply_time` datetime NULL DEFAULT NULL COMMENT '申请时间',
  `status` smallint NULL DEFAULT 0 COMMENT '状态 0未审核 1通过 2未通过',
  PRIMARY KEY (`apply_id`) USING BTREE,
  INDEX `applicant_id`(`applicant_id` ASC) USING BTREE,
  INDEX `material_id`(`material_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '农资申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_apply
-- ----------------------------
INSERT INTO `material_apply` VALUES (11, 2214, 7, 50, '2026-03-18 06:33:28', 1);
INSERT INTO `material_apply` VALUES (12, 2214, 5, 50, '2026-03-18 06:43:04', 1);
INSERT INTO `material_apply` VALUES (14, 2214, 6, 50, '2026-03-18 07:02:50', 1);
INSERT INTO `material_apply` VALUES (15, 2217, 5, 10, '2026-03-21 07:07:02', 1);
INSERT INTO `material_apply` VALUES (16, 2217, 7, 1, '2026-03-21 08:38:09', 1);
INSERT INTO `material_apply` VALUES (17, 2217, 7, 10, '2026-03-22 07:26:05', 1);
INSERT INTO `material_apply` VALUES (18, 2214, 5, 1, '2026-04-10 18:47:51', 1);
INSERT INTO `material_apply` VALUES (19, 2217, 6, 10, '2026-04-11 06:23:20', 1);
INSERT INTO `material_apply` VALUES (20, 2214, 7, 10, '2026-04-11 10:54:20', 1);
INSERT INTO `material_apply` VALUES (22, 2214, 5, 1, '2026-04-11 12:37:00', 0);

-- ----------------------------
-- Table structure for material_stock_record
-- ----------------------------
DROP TABLE IF EXISTS `material_stock_record`;
CREATE TABLE `material_stock_record`  (
  `stock_record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '农资出入库ID',
  `material_id` bigint NULL DEFAULT NULL COMMENT '农资ID',
  `type` smallint NULL DEFAULT NULL COMMENT '类型（0入库、1出库）',
  `quantity` int NULL DEFAULT NULL COMMENT '数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`stock_record_id`) USING BTREE,
  INDEX `material_id`(`material_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '农资出入库记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_stock_record
-- ----------------------------
INSERT INTO `material_stock_record` VALUES (16, 5, 1, 1, '2026-04-11 02:47:53');
INSERT INTO `material_stock_record` VALUES (17, 6, 1, 10, '2026-04-11 14:23:25');
INSERT INTO `material_stock_record` VALUES (18, 7, 1, 10, '2026-04-11 18:54:23');

-- ----------------------------
-- Table structure for material_type
-- ----------------------------
DROP TABLE IF EXISTS `material_type`;
CREATE TABLE `material_type`  (
  `type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型名（化肥、农药、种子、农膜）',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '农资类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of material_type
-- ----------------------------
INSERT INTO `material_type` VALUES (5, '种子');
INSERT INTO `material_type` VALUES (7, '化肥');

-- ----------------------------
-- Table structure for mature_crop
-- ----------------------------
DROP TABLE IF EXISTS `mature_crop`;
CREATE TABLE `mature_crop`  (
  `mature_crop_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成熟作物ID',
  `record_id` bigint NULL DEFAULT NULL COMMENT '种植记录ID',
  `output_quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '产出数量',
  `harvest_time` date NULL DEFAULT NULL COMMENT '收割时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`mature_crop_id`) USING BTREE,
  INDEX `record_id`(`record_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '成熟作物表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mature_crop
-- ----------------------------
INSERT INTO `mature_crop` VALUES (36, 31, 10.00, '2026-04-21', '2026-04-11 18:56:12');

-- ----------------------------
-- Table structure for planting_plan
-- ----------------------------
DROP TABLE IF EXISTS `planting_plan`;
CREATE TABLE `planting_plan`  (
  `plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '种植计划ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '计划名',
  `land_id` bigint NULL DEFAULT NULL COMMENT '地块ID',
  `crop_id` bigint NOT NULL,
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `expected_output` decimal(10, 2) NULL DEFAULT NULL COMMENT '期望产出',
  `start_time` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` date NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` smallint NULL DEFAULT 0 COMMENT '状态（未发布、执行中、调整中、已完成、已终止）',
  PRIMARY KEY (`plan_id`) USING BTREE,
  INDEX `land_id`(`land_id` ASC) USING BTREE,
  INDEX `creator_id`(`creator_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '种植计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of planting_plan
-- ----------------------------
INSERT INTO `planting_plan` VALUES (29, '计划1', 2, 5, 2216, 100.00, '2026-04-07', '2026-04-16', '2026-04-11 18:50:39', 3);
INSERT INTO `planting_plan` VALUES (30, '计划2', 5, 6, 2216, 100.00, '2026-04-02', '2026-05-08', '2026-04-11 18:50:55', 1);
INSERT INTO `planting_plan` VALUES (31, '计划3', 13, 5, 2216, 100.00, '2026-04-13', '2027-04-07', '2026-04-11 18:51:10', 2);
INSERT INTO `planting_plan` VALUES (32, '计划4', 14, 6, 2216, 100.00, '2026-04-13', '2027-04-13', '2026-04-11 18:51:23', 0);

-- ----------------------------
-- Table structure for planting_plan_adjust
-- ----------------------------
DROP TABLE IF EXISTS `planting_plan_adjust`;
CREATE TABLE `planting_plan_adjust`  (
  `adjust_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请表ID',
  `applicant_id` bigint NULL DEFAULT NULL COMMENT '申请人ID',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '种植计划ID',
  `land_id` bigint NOT NULL,
  `crop_id` bigint NOT NULL,
  `expected_output` decimal(10, 2) NULL DEFAULT NULL,
  `start_time` date NULL DEFAULT NULL,
  `end_time` date NULL DEFAULT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调整原因描述',
  `apply_time` datetime NULL DEFAULT NULL COMMENT '申请时间',
  `status` smallint NULL DEFAULT 0 COMMENT '状态（0未审批 1 通过 2未通过）',
  PRIMARY KEY (`adjust_id`) USING BTREE,
  INDEX `applicant_id`(`applicant_id` ASC) USING BTREE,
  INDEX `plan_id`(`plan_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '种植计划调整申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of planting_plan_adjust
-- ----------------------------
INSERT INTO `planting_plan_adjust` VALUES (24, 2214, 30, 5, 6, 100.00, '2026-04-04', '2026-05-10', '1', '2026-04-11 11:01:55', 1);
INSERT INTO `planting_plan_adjust` VALUES (40, 2217, 31, 13, 5, 100.00, '2026-04-12', '2027-04-06', '4', '2026-04-11 12:03:12', 0);

-- ----------------------------
-- Table structure for planting_record
-- ----------------------------
DROP TABLE IF EXISTS `planting_record`;
CREATE TABLE `planting_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '种植记录ID',
  `land_id` bigint NULL DEFAULT NULL COMMENT '地块ID',
  `crop_id` bigint NULL DEFAULT NULL COMMENT '农作物ID',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '种植计划ID',
  `planting_date` date NULL DEFAULT NULL COMMENT '种植日期',
  `expected_harvest_date` date NULL DEFAULT NULL COMMENT '期望收割日期',
  `actual_harvest_date` date NULL DEFAULT NULL COMMENT '实际收割日期',
  `status` smallint NULL DEFAULT 0 COMMENT '状态 0生长中 1已成熟',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `land_id`(`land_id` ASC) USING BTREE,
  INDEX `crop_id`(`crop_id` ASC) USING BTREE,
  INDEX `plan_id`(`plan_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地块种植记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of planting_record
-- ----------------------------
INSERT INTO `planting_record` VALUES (31, 2, 5, 29, '2026-04-13', '2026-04-27', '2026-04-21', 1, '2026-04-11 18:53:14');

-- ----------------------------
-- Table structure for processing_record
-- ----------------------------
DROP TABLE IF EXISTS `processing_record`;
CREATE TABLE `processing_record`  (
  `processing_id` bigint NOT NULL AUTO_INCREMENT COMMENT '加工记录ID',
  `mature_crop_id` bigint NULL DEFAULT NULL COMMENT '成熟作物ID',
  `quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '数量',
  `processing_method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加工方法',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `start_date` date NULL DEFAULT NULL COMMENT '加工开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '加工结束日期',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`processing_id`) USING BTREE,
  INDEX `mature_crop_id`(`mature_crop_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '加工记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of processing_record
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品名',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '数量',
  `processing_id` bigint NULL DEFAULT NULL COMMENT '加工记录ID',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态（库存中、已售出）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `processing_id`(`processing_id` ASC) USING BTREE,
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`processing_id`) REFERENCES `processing_record` (`processing_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（加密存储）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色（system_admin、enterprise_admin、user）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` smallint NOT NULL DEFAULT 1 COMMENT '冻结（0）、正常（1）',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2219 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2214, '1', '1', '1', 'user', '1', '1', 1);
INSERT INTO `user` VALUES (2215, '2', '2', '2', 'enterprise_admin', '2', '2', 1);
INSERT INTO `user` VALUES (2216, '3', '3', '3', 'system_admin', '3', '3', 1);
INSERT INTO `user` VALUES (2217, '4', '4', '4', 'user', '4', '4', 1);
INSERT INTO `user` VALUES (2218, '5', '5', '5', 'enterprise_admin', '5', '5', 1);
INSERT INTO `user` VALUES (2219, '6', '6', '6', 'system_admin', '6', '6', 1);

SET FOREIGN_KEY_CHECKS = 1;
