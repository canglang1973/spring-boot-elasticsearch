package com.winxuan.es.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/27 0027-12:56
 * @version: 1.0
 * @description: 应用API调用明细
 **/
@Data
public class AppCountDetail implements Serializable {

    /**
     * 应用键
     */
    private String appKey;

    /**
     * 调用总次数 = 调用成功总次数+调用失败总次数
     */
    private long total = 0L;

    /**
     * 调用成功总次数
     */
    private long successed = 0L;

    /**
     * 调用失败总次数
     */
    private long failed = 0L;

    /**
     * 调用方法
     */
    private String method;

    /**
     * 调用方法总次数 = 调用方法成功总次数 + 调用方法失败总次数
     */
    private long methodTotal = 0L;

    /**
     * 调用方法成功总次数
     */
    private long methodSuccessed = 0L;

    /**
     * 调用方法失败总次数
     */
    private long methodFailed = 0L;

    /**
     * 应用调用成功率
     */
    private float appSuccessRatio;

    /**
     * 应用调用方法成功率
     */
    private float methodSuccessRatio;

    public float getAppSuccessRatio() {
        if (this.total > 0L) {
            this.appSuccessRatio = (float) this.successed / (float) this.total;
            BigDecimal bd = new BigDecimal(appSuccessRatio);
            //2 表示保留2位,4 表示四舍五入
            appSuccessRatio = new BigDecimal(appSuccessRatio).setScale(2, 4).floatValue();
        }
        return appSuccessRatio;
    }

    public float getMethodSuccessRatio() {
        if (this.methodTotal > 0L) {
            this.methodSuccessRatio = (float) this.methodSuccessed / (float) this.methodTotal;
            //2 表示保留2位,4 表示四舍五入
            methodSuccessRatio = new BigDecimal(methodSuccessRatio).setScale(2, 4).floatValue();
        }
        return methodSuccessRatio;
    }
}
