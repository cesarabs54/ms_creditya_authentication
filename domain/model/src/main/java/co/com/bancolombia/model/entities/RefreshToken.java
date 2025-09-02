package co.com.bancolombia.model.entities;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    private UUID id;
    private String token;
    private Instant expiryDate;
    private User user;
}
