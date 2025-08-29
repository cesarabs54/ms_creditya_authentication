package co.com.bancolombia.api.security.service;

import co.com.bancolombia.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class SecurityUserDetails implements UserDetails {

    private final String userId;
    private final String documentIdentification;
    private final String firstName;
    private final String lastName;
    private final String birthDate;
    private final String direction;
    private final String telephoneNumber;
    private final String email;
    private final String password;
    private final Long baseSalary;
    private final Collection<? extends GrantedAuthority> authorities;

    public static Mono<SecurityUserDetails> build(User user) {
        return Mono.fromSupplier(() -> {
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());

            return new SecurityUserDetails(
                    user.getUserId().toString(),
                    user.getDocumentIdentification(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getBirthDate().toString(),
                    user.getDirection(),
                    user.getTelephoneNumber(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getBaseSalary(),
                    authorities
            );
        });
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityUserDetails that)) {
            return false;
        }
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String getUsername() {
        return "";
    }
}
