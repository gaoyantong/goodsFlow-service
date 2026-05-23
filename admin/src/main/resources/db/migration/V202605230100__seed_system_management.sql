INSERT IGNORE INTO `sys_role` (`id`, `name`, `roleCode`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (10001, '超级管理员', 'SUPER_ADMIN', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (10002, '管理员', 'ADMIN', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (10003, '普通用户', 'USER', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3);

INSERT IGNORE INTO `sys_user` (`id`, `name`, `loginName`, `password`, `workNum`, `description`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (10101, '超级管理员', 'superadmin', 'sha256$8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SA001', 'Seeded super administrator', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (10102, '管理员', 'admin', 'sha256$8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'AD001', 'Seeded administrator', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (10103, '普通用户', 'user', 'sha256$8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'US001', 'Seeded normal user', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3);

INSERT IGNORE INTO `sys_user_role` (`id`, `roleId`, `userId`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (10201, 10001, 10101, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (10202, 10002, 10102, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (10203, 10003, 10103, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3);

INSERT IGNORE INTO `sys_resource` (`id`, `name`, `nameCh`, `icon`, `path`, `parentId`, `type`, `description`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (10900, 'Dashboard', '工作台', 'DashboardOutlined', '/dashboard', NULL, 'MENU', 'Dashboard', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (10910, 'BaseData', '基础数据', 'DatabaseOutlined', '/base', NULL, 'MENU', 'Base data', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (10911, 'Goods', '货品资料', NULL, '/base/goods', '10910', 'MENU', 'Goods master data', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (10912, 'Store', '门店资料', NULL, '/base/store', '10910', 'MENU', 'Store master data', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (11000, 'System', '系统管理', 'SettingOutlined', '/sys', NULL, 'MENU', 'System management', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (11001, 'Resource', '资源管理', NULL, '/sys/resource', '11000', 'MENU', 'Resource management', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (11002, 'Role', '角色管理', NULL, '/sys/role', '11000', 'MENU', 'Role management', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (11003, 'User', '管理员', NULL, '/sys/user', '11000', 'MENU', 'Administrator management', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (11004, 'Constant', '常量管理', NULL, '/sys/constant', '11000', 'MENU', 'Constant management', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 4),
    (11005, 'Dict', '字典管理', NULL, '/sys/dict', '11000', 'MENU', 'Dictionary management', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 5);

INSERT IGNORE INTO `sys_role_resource` (`id`, `roleId`, `resourceId`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (11900, 10001, 10900, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (11910, 10001, 10910, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (11911, 10001, 10911, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (11912, 10001, 10912, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12000, 10001, 11000, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12001, 10001, 11001, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12002, 10001, 11002, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12003, 10001, 11003, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12004, 10001, 11004, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 4),
    (12005, 10001, 11005, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 5),
    (12100, 10002, 10900, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12110, 10002, 10910, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12111, 10002, 10911, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12112, 10002, 10912, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12120, 10002, 11000, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12121, 10002, 11001, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12122, 10002, 11002, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12123, 10002, 11003, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12124, 10002, 11004, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 4),
    (12125, 10002, 11005, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 5),
    (12200, 10003, 10900, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12210, 10003, 10910, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12211, 10003, 10911, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12212, 10003, 10912, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2);
