package com.magicfish.weroll;

import demo.web.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= DemoApplication.class)
public class WerollApplicationTests {

	@Test
	@Rollback
	public void findByName() throws Exception {

	}

}
