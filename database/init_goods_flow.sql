CREATE DATABASE IF NOT EXISTS `goods_flow`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE `goods_flow`;

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(255) DEFAULT NULL,
    `loginName` VARCHAR(255) DEFAULT NULL,
    `password` VARCHAR(255) DEFAULT NULL,
    `icon` VARCHAR(255) DEFAULT NULL,
    `language` VARCHAR(255) DEFAULT NULL,
    `description` VARCHAR(255) DEFAULT NULL,
    `sex` VARCHAR(191) DEFAULT NULL,
    `deleted` BIT(1) DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT DEFAULT 1 COMMENT 'Sort number',
    `fingerprint` VARCHAR(255) DEFAULT NULL,
    `workNum` VARCHAR(100) DEFAULT NULL COMMENT 'Work number',
    `email` VARCHAR(255) DEFAULT NULL COMMENT 'Email',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Admin users';

CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(255) DEFAULT NULL,
    `deleted` BIT(1) DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT DEFAULT 1 COMMENT 'Sort number',
    `roleCode` VARCHAR(20) DEFAULT NULL COMMENT 'Role code',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Roles';

CREATE TABLE IF NOT EXISTS `sys_resource` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(255) DEFAULT NULL,
    `icon` VARCHAR(255) DEFAULT NULL,
    `path` VARCHAR(255) DEFAULT NULL,
    `description` VARCHAR(255) DEFAULT NULL,
    `parentId` VARCHAR(36) DEFAULT NULL,
    `type` VARCHAR(100) DEFAULT NULL,
    `deleted` BIT(1) DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT DEFAULT 1 COMMENT 'Sort number',
    `nameCh` VARCHAR(255) DEFAULT NULL COMMENT 'Chinese name',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Resources';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL,
    `roleId` BIGINT DEFAULT NULL,
    `userId` BIGINT DEFAULT NULL,
    `deleted` BIT(1) DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    KEY `fk_sys_user_role_sys_role_1` (`roleId`),
    KEY `fk_sys_user_role_sys_user_1` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User role relation';

CREATE TABLE IF NOT EXISTS `sys_role_resource` (
    `id` BIGINT NOT NULL,
    `roleId` BIGINT DEFAULT NULL,
    `resourceId` BIGINT DEFAULT NULL,
    `deleted` BIT(1) DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    KEY `fk_sys_role_resource_sys_resource_2` (`resourceId`),
    KEY `fk_sys_role_resource_sys_role_1` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Role resource relation';

CREATE TABLE IF NOT EXISTS `sys_dict` (
    `id` BIGINT NOT NULL,
    `code` VARCHAR(191) DEFAULT NULL,
    `nameEnus` VARCHAR(191) DEFAULT NULL,
    `nameZhcn` VARCHAR(191) DEFAULT NULL,
    `nameZhtw` VARCHAR(191) DEFAULT NULL,
    `parent` VARCHAR(191) DEFAULT NULL,
    `vals` VARCHAR(191) DEFAULT NULL,
    `remarks` VARCHAR(191) DEFAULT NULL,
    `createdAt` BIGINT DEFAULT NULL,
    `updatedAt` BIGINT DEFAULT NULL,
    `deleted` BIT(1) DEFAULT b'0',
    `sortedNum` INT DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Dictionary';

CREATE TABLE IF NOT EXISTS `sys_constant` (
    `id` BIGINT NOT NULL,
    `code` VARCHAR(191) DEFAULT NULL,
    `name` VARCHAR(191) DEFAULT NULL,
    `parent` VARCHAR(191) DEFAULT NULL,
    `vals` LONGTEXT,
    `remarks` VARCHAR(191) DEFAULT NULL,
    `createdAt` BIGINT DEFAULT NULL,
    `updatedAt` BIGINT DEFAULT NULL,
    `deleted` BIT(1) DEFAULT b'0',
    `sortedNum` INT DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Constants';

CREATE TABLE IF NOT EXISTS `sys_snap` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(255) DEFAULT NULL,
    `description` TEXT COMMENT 'Description',
    `scripts` MEDIUMTEXT COMMENT 'Source data',
    `data` MEDIUMTEXT COMMENT 'Formatted frontend data',
    `deleted` BIT(1) DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Snapshots';

CREATE TABLE IF NOT EXISTS `gf_goods` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `goodsId` VARCHAR(64) NOT NULL COMMENT 'Business goods id',
    `genericName` VARCHAR(255) NOT NULL COMMENT 'Generic name',
    `manufacturer` VARCHAR(255) NOT NULL COMMENT 'Manufacturer',
    `specification` VARCHAR(255) NOT NULL COMMENT 'Specification',
    `unit` VARCHAR(64) NOT NULL COMMENT 'Goods unit',
    `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT NOT NULL DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gf_goods_goods_id` (`goodsId`),
    KEY `idx_gf_goods_generic_name` (`genericName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Goods master data';

CREATE TABLE IF NOT EXISTS `gf_store` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `storeId` VARCHAR(64) NOT NULL COMMENT 'Business store id',
    `storeName` VARCHAR(255) NOT NULL COMMENT 'Store name',
    `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT NOT NULL DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gf_store_store_id` (`storeId`),
    KEY `idx_gf_store_store_name` (`storeName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Store master data';

CREATE TABLE IF NOT EXISTS `gf_flow_task` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `taskNo` VARCHAR(64) NOT NULL COMMENT 'Flow generation task number',
    `goodsId` VARCHAR(64) NOT NULL COMMENT 'Business goods id',
    `pendingDeliveryQty` INT NOT NULL COMMENT 'Quantity waiting for delivery',
    `deliveryStartDate` DATE NOT NULL COMMENT 'Delivery start date',
    `deliveryEndDate` DATE NOT NULL COMMENT 'Delivery end date',
    `maxRetailQtyPerOrder` INT NOT NULL COMMENT 'Maximum retail quantity per order',
    `retailDays` INT NOT NULL COMMENT 'Retail generation days',
    `batchNo` VARCHAR(128) NOT NULL COMMENT 'Batch number',
    `expiryDate` DATE NOT NULL COMMENT 'Expiry date',
    `storeScopeType` VARCHAR(32) NOT NULL DEFAULT 'ALL' COMMENT 'Store scope: ALL or SELECTED',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT 'Task status',
    `generatedAt` BIGINT DEFAULT NULL COMMENT 'Generated time',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT 'Remark',
    `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT NOT NULL DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gf_flow_task_task_no` (`taskNo`),
    KEY `idx_gf_flow_task_goods_id` (`goodsId`),
    KEY `idx_gf_flow_task_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Goods flow generation task';

CREATE TABLE IF NOT EXISTS `gf_flow_task_store` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `taskId` BIGINT NOT NULL COMMENT 'Flow generation task id',
    `storeId` VARCHAR(64) NOT NULL COMMENT 'Business store id',
    `storeName` VARCHAR(255) NOT NULL COMMENT 'Store name snapshot',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gf_flow_task_store_task_store` (`taskId`, `storeId`),
    KEY `idx_gf_flow_task_store_store_id` (`storeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Stores selected for a flow task';

CREATE TABLE IF NOT EXISTS `gf_delivery_inbound` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `taskId` BIGINT NOT NULL COMMENT 'Flow generation task id',
    `businessDate` DATE NOT NULL COMMENT 'Inbound business date',
    `storeId` VARCHAR(64) NOT NULL COMMENT 'Business store id',
    `storeName` VARCHAR(255) NOT NULL COMMENT 'Store name snapshot',
    `goodsId` VARCHAR(64) NOT NULL COMMENT 'Business goods id',
    `genericName` VARCHAR(255) NOT NULL COMMENT 'Generic name snapshot',
    `specification` VARCHAR(255) NOT NULL COMMENT 'Specification snapshot',
    `manufacturer` VARCHAR(255) NOT NULL COMMENT 'Manufacturer snapshot',
    `unit` VARCHAR(64) NOT NULL COMMENT 'Goods unit snapshot',
    `batchNo` VARCHAR(128) NOT NULL COMMENT 'Batch number',
    `expiryDate` DATE NOT NULL COMMENT 'Expiry date',
    `inboundQty` INT NOT NULL COMMENT 'Inbound quantity',
    `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT NOT NULL DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    KEY `idx_gf_delivery_inbound_task_id` (`taskId`),
    KEY `idx_gf_delivery_inbound_business_date` (`businessDate`),
    KEY `idx_gf_delivery_inbound_store_goods` (`storeId`, `goodsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Store delivery inbound flow';

CREATE TABLE IF NOT EXISTS `gf_retail_outbound` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `taskId` BIGINT NOT NULL COMMENT 'Flow generation task id',
    `inboundId` BIGINT NOT NULL COMMENT 'Source delivery inbound id',
    `businessDate` DATE NOT NULL COMMENT 'Retail business date',
    `storeId` VARCHAR(64) NOT NULL COMMENT 'Business store id',
    `storeName` VARCHAR(255) NOT NULL COMMENT 'Store name snapshot',
    `goodsId` VARCHAR(64) NOT NULL COMMENT 'Business goods id',
    `genericName` VARCHAR(255) NOT NULL COMMENT 'Generic name snapshot',
    `specification` VARCHAR(255) NOT NULL COMMENT 'Specification snapshot',
    `manufacturer` VARCHAR(255) NOT NULL COMMENT 'Manufacturer snapshot',
    `unit` VARCHAR(64) NOT NULL COMMENT 'Goods unit snapshot',
    `batchNo` VARCHAR(128) NOT NULL COMMENT 'Batch number',
    `outboundQty` INT NOT NULL COMMENT 'Retail outbound quantity',
    `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT NOT NULL DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    KEY `idx_gf_retail_outbound_task_id` (`taskId`),
    KEY `idx_gf_retail_outbound_inbound_id` (`inboundId`),
    KEY `idx_gf_retail_outbound_business_date` (`businessDate`),
    KEY `idx_gf_retail_outbound_store_goods` (`storeId`, `goodsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Store retail outbound flow';

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
    (10920, 'Flow', '数据处理', 'PartitionOutlined', '/flow', NULL, 'MENU', 'Flow data processing', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (10921, 'FlowTask', '数据录入', NULL, '/flow/task', '10920', 'MENU', 'Flow task input', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (10922, 'DeliveryInbound', '配送入库数据', NULL, '/flow/inbound', '10920', 'MENU', 'Delivery inbound data', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (10923, 'RetailOutbound', '零售数据', NULL, '/flow/retail', '10920', 'MENU', 'Retail outbound data', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
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
    (12320, 10001, 10920, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12321, 10001, 10921, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12322, 10001, 10922, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12323, 10001, 10923, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
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
    (12420, 10002, 10920, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12421, 10002, 10921, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12422, 10002, 10922, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12423, 10002, 10923, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
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
