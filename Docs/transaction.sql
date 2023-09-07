-- test.tab_user definition
CREATE TABLE `tab_user`
(
    `uid`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `name` varchar(100) COLLATE utf8mb4_unicode_ci                      NOT NULL,
    `age`  varchar(100) COLLATE utf8mb4_unicode_ci                      NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- test.tab_occupation definition
CREATE TABLE `tab_occupation`
(
    `oid`        bigint                                                       NOT NULL AUTO_INCREMENT,
    `uName`      varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `occupation` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;