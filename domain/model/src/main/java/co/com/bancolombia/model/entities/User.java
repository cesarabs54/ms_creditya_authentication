package co.com.bancolombia.model.entities;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {

    private UUID userId;
    private String documentIdentification;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String direction;
    private String telephoneNumber;
    private String email;
    private String password;
    private Long baseSalary;
    private Set<Role> roles;

    public List<String> getRolesAsString() {
        return this.roles.stream()
                .map(role -> role.getName().name())
                .toList();
    }

}
