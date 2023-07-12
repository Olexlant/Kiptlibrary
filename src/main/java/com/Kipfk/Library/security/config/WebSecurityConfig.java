package com.Kipfk.Library.security.config;

import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserRole;
import com.Kipfk.Library.appuser.AppUserService;
import com.Kipfk.Library.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.security.Principal;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(AppUserService appUserService, PasswordEncoder passwordEncoder) {
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        super.setApplicationContext(context);
        AuthenticationManagerBuilder globalAuthBuilder = context.getBean(AuthenticationManagerBuilder.class);
        try {
            globalAuthBuilder.userDetailsService(appUserService);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder());
        authProvider.setUserDetailsService(userDetailsService());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationSuccessHandler appAuthenticationSuccessHandler(){
        return new AppAuthenticationSuccessHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasAuthority(AppUserRole.ADMIN.name())
                .antMatchers("/register").permitAll()
                .antMatchers("/registration/**").permitAll()
                .antMatchers("/process_register").permitAll()
                .antMatchers("/resetpassword/**").permitAll()
                .antMatchers("/confirmsuccess").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/news/**").permitAll()
                .antMatchers("/book/image/**").permitAll()
                .antMatchers("/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .defaultSuccessUrl("/")
                    .permitAll()
                .and()
                .rememberMe()
                    .tokenValiditySeconds(7 * 24 * 60 * 60) // expiration time: 7 days
                    .key("remember-me")
                .and()
                .logout()
                .logoutSuccessUrl("/");
        http.cors().and().csrf().disable();
    }

}
