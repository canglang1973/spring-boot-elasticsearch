package com.winxuan.es.utils;

import com.google.common.base.Joiner;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/27 0027-9:09
 * @version: 1.0
 * @description: ES index构造工具类
 **/
public class IndexUtils {

    public static final String OPEN_RESPONSE_TYPE = "open_response";

    public String getIndexNameWithDate(String type,DateTime dateTime) {
        if (null != dateTime && !StringUtils.isEmpty(type)) {
            return Joiner.on("-").join(type.toLowerCase(), dateTime.toString("yyyy-MM-dd"));
        }
        return null;
    }
}
