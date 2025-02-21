package com.stepup.ims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Publicly accessible endpoints
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .formLogin(form -> form
                        .loginPage("/login")               // Set custom login page
                        .defaultSuccessUrl("/client/list")        // Redirect after successful login
                        .permitAll()                       // Allow all to access the login page
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")              // Logout URL
                        .logoutSuccessUrl("/login?logout") // Redirect after logout
                        .permitAll()                       // Allow all to log out successfully
                );
        return http.build();
    }
}
