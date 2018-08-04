package demo.web.service;

import demo.web.dao.UserDao;
import com.magicfish.weroll.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String login(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, userDao.findByUsername(username).getJS());
        } catch (AuthenticationException e) {
            throw new Exception("Invalid username/password supplied");
        }
    }
    /*
    @Override
    public PageInfo<User> findAllUser(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<User> customerDomains = userDao.selectUsers();
        PageInfo result = new PageInfo(customerDomains);
        return result;
    }
    */
}
