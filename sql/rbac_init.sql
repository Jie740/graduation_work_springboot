-- ============================================================
-- RBAC 权限体系初始化脚本
-- 项目:graduation_work_springBoot(现代农业管理系统)
-- 说明:
--   1. 业务用户主体为 user 表(user.role 字段直接存角色编码),
--      登录鉴权读取 user.role,构造权限 ROLE_<role_code>
--   2. sys_role / sys_permission / sys_user_role / sys_role_permission
--      为标准 RBAC 模型表,此处初始化角色/权限字典数据
--   3. 执行方式:在目标数据库(与 smart_agriculture.sql 同库)执行本脚本
-- ============================================================

-- ----------------------------
-- 1. 角色字典初始化
-- ----------------------------
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`) VALUES
('承包人',     'USER',             '承包人(个人用户)：个人中心、承包土地查看、种植记录查看、订单查看', 1),
('企业管理员', 'ENTERPRISE_ADMIN', '企业管理员：企业管理、土地管理、农作物管理、设备管理、员工管理',       1),
('系统管理员', 'SYSTEM_ADMIN',     '系统管理员：拥有全部权限',                                        1);

-- ----------------------------
-- 2. 权限字典初始化(type: 1菜单 2按钮 3接口)
-- ----------------------------
INSERT INTO `sys_permission` (`parent_id`, `permission_name`, `permission_code`, `type`, `sort`, `status`) VALUES
(0, '企业管理',   'enterprise:manage', 1, 1, 1),
(0, '土地管理',   'land:manage',       1, 2, 1),
(0, '农作物管理', 'crop:manage',       1, 3, 1),
(0, '设备管理',   'equipment:manage',  1, 4, 1),
(0, '员工管理',   'user:manage',       1, 5, 1),
(0, '个人中心',   'user:profile',      1, 6, 1),
(0, '承包土地查看', 'land:view',       2, 7, 1),
(0, '种植记录查看', 'record:view',     2, 8, 1);

-- ----------------------------
-- 3. 角色-权限关联（供前端按角色渲染菜单）
-- ----------------------------
-- system_admin 拥有全部权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.`id`, p.`id` FROM `sys_role` r, `sys_permission` p WHERE r.`role_code` = 'SYSTEM_ADMIN';

-- enterprise_admin 拥有企业管理/土地/农作物/设备/员工管理
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.`id`, p.`id` FROM `sys_role` r, `sys_permission` p
WHERE r.`role_code` = 'ENTERPRISE_ADMIN' AND p.`permission_code` IN
('enterprise:manage','land:manage','crop:manage','equipment:manage','user:manage');

-- user 拥有个人中心/承包土地查看/种植记录查看
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.`id`, p.`id` FROM `sys_role` r, `sys_permission` p
WHERE r.`role_code` = 'USER' AND p.`permission_code` IN
('user:profile','land:view','record:view');
