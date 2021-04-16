package com.pitaya;

import com.pitaya.service.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @Author: qyl
 * @Date: 2021/4/15 21:21
 */
@SpringBootApplication
@MapperScan("com.pitaya.mapper")
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker(2, 3);
    }
}
