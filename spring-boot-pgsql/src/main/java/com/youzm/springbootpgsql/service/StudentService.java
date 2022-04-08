package com.youzm.springbootpgsql.service;

import com.youzm.springbootpgsql.dao.StudentMapper;
import com.youzm.springbootpgsql.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:youzhiming
 * @date: 2022/2/11
 * @description:
 */
@Service
public class StudentService {
    @Autowired
    StudentMapper studentMapper;
   public List<Student> getAll(){
        return studentMapper.selectAll();
    }

}
