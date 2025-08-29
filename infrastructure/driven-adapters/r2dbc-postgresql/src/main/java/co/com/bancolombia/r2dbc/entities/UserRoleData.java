package co.com.bancolombia.r2dbc.entities;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleData {

    private UUID userId;
    private UUID roleId;

}
