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

INSERT IGNORE INTO `sys_resource` (`id`, `name`, `nameCh`, `icon`, `path`, `parentId`, `type`, `description`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (10920, 'Flow', '数据处理', 'PartitionOutlined', '/flow', NULL, 'MENU', 'Flow data processing', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
(10921, 'FlowTask', '药品录入', NULL, '/flow/task', '10920', 'MENU', 'Medicine input', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
(10922, 'DeliveryInbound', '入库数据', NULL, '/flow/inbound', '10920', 'MENU', 'Inbound data', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (10923, 'RetailOutbound', '零售数据', NULL, '/flow/retail', '10920', 'MENU', 'Retail outbound data', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3);

INSERT IGNORE INTO `sys_role_resource` (`id`, `roleId`, `resourceId`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (12320, 10001, 10920, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12321, 10001, 10921, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12322, 10001, 10922, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12323, 10001, 10923, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12420, 10002, 10920, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3),
    (12421, 10002, 10921, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 1),
    (12422, 10002, 10922, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 2),
    (12423, 10002, 10923, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 3);
