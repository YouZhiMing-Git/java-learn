package com.youzm.springBootMybatis.entity;

import lombok.Data;

/**
 * CCU信息表
 *
 * @author Adam
 */
@Data
public class EtCcuInfo {

    private int sid;
    private String ccuName;
    private String ip;
    private int port;
}
