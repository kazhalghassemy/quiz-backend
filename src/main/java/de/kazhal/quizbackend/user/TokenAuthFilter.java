package de.kazhal.quizbackend.user;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
/**
 * Liest "Authorization: Bearer <token>" und legt bei gültigem Token den Benutzer in den SecurityContext.
 * Bewusst KEIN @Component: Sonst würde Spring Boot den Filter zusätzlich außerhalb der Security-Kette
 registrieren.
 */
public class TokenAuthFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenService tokenService;
    public TokenAuthFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length()).trim();
            tokenService.authenticate(token).ifPresent(user -> {
                var authentication = UsernamePasswordAuthenticationToken.authenticated(
                        user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        filterChain.doFilter(request, response);   // immer weiterreichen
    }
}
