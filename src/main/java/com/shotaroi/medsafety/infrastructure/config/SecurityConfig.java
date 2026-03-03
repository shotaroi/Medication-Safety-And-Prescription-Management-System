package com.shotaroi.medsafety.infrastructure.config;

import com.shotaroi.medsafety.infrastructure.security.JwtAuthenticationFilter;
import com.shotaroi.medsafety.infrastructure.security.SecurityRoles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/actuator/**", "/api/ready", "/api/auth/token", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Patients: DOCTOR creates, DOCTOR/PHARMACIST view
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/patients").hasRole(SecurityRoles.DOCTOR)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/patients/**").hasAnyRole(SecurityRoles.DOCTOR, SecurityRoles.PHARMACIST)
                        // Medications: ADMIN manages, all roles can read for prescriptions
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/medications").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/medications/**").hasAnyRole(SecurityRoles.DOCTOR, SecurityRoles.PHARMACIST, SecurityRoles.ADMIN)
                        // Prescriptions: DOCTOR creates/schedule, PHARMACIST cancels, both view
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/prescriptions").hasRole(SecurityRoles.DOCTOR)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/prescriptions/**").hasAnyRole(SecurityRoles.DOCTOR, SecurityRoles.PHARMACIST)
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/prescriptions/**/cancel").hasRole(SecurityRoles.PHARMACIST)
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/prescriptions/**/schedule").hasRole(SecurityRoles.DOCTOR)
                        // Interactions: DOCTOR, PHARMACIST
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/interactions/check").hasAnyRole(SecurityRoles.DOCTOR, SecurityRoles.PHARMACIST)
                        // Audit: ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/audit").hasRole(SecurityRoles.ADMIN)
                        // Drug interaction rules: ADMIN
                        .requestMatchers("/api/drug-interactions/**").hasRole(SecurityRoles.ADMIN)
                        // Deny by default
                        .anyRequest().denyAll())
                .build();
    }
}
