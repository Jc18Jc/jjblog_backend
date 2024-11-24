package org.jj.jjblog.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.config.filter.LoginFilter;
import org.jj.jjblog.config.filter.RefreshTokenFilter;
import org.jj.jjblog.config.filter.TokenCheckFilter;
import org.jj.jjblog.config.handler.LoginSuccessHandler;
import org.jj.jjblog.util.JWTUtil;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;


@Log4j2
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final CustomUserDetailService userDetailService;
    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        log.info("-----------------configure------------");
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);
        // LoginFilter
        LoginFilter loginFilter = new LoginFilter("/api/generateToken");
        loginFilter.setAuthenticationManager(authenticationManager);
        //APILoginSuccessHandler
        LoginSuccessHandler successHandler = new LoginSuccessHandler(jwtUtil);
        //SuccessHandler 세팅
        loginFilter.setAuthenticationSuccessHandler(successHandler);
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(tokenCheckFilter(jwtUtil, userDetailService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new RefreshTokenFilter("/api/refreshToken", jwtUtil), TokenCheckFilter.class);

        //http.formLogin(form -> form.loginPage("/member/login")).csrf(csrf -> csrf.disable());
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // cors는 외부 서버에서 jwt 발행 시 발생하는 문제인 동일 출처 정책을 해결하기 위함
        http.cors(cors->cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowedOrigins(List.of("http://localhost", "https://jjlog.vercel.app"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, CustomUserDetailService userDetailService) {
        return new TokenCheckFilter(userDetailService, jwtUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("----------------web configure-------------");
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
