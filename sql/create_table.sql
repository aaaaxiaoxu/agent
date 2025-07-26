-- Create database
create database if not exists advx;
use advx;

-- Drop tables
DROP TABLE IF EXISTS user, ai_api_log;


-- User table
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment 'Account',
    userPassword varchar(512)                           not null comment 'Password',
    email        varchar(256)                           null comment 'Email',
    userName     varchar(256)                           null comment 'Username',
    userAvatar   varchar(1024)                          null comment 'Avatar',
    userProfile  varchar(512)                           null comment 'Profile',
    userRole     varchar(256) default 'user'            not null comment 'Role: user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment 'Edit time',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'Create time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update time',
    isDelete     tinyint      default 0                 not null comment 'Is deleted',
    UNIQUE KEY uk_userAccount (userAccount),
    UNIQUE KEY uk_email (email),
    INDEX idx_userName (userName)
) comment 'User' collate = utf8mb4_unicode_ci;
