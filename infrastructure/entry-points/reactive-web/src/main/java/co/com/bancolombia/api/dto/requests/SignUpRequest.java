package co.com.bancolombia.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;

@Schema(description = "Petición para registrar un nuevo usuario")
public record SignUpRequest(

        @NotBlank
        @Schema(description = "Documento de identificación", example = "88111111")
        String documentIdentification,

        @NotBlank
        @Schema(description = "Nombre del usuario", example = "Cesar")
        String firstName,

        @NotBlank
        @Schema(description = "Apellido del usuario", example = "Alfonso")
        String lastName,

        @NotBlank
        @Schema(description = "Fecha de nacimiento", example = "10/11/1980")
        String birthDate,

        @NotBlank
        @Schema(description = "Dirección del usuario", example = "Calle 123 #45-67")
        String direction,

        @NotBlank
        @Schema(description = "Número de teléfono", example = "3001234567")
        String telephoneNumber,

        @NotBlank
        @Size(max = 50)
        @Email
        @Schema(description = "Correo electrónico válido", example = "cesar@correo.com")
        String email,

        @NotBlank
        @Size(min = 6, max = 40)
        @Schema(description = "Contraseña del usuario", example = "password123")
        String password,

        @NotNull
        @PositiveOrZero
        @DecimalMax(value = "15000000", message = "El valor máximo permitido es te 15.000.000")
        @Schema(description = "Salario base del usuario", example = "2500000")
        Long baseSalary,

        @Schema(description = "Roles asignados al usuario", example = "[\"ROLE_APPLICANT\", \"ROLE_CLIENT\", \"ROLE_ADMIN\"]")
        Set<String> roles
) {}
