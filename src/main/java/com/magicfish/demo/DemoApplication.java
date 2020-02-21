package com.magicfish.demo;

import com.magicfish.weroll.Weroll;
import com.magicfish.weroll.WerollApplication;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Weroll
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		String env = "localdev";
		String[] params = (String[]) ArrayUtils.addAll(new String[] { "--spring.profiles.active=" + env }, args);
        WerollApplication.run(DemoApplication.class, params);
	}

}