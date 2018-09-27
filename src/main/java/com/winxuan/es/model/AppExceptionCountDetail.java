package com.winxuan.es.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/27 0027-15:19
 * @version: 1.0
 * @description:
 **/
@Data
public class AppExceptionCountDetail implements Serializable {

    /**
     * 应用键
     */
    private String appKey;

    /**
     * 方法名
     */
    private String method;

    /**
     * 异常类型
     */
    private String exceptionType;

    /**
     * 异常次数
     */
    private long total;

}
