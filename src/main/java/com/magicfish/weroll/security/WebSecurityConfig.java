package com.magicfish.weroll.security;

import com.magicfish.weroll.config.GlobalSetting;
import com.magicfish.weroll.config.property.AuthProperties;
import com.magicfish.weroll.security.encoder.MD5PasswordEncoder;
import com.magicfish.weroll.security.jwt.SessionTokenFilterConfigure;
import com.magicfish.weroll.security.jwt.SessionTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.SecureRandom;
import java.util.Set;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private GlobalSetting globalSetting;

    @Autowired
    private SessionTokenProvider sessionTokenProvider;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (!globalSetting.getAuth().isEnabled()) {
            http.authorizeRequests().anyRequest().permitAll();
            return;
        }

        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        AuthProperties authConfiguration = globalSetting.getAuth();

        // Entry points
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        Set<String> whitelist = authConfiguration.getPublicPaths();
        for (String path : whitelist) {
            registry = registry.antMatchers(path).permitAll();
        }
        registry.anyRequest().authenticated();

        String entryPoint = authConfiguration.getEntryPoint();
        // If a user try to access a resource without having enough permissions
        if (entryPoint != null && !entryPoint.isEmpty()) {
            http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(entryPoint));
        }

        String deniedRedirect = authConfiguration.getDeniedRedirect();
        // If a user try to access a resource without having enough permissions
        if (deniedRedirect != null && !deniedRedirect.isEmpty()) {
            http.exceptionHandling().accessDeniedPage(deniedRedirect);
        }

        // Apply JWT
        http.apply(new SessionTokenFilterConfigure(sessionTokenProvider));

        // Optional, if you want to test the API from a browser
        // http.httpBasic();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        if (!globalSetting.getAuth().isEnabled()) {
            web.ignoring().antMatchers("/**");
            return;
        }
        // Allow swagger to be accessed without authentication
        web.ignoring().antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**")
                .antMatchers("/webjars/**")
                .antMatchers("/public")
                .antMatchers("/static");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String method = globalSetting.getAuth().getPasswordEncodeMethod();
        String salt = globalSetting.getAuth().getPasswordEncodeSalt();
        if (method.equals(AuthProperties.MD5_ENCODE)) {
            return new MD5PasswordEncoder(salt);
        } else if (method.equals(AuthProperties.BCRYPT_ENCODE)) {
            int strength = globalSetting.getAuth().getPasswordEncodeStrength();
            if (salt != null && !salt.isEmpty()) {
                SecureRandom random = new SecureRandom(salt.getBytes());
                return new BCryptPasswordEncoder(strength, random);
            }
            return new BCryptPasswordEncoder(strength);
        }
        return null;
    }

}
