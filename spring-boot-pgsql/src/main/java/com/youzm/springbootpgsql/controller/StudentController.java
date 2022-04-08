package com.youzm.springbootpgsql.controller;

import com.youzm.springbootpgsql.entity.Student;
import com.youzm.springbootpgsql.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author:youzhiming
 * @date: 2022/2/11
 * @description:
 */
@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @RequestMapping("/getAll")
   public List<Student> getAll(){
       return studentService.getAll();
   }
}
