package com.magicfish.weroll;

import com.magicfish.weroll.dao.UserDao;
import com.magicfish.weroll.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WerollApplicationTests {

	@Autowired
	private UserDao userDao;

	@Test
	@Rollback
	public void findByName() throws Exception {
		User user = userDao.findByUsername("admin");
		Assert.assertEquals(user.getUsername(), "admin");
	}

}
