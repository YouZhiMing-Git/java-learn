package com.youzm.springbootpgsql.dao;

import com.youzm.springbootpgsql.entity.JsonTest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JsonTestMapper {

    List<JsonTest> getAll();

    void insertData(@Param("data") JsonTest data);
}
