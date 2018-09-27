package com.winxuan.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/25 0025-14:06
 * @version: 1.0
 * @description:
 **/
@SpringBootApplication
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
        System.out.println("容器启动成功...");
    }

}
