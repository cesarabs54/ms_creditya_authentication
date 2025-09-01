package co.com.bancolombia.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Usuario sin campos sensibles")
public record UserResponse(
        @Schema(description = "ID del usuario") UUID userId,
        @Schema(description = "Documento") String documentIdentification,
        @Schema(description = "Nombres") String firstName,
        @Schema(description = "Apellidos") String lastName,
        @Schema(description = "Fecha de nacimiento") LocalDate birthDate,
        @Schema(description = "Dirección") String address,
        @Schema(description = "Teléfono") String telephoneNumber,
        @Schema(description = "Email") String email,
        @Schema(description = "Salario base") Long baseSalary,
        @Schema(description = "Roles del usuario") List<String> roles
) {
}
