package com.work.erpsystem.config;

import com.work.erpsystem.service.impl.UserServiceImpl;
import com.work.erpsystem.service.impl.auth.SuccessAuthHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Persistent;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final DataSource dataSource;

    private @Value("${secret.key}") String secretKey;

    @Autowired
    public WebSecurityConfig(DataSource dataSource, UserServiceImpl userService,
                             AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.dataSource = dataSource;
    }

    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        return new PersistentTokenBasedRememberMeServices(secretKey, userDetailsService, tokenRepository());
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        return tokenRepository;
    }

    @Bean
    public SuccessAuthHandler authHandler() {
        return new SuccessAuthHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register", "/v3/**", "/api/auth/**", "/set_new_user/**").permitAll()
                        .anyRequest().authenticated()
                ).authenticationManager(authenticationManager)
                .securityContext(context -> new HttpSessionSecurityContextRepository())
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeServices(rememberMeServices(userService))
                        .tokenRepository(tokenRepository())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authHandler())
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
