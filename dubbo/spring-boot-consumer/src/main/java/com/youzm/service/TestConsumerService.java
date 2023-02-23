package com.youzm.service;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author:youzhiming
 * @date: 2022/11/4
 * @description:
 */
@Service
public class TestConsumerService {

    @DubboReference
    TestService testService;

    public void test(){
        System.out.println(testService.test01());
    }
}
