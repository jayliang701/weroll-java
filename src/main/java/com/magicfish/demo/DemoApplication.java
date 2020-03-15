package com.magicfish.demo;

import com.magicfish.weroll.Weroll;
import com.magicfish.weroll.WerollApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Weroll
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
        WerollApplication.run(DemoApplication.class, args);
	}

}