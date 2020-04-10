package com.zjm;

import com.zjm.config.elasticSearch.ElasticSearchConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author White Tan
 * @description
 * @date 2020/3/11
 */
@SpringBootApplication
//@SpringBootApplication(exclude = {ElasticSearchConfig.class}) //禁止springboot自动注入es配置
@EnableAsync //可以异步执行，就是开启多线程
	//表示开启AOP代理自动配置，如果配@EnableAspectJAutoProxy表示使用cglib进行代理对象的生成；
	// 设置@EnableAspectJAutoProxy(exposeProxy=true)表示通过aop框架暴露该代理对象，aopContext能够访问.
	// https://blog.csdn.net/pml18710973036/article/details/61654277
//@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan("com.zjm.*")
//@MapperScan(basePackages = "com.yonghui.yh.rme.srm.inventorycenter.dao.mapper")
public class EsApplication {
	public static void main(String[] args) {
		SpringApplication.run(EsApplication.class, args);
	}
}
