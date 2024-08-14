package com.work.erpsystem.config;

import com.work.erpsystem.service.impl.UserServiceImpl;
import com.work.erpsystem.service.impl.auth.AuthenticationProviderImpl;
import com.work.erpsystem.service.impl.auth.RememberMeAuthProviderImpl;
import com.work.erpsystem.service.impl.auth.SuccessAuthHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final RememberMeAuthProviderImpl rememberMeAuthProvider;
    private final AuthenticationProviderImpl authenticationProvider;
    private final UserServiceImpl userService;
    private final DataSource dataSource;

    private @Value("${secret.key}") String secretKey;

    @Autowired
    public WebSecurityConfig(DataSource dataSource, RememberMeAuthProviderImpl rememberMeAuthProvider,
                             UserServiceImpl userService, AuthenticationProviderImpl authenticationProvider) {
        this.rememberMeAuthProvider = rememberMeAuthProvider;
        this.authenticationProvider = authenticationProvider;
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
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(authenticationProvider);
        builder.authenticationProvider(rememberMeAuthProvider);

        return builder.build();
    }

    @Bean
    public SuccessAuthHandler authHandler() {
        return new SuccessAuthHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
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
