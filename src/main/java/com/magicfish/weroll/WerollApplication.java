package com.magicfish.weroll;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.magicfish.weroll.dao")
@SpringBootApplication
public class WerollApplication {

	public static void main(String[] args) {
		SpringApplication.run(WerollApplication.class, args);
	}
}
