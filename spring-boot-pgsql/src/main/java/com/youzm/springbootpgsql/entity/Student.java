package com.youzm.springbootpgsql.entity;

import lombok.Data;

/**
 * @author:youzhiming
 * @date: 2022/2/11
 * @description:
 */
@Data
public class Student {
    String no;
    String name;
    int age;
    String sex;
    String description;
}
