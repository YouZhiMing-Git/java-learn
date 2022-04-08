package com.youzm.springbootpgsql.dao;


import com.youzm.springbootpgsql.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author:youzhiming
 * @date: 2022/2/11
 * @description:
 */
@Mapper
public interface StudentMapper {
    List<Student> selectAll();
}
