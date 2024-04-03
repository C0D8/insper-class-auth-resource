package insper.classroom.auth;

import lombok.Builder;
import lombok.experimental.Accessors;

@Builder
@Accessors(fluent = true, chain = true)
public record Token (

        String id,
        String role,
        String name

) {
}