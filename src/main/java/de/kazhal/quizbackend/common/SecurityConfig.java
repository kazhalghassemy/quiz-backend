package de.kazhal.quizbackend.common;

import de.kazhal.quizbackend.user.TokenAuthFilter;
import de.kazhal.quizbackend.user.TokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import
        org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, TokenService tokenService,
                                            @Qualifier("handlerExceptionResolver")
                                            HandlerExceptionResolver resolver)
            throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register",
                                "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/topics", "/api/topics/*").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new TokenAuthFilter(tokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        (request, response, ex) -> resolver.resolveException(request, response, null,
                                ex)))
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws
            Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}