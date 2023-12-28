package org.zerock.b01.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomUserDetailsService;
import org.zerock.b01.security.handler.Custom403Handler;
import org.zerock.b01.security.handler.CustomSocialLoginSuccessHandler;

import javax.sql.DataSource;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
    //remember-me 쿠키설정 테이블로 보관하려면 data source와 userdetailsservice 객체 필요
    private final DataSource dataSource;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("-----------------configure-------------------");
        log.info("========confire==========");

        //커스텀 로그인 페이지
        http.formLogin(
                config -> {
            config.loginPage("/member/login");
                } );
        //csrf 토큰 비활성화
        http.csrf(config-> config.disable());
        //remember-me 사용
        http.rememberMe(
                config -> {
                    config.key("12345678").tokenRepository(persistentTokenRepository())
                            .userDetailsService(userDetailsService)
                            .tokenValiditySeconds(60*60*24*30);
                });
        http.exceptionHandling( config -> {
                config.accessDeniedHandler(accessDeniedHandler());
        });

        http.oauth2Login(config -> {
            config.loginPage("/member/login")
                    .successHandler(authenticationSuccessHandler());
        } );

        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }

    @Bean
    public WebSecurityCustomizer sebSecurityCustomizer(){
        log.info("------------------------configure------------------------");
        return (web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources()
                        .atCommonLocations()));

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }

}
