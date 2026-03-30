package com.faktum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public read endpoints
                .requestMatchers(HttpMethod.GET, "/api/fiches/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stats/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/questions/**").permitAll()
                // Public question submission
                .requestMatchers(HttpMethod.POST, "/api/questions").permitAll()
                // Editor endpoints
                .requestMatchers(HttpMethod.POST, "/api/fiches").hasRole("EDITOR")
                .requestMatchers(HttpMethod.PUT, "/api/fiches/{slug}").hasRole("EDITOR")
                .requestMatchers(HttpMethod.POST, "/api/fiches/{slug}/translate").hasRole("EDITOR")
                // Admin endpoints
                .requestMatchers(HttpMethod.POST, "/api/fiches/{slug}/publish").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/fiches/{slug}/archive").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/questions/{id}/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/questions/{id}/reject").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/questions/{id}/answer").hasRole("ADMIN")
                // SPA static resources
                .requestMatchers("/", "/index.html", "/callback", "/assets/**", "/*.js", "/*.css", "/*.ico").permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized\"}");
                })
            )
            .anonymous(anonymous -> {});
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            List<String> groups = jwt.getClaimAsStringList("groups");
            if (groups != null) {
                for (String group : groups) {
                    String lower = group.toLowerCase();
                    if (lower.contains("admin")) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_EDITOR"));
                    }
                    if (lower.contains("editor")) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_EDITOR"));
                    }
                }
            }
            return authorities;
        });
        return converter;
    }

    @Bean
    @Profile("dev")
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
