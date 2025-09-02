package co.com.bancolombia.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table(name = "refreshtoken")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenData implements Persistable<UUID> {

    @Id
    private UUID refreshTokenId;

    private UUID userId;

    private String token;

    private Instant expiryDate;

    @Transient
    @SuppressWarnings("all")
    private transient boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public UUID getId() {
        return refreshTokenId;
    }

    public void setAsNotNew() {
        this.isNew = false;
    }
}

