package com.youzm.springBootMybatis.dao;


import com.youzm.springBootMybatis.entity.EtCcuInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EtCcuInfoMapper {

    List<EtCcuInfo> getAll();
}
