package demo.web.dao;

import demo.web.model.User;
import org.apache.ibatis.annotations.Select;

public interface UserDao {

    @Select("select * from  user where username = #{username}")
    User findByUsername(String username);

//    List<User> selectUsers();
}
