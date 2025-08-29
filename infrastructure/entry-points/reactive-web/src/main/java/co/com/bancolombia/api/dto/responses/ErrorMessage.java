package co.com.bancolombia.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Detalle del error en la respuesta")
public record ErrorMessage(

        @Schema(description = "Código HTTP del error", example = "400")
        int statusCode,

        @Schema(description = "Fecha y hora del error", example = "2025-08-27T14:35:20")
        LocalDateTime timestamp,

        @Schema(description = "Mensaje descriptivo del error", example = "El nombre de usuario es obligatorio")
        String message,

        @Schema(description = "Ruta del endpoint donde ocurrió el error", example = "/auth/signin")
        String description
) {}
