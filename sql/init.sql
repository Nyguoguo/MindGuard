-- MindGuard 数据库初始化

CREATE DATABASE IF NOT EXISTS mindguard_auth
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS mindguard_chat
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS mindguard_notification
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- ============================================
-- mindguard_auth
-- ============================================
USE mindguard_auth;

CREATE TABLE IF NOT EXISTS user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(50)  DEFAULT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT 'USER or ADMIN',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1=active, 0=disabled',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================
-- mindguard_chat
-- ============================================
USE mindguard_chat;

CREATE TABLE IF NOT EXISTS conversation (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id  VARCHAR(64)  NOT NULL,
    user_id     BIGINT       NOT NULL,
    role        VARCHAR(16)  NOT NULL COMMENT 'user or assistant',
    content     TEXT         NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session (session_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS knowledge_doc (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    content      LONGTEXT     NOT NULL,
    chunk_count  INT          DEFAULT 0,
    vector_ids   JSON         DEFAULT NULL,
    uploaded_by  BIGINT       NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS psych_report (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    session_id    VARCHAR(64)  NOT NULL,
    risk_level    TINYINT      NOT NULL DEFAULT 0 COMMENT '0=normal, 1=mild, 2=moderate, 3=high, 4=critical',
    analysis_text TEXT         DEFAULT NULL,
    keywords      JSON         DEFAULT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_session (session_id)
) ENGINE=InnoDB;

-- ============================================
-- mindguard_notification
-- ============================================
USE mindguard_notification;

CREATE TABLE IF NOT EXISTS alert_rule (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name              VARCHAR(100) NOT NULL,
    risk_level_threshold   TINYINT      NOT NULL DEFAULT 3,
    consecutive_count      INT          NOT NULL DEFAULT 3,
    enabled                TINYINT      NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS alert_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    rule_id       BIGINT       NOT NULL,
    alert_content TEXT         DEFAULT NULL,
    sent_to       VARCHAR(255) DEFAULT NULL,
    sent_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status        VARCHAR(20)  NOT NULL DEFAULT 'sent' COMMENT 'sent, failed'
) ENGINE=InnoDB;
