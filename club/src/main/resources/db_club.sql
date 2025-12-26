CREATE TABLE `sys_user`
(
    `id`          bigint       NOT NULL COMMENT '主键ID',
    `username`    varchar(50)  NOT NULL COMMENT '学号',
    `password`    varchar(100) NOT NULL COMMENT '密码',
    `real_name`   varchar(50) DEFAULT NULL COMMENT '姓名',
    `college`     varchar(50) DEFAULT NULL COMMENT '所属学院',
    `system_role` int         DEFAULT 1 COMMENT '系统角色: 0-超级管理员, 1-普通学生',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP,
    `deleted`     tinyint     DEFAULT 0 COMMENT '逻辑删除: 0-未删, 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) COMMENT ='用户表';

CREATE TABLE `club`
(
    `id`          bigint       NOT NULL COMMENT '社团ID',
    `name`        varchar(100) NOT NULL COMMENT '社团名称',
    `category`    varchar(50)  NOT NULL COMMENT '分类(科技/艺术/体育)',
    `description` text COMMENT '社团介绍',
    `creator_id`  bigint       NOT NULL COMMENT '创建申请人ID',
    `status`      int      DEFAULT 0 COMMENT '状态: 0-审核中, 1-正常, 2-驳回, 3-已注销',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) COMMENT ='社团主表';

CREATE TABLE `club_member`
(
    `id`          bigint NOT NULL COMMENT '主键',
    `club_id`     bigint NOT NULL COMMENT '社团ID',
    `user_id`     bigint NOT NULL COMMENT '学生ID',
    `member_role` int      DEFAULT 1 COMMENT '职位: 1-普通成员,  2-社长',
    `status`      int      DEFAULT 0 COMMENT '状态: 0-申请中, 1-已入社, 2-已拒绝',
    `join_time`   datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_club_user` (`club_id`, `user_id`) COMMENT '防止重复加入同一社团'
) COMMENT ='社团成员关联表';

CREATE TABLE `activity`
(
    `id`         bigint       NOT NULL COMMENT '主键',
    `club_id`    bigint       NOT NULL COMMENT '举办社团ID',
    `title`      varchar(100) NOT NULL COMMENT '活动标题',
    `content`    text COMMENT '活动详情(富文本)',
    `start_time` datetime     NOT NULL COMMENT '开始时间',
    `end_time`   datetime     NOT NULL COMMENT '结束时间',
    `location`   varchar(100) DEFAULT NULL COMMENT '活动地点',
    `max_people` int          DEFAULT 0 COMMENT '最大人数限制(0不限)',
    `status`     int          DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已结束',
    PRIMARY KEY (`id`)
) COMMENT ='活动表';

CREATE TABLE `activity_member`
(
    `id`             bigint NOT NULL COMMENT '主键',
    `activity_id`    bigint NOT NULL COMMENT '活动ID',
    `user_id`        bigint NOT NULL COMMENT '用户ID',
    `signup_time`    datetime DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `checkin_status` int      DEFAULT 0 COMMENT '签到状态: 0-未签到, 1-已签到',
    `checkin_time`   datetime DEFAULT NULL COMMENT '签到时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`) COMMENT '防止重复报名'
) COMMENT ='活动报名记录表';