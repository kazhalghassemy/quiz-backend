package de.kazhal.quizbackend.user;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService,
                       UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(),
                        request.password()));
        QuizUserDetails principal = (QuizUserDetails) authentication.getPrincipal();
        IssuedToken issued = tokenService.issueToken(principal.id());
        return new LoginResponse(issued.token(), "Bearer", issued.expiresAt(),
                userService.findUser(principal.id()));
    }
}
