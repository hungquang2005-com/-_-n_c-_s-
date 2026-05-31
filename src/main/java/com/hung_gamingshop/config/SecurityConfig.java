package com.hung_gamingshop.config;

import com.hung_gamingshop.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ========================================================
    // FILTER CHAIN 1: ADMIN  (ưu tiên cao hơn - @Order(1))
    // Chỉ xử lý các request bắt đầu bằng /admin/**
    // ========================================================
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            // Chain này chỉ áp dụng cho /admin/**
            .securityMatcher("/admin/**")
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập trang login admin
                .requestMatchers("/admin/auth/login").permitAll()
                // Tất cả /admin/** còn lại phải có ROLE_ADMIN
                .anyRequest().hasRole("ADMIN")
            )
            // Form login riêng cho admin
            .formLogin(form -> form
                .loginPage("/admin/auth/login")
                .loginProcessingUrl("/admin/auth/login")
                .usernameParameter("loginId")
                .passwordParameter("password")
                .successHandler(adminLoginSuccessHandler())
                // Thất bại → quay lại trang login admin
                .failureUrl("/admin/auth/login?error=true")
                .permitAll()
            )
            // Logout riêng cho admin → quay về login admin
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/admin/auth/logout"))
                .logoutSuccessUrl("/admin/auth/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            // Nếu user thường cố vào /admin → redirect về login admin (không phải 403)
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendRedirect("/admin/auth/login"))
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendRedirect("/admin/auth/login"))
            );

        return http.build();
    }

    // ========================================================
    // FILTER CHAIN 2: USER  (@Order(2) - xử lý phần còn lại)
    // ========================================================
    @Bean
    @Order(2)
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Static resources: public
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // Trang login / register: public
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                // Các trang khác PHẢI đăng nhập (bao gồm cả "/", "/home", "/products/...")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("loginId")
                .passwordParameter("password")
                // Sau đăng nhập thành công → vào trang chủ user
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                // Chưa đăng nhập → về trang login user
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendRedirect("/auth/login"))
            );

        return http.build();
    }

    // Success handler cho admin: luôn vào /admin/dashboard
    @Bean
    public AuthenticationSuccessHandler adminLoginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (isAdmin) {
                    response.sendRedirect("/admin/dashboard");
                } else {
                    // Không phải admin → đá về login admin với lỗi
                    response.sendRedirect("/admin/auth/login?error=true");
                }
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }
}