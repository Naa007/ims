package com.stepup.ims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static com.stepup.ims.constants.ApplicationConstants.*;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private static final String[] OPEN_ACCESS = {"/auth/**", "/js/**", "/css/**", "/images/**", "/login", "/", "/error/**"};
    private static final String[] ADMIN_ACCESS = {"/admin/**"};
    private static final String[] BUSINESS_ACCESS = {"/business/**"};
    private static final String[] COORDINATOR_ACCESS = {"/coordinator/**", "/inspection/**"};
    private static final String[] TECHNICAL_COORDINATOR_ACCESS = {"/technical-coordinator/**"};
    private static final String[] INSPECTOR_ACCESS = {"/inspector/**"};
    private static final String[] EMPLOYEE_ACCESS = {"/employee/**"};
    private static final String[] CLIENT_ACCESS = {"/client/**"};
    private static final String[] INSPECTORS_ACCESS = {"/inspectors/**"};
    private static final String[] STATS_ACCESS = {"/stats/**"};
    private static final String[] REPORT_ACCESS = {"/reports/**"};
    private static final String[] CALENDAR_ACCESS = {"/calendar/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(OPEN_ACCESS).permitAll()
                        .requestMatchers(ADMIN_ACCESS).hasRole(ADMIN)
                        .requestMatchers(BUSINESS_ACCESS).hasRole(BUSINESS)
                        .requestMatchers(COORDINATOR_ACCESS).hasAnyRole(COORDINATOR)
                        .requestMatchers(TECHNICAL_COORDINATOR_ACCESS).hasAnyRole(TECHNICAL_COORDINATOR)
                        .requestMatchers(INSPECTOR_ACCESS).hasAnyRole(INSPECTOR, TECHNICAL_COORDINATOR)
                        .requestMatchers(EMPLOYEE_ACCESS).hasAnyRole(ADMIN)
                        .requestMatchers(CLIENT_ACCESS).hasAnyRole(ADMIN, COORDINATOR)
                        .requestMatchers(INSPECTORS_ACCESS).hasAnyRole(ADMIN, COORDINATOR)
                        .requestMatchers(STATS_ACCESS).hasAnyRole(BUSINESS, COORDINATOR, TECHNICAL_COORDINATOR, INSPECTOR)
                        .requestMatchers(REPORT_ACCESS).hasAnyRole(COORDINATOR, ADMIN)
                        .requestMatchers(CALENDAR_ACCESS).hasAnyRole(COORDINATOR, ADMIN)
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1) // Allow a single active session per user
                        .maxSessionsPreventsLogin(false))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> response
                                .sendRedirect("/error")))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .headers(header -> header
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable));

        return http.build();
    }

}
