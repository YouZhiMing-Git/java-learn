package com.youzm;

import com.youzm.service.TestConsumerService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author:youzhiming
 * @date: 2022/11/4
 * @description:
 */
@SpringBootApplication
@EnableDubbo
public class ConsumerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ConsumerApplication.class, args);
        TestConsumerService bean = applicationContext.getBean(TestConsumerService.class);
        bean.test();
    }
}
