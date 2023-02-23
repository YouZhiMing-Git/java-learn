package com.youzm.serviceImpl;

import com.youzm.service.TestService;

/**
 * @author:youzhiming
 * @date: 2022/11/4
 * @description:
 */
public class TestServiceImpl  implements TestService {
    @Override
    public String test01() {
        System.out.println("==========test01==========");
        return "=====test01=====";
    }
}
