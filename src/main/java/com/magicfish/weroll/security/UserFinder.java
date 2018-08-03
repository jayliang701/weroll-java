package com.magicfish.weroll.security;

import com.magicfish.weroll.dao.UserDao;
import com.magicfish.weroll.model.User;
import com.magicfish.weroll.model.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserFinder implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userDao.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.withUsername(username) //
                        .password(user.getPassword()) //
                        .authorities(user.getJS()) //
                        .accountExpired(false) //
                        .accountLocked(false) //
                        .credentialsExpired(false) //
                        .disabled(false) //
                        .build();
        return new UserAuth(user.getId(), userDetails);
    }
}
