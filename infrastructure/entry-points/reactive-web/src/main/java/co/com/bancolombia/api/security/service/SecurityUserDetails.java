package co.com.bancolombia.api.security.service;

import co.com.bancolombia.model.entities.User;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@Getter
@AllArgsConstructor
public class SecurityUserDetails implements UserDetails {

    private final String id;
    private final String username;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public static Mono<SecurityUserDetails> build(User user) {
        return Mono.fromSupplier(() -> {
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());

            return new SecurityUserDetails(
                    user.getUserId().toString(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
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
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
