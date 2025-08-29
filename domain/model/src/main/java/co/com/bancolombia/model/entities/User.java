package co.com.bancolombia.model;

import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID userId;
    private String username;
    private String name;
    private String email;
    private String password;
    private Set<Role> roles;

    public List<String> getRolesAsString() {
        return this.roles.stream()
                .map(role -> role.getName().name())
                .toList();
    }
}
