CREATE TABLE IF NOT EXISTS `gf_store_collection` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `collectionId` VARCHAR(64) NOT NULL COMMENT 'Store collection business id',
    `collectionName` VARCHAR(255) NOT NULL COMMENT 'Store collection name',
    `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT NOT NULL DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gf_store_collection_collection_id` (`collectionId`),
    KEY `idx_gf_store_collection_name` (`collectionName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Store collection master';

CREATE TABLE IF NOT EXISTS `gf_store_collection_store` (
    `id` BIGINT NOT NULL COMMENT 'Primary key',
    `collectionDbId` BIGINT NOT NULL COMMENT 'Store collection primary key',
    `collectionId` VARCHAR(64) NOT NULL COMMENT 'Store collection business id',
    `storeId` VARCHAR(64) NOT NULL COMMENT 'Business store id',
    `storeName` VARCHAR(255) NOT NULL COMMENT 'Store name snapshot',
    `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Logical delete flag',
    `createdAt` BIGINT DEFAULT NULL COMMENT 'Created time',
    `updatedAt` BIGINT DEFAULT NULL COMMENT 'Updated time',
    `sortedNum` INT NOT NULL DEFAULT 1 COMMENT 'Sort number',
    PRIMARY KEY (`id`),
    KEY `idx_gf_store_collection_store_collection` (`collectionDbId`),
    KEY `idx_gf_store_collection_store_store` (`storeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Stores in a store collection';

INSERT IGNORE INTO `sys_resource` (`id`, `name`, `nameCh`, `icon`, `path`, `parentId`, `type`, `description`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (10924, 'StoreCollection', '门店集合', NULL, '/flow/store-collection', '10920', 'MENU', 'Store collection', b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 4);

INSERT IGNORE INTO `sys_role_resource` (`id`, `roleId`, `resourceId`, `deleted`, `createdAt`, `updatedAt`, `sortedNum`) VALUES
    (12324, 10001, 10924, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 4),
    (12424, 10002, 10924, b'0', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 4);
