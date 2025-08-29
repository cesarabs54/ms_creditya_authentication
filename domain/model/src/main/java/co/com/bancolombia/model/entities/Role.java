package co.com.bancolombia.model.entities;

import co.com.bancolombia.model.enums.ERole;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private UUID roleId;
    private ERole name;
    private String description;
}
