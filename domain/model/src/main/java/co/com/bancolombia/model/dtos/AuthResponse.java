package co.com.bancolombia.model.dtos;

import java.util.List;

public record AuthResponse(
        String token,
        String refreshToken,
        String userId,
        String firstName,
        String lastName,
        String email,
        List<String> roles
) {
}

