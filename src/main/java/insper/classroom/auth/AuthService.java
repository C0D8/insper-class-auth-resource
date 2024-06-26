package insper.classroom.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import insper.classroom.account.AccountController;
import insper.classroom.account.AccountIn;
import insper.classroom.account.AccountOut;
import insper.classroom.account.LoginIn;
import insper.classroom.auth.LoginOut;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class AuthService {

    @Autowired
    private AccountController accountController;

    @Autowired
    private JwtService jwtService;

    @SuppressWarnings("null")
    public String register(Register in) {
        final String password = in.password().trim();
        if (null == password || password.isEmpty()) throw new IllegalArgumentException("Password is required");
        if (password.length() < 4) throw new IllegalArgumentException("Password must be at least 4 characters long");

        ResponseEntity<AccountOut> response = accountController.create(AccountIn.builder()
            .name(in.name())
            .email(in.email())
            .password(password)
            .build()
        );
        if (response.getStatusCode().isError()) throw new IllegalArgumentException("Invalid credentials");
        if (null == response.getBody()) throw new IllegalArgumentException("Invalid credentials");
        return response.getBody().id();
    }

    @CircuitBreaker(name = "auth", fallbackMethod = "fallbackAuthAuthenticate")
    public LoginOut authenticate(String email, String password) {
        ResponseEntity<AccountOut> response = accountController.login(LoginIn.builder()
            .email(email)
            .password(password)
            .build()
        );
        if (response.getStatusCode().isError()) throw new IllegalArgumentException("Invalid credentials");
        if (null == response.getBody()) throw new IllegalArgumentException("Invalid credentials");
        final AccountOut account = response.getBody();

        // Cria um token JWT
        @SuppressWarnings("null")
        final String token = jwtService.create(account.id(), account.name(), "regular");

        return LoginOut.builder()
            .token(token)
            .build();
    }

    public LoginOut fallbackAuthAuthenticate(String email, String password, Throwable t) {
        throw new IllegalArgumentException("Invalid credentials");
    }

    @CircuitBreaker(name = "auth", fallbackMethod = "fallbackAuthSolve")
    public Token solve(String token) {
        return jwtService.getToken(token);
    }

    public Token fallbackAuthSolve(String token, Throwable t) {
        throw new IllegalArgumentException("Invalid token");
    }
    
}
