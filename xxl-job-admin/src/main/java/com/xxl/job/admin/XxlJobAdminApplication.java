package com.xxl.job.admin;

import com.leqee.boot.autoconfiguration.annotation.EnableCat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
@EnableCat
public class XxlJobAdminApplication {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobAdminApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(XxlJobAdminApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}