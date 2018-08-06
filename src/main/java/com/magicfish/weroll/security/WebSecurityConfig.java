package com.magicfish.weroll.security;

import com.magicfish.weroll.config.AuthConfiguration;
import com.magicfish.weroll.config.GlobalConfiguration;
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

import java.util.Set;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private GlobalConfiguration globalConfiguration;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        AuthConfiguration authConfiguration = globalConfiguration.getAuth();

        // Entry points
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        Set<String> whitelist = authConfiguration.getPublicPaths();
        for(String path : whitelist){
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
        http.apply(new JwtTokenFilterConfigure(jwtTokenProvider));

        // Optional, if you want to test the API from a browser
        // http.httpBasic();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Allow swagger to be accessed without authentication
        web.ignoring().antMatchers("/v2/api-docs")//
            .antMatchers("/swagger-resources/**")//
            .antMatchers("/swagger-ui.html")//
            .antMatchers("/configuration/**")//
            .antMatchers("/webjars/**")//
            .antMatchers("/public");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

}
