package co.com.bancolombia.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta genérica que contiene un mensaje informativo")
public record MessageResponse(

        @Schema(description = "Mensaje de confirmación o información", example = "Usuario creado correctamente")
        String message
) {

}
