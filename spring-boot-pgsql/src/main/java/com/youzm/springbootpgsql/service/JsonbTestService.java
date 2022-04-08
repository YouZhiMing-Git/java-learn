package com.youzm.springbootpgsql.service;

import com.youzm.springbootpgsql.dao.JsonTestMapper;
import com.youzm.springbootpgsql.entity.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author:youzhiming
 * @date: 2022/2/15
 * @description:
 */
@Service
public class JsonbTestService {
    @Autowired
    JsonTestMapper jsonTestMapper;

    public void insert(JsonTest jsonTest){
        jsonTestMapper.insertData(jsonTest);
    }

    public List<JsonTest> getAll(){
        return  jsonTestMapper.getAll();
    }
}
