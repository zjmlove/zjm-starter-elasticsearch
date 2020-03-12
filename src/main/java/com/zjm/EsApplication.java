package com.zjm;

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
@ComponentScan("com.zjm.*")
public class EsApplication {
	public static void main(String[] args) {
		SpringApplication.run(EsApplication.class, args);
	}
}
