package co.com.bancolombia.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta de autenticación que incluye tokens y datos del usuario")
public record JwtResponse(

        @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzUxMiJ9...")
        String token,

        @Schema(description = "Refresh token para renovar el token de acceso", example = "eyJhbGciOiJIUzUxMiJ9.refresh...")
        String refreshToken,

        @Schema(description = "Identificador único del usuario", example = "12345")
        String userId,

        @Schema(description = "Nombre del usuario", example = "Cesar")
        String firstName,

        @Schema(description = "Apellido del usuario", example = "Alfonso")
        String lastName,

        @Schema(description = "Correo electrónico", example = "jdoe@example.com")
        String email,

        @Schema(description = "Lista de roles asignados al usuario", example = "[\"USER\", \"ADMIN\"]")
        List<String> roles
) {
    private static final String TOKEN_TYPE = "Bearer";

    @JsonGetter("tokenType")
    @Schema(description = "Tipo de token devuelto", example = "Bearer")
    public String tokenType() {
        return TOKEN_TYPE;
    }
}
