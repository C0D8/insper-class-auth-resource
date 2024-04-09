package insper.classroom.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import insper.classroom.auth.AuthController;
import insper.classroom.auth.CredentialIn;
import insper.classroom.auth.LoginOut;
import insper.classroom.auth.RegisterIn;
import insper.classroom.auth.SolveIn;
import insper.classroom.auth.SolveOut;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@Tag(name = "Auth Resource", description = "Auth Resource")
public class AuthResource implements AuthController {

    @Autowired
    private AuthService authService;

    @Override
    @Operation(summary = "Create a new account", description = "Create a new account")
    public ResponseEntity<?> create(RegisterIn in) {

        final String id = authService.register(Register.builder()
            .name(in.name())
            .email(in.email())
            .password(in.password())
            .build()
        );

        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri())
            .build();
    }

    @Override
    @Operation(summary = "Authenticate", description = "Authenticate")
    public ResponseEntity<LoginOut> authenticate(CredentialIn in) {
        return ResponseEntity.ok(authService.authenticate(in.email(), in.password()));
    }

    @Override
    @Operation(summary = "Solve", description = "Solve")
    public ResponseEntity<SolveOut> solve(SolveIn in) {
        final Token token = authService.solve(in.token());
        return ResponseEntity.ok(
            SolveOut.builder()
                .id(token.id())
                .name(token.name())
                .role(token.role())
                .build()
        );
    }

}
