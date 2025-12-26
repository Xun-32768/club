package com.example.club;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.sql.Types;
import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        // 1. 数据库配置
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/db_club", "root", "060216")

                // 2. 全局配置
                .globalConfig(builder -> {
                    builder.author("Xun") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 输出目录
                            .disableOpenDir(); // 禁止打开输出目录
                })

                // 3. 包配置
                .packageConfig(builder -> {
                    builder.parent("com.example.club") // 父包名
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .mapper("mapper")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper"));
                })

                // 4. 策略配置 (核心)
                .strategyConfig(builder -> {
                    builder.addInclude("sys_user", "club", "club_member", "activity", "activity_member") // 需要生成的表名
                            .addTablePrefix("tb_", "sys_") // 自动去除表前缀 (生成的类名将是 User, Club...)

                            // Entity 策略
                            .entityBuilder()
                            .enableLombok() // 开启 Lombok
                            .enableTableFieldAnnotation() // 开启字段注解

                            // Controller 策略
                            .controllerBuilder()
                            .enableRestStyle(); // 开启 @RestController
                })
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}