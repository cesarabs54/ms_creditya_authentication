package co.com.bancolombia.usecase;

import co.com.bancolombia.model.dtos.RegisterRequest;
import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.enums.ERole;
import co.com.bancolombia.model.gateways.PasswordEncoderService;
import co.com.bancolombia.model.gateways.RoleRepository;
import co.com.bancolombia.model.gateways.UserRepository;
import co.com.bancolombia.usecase.api.security.RegisterUserUseCaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoderService passwordEncoderService;

    private RegisterUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCaseImpl(userRepository, roleRepository, passwordEncoderService);
    }

    private RegisterRequest buildRequest(Set<String> roles) {
        return new RegisterRequest(
                "1002003000",
                "Juan",
                "Pérez",
                LocalDate.of(1990, 5, 10),
                "Calle 1 # 2-3",
                "+57 3001112233",
                "jperez@example.com",
                "Secreta*123",
                5_000_000L,
                roles
        );
    }

    @Test
    @DisplayName("Registra usuario sin roles (asigna ROLE_APPLICANT por defecto)")
    void registerWithoutRoles_assignsDefaultRole() {
        RegisterRequest request = buildRequest(null);
        Role applicant = Role.builder().name(ERole.ROLE_APPLICANT).description("Default").build();

        when(userRepository.existsByEmail(request.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(request.documentIdentification())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(ERole.ROLE_APPLICANT)).thenReturn(Mono.just(applicant));
        when(passwordEncoderService.encode(request.password())).thenReturn(Mono.just("encoded-pass"));
        // capturamos el usuario que se persiste
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.execute(request))
                .expectNextMatches(saved ->
                        saved.getEmail().equals(request.email()) &&
                                saved.getPassword().equals("encoded-pass") &&
                                saved.getRoles() != null && saved.getRoles().size() == 1 &&
                                saved.getRoles().iterator().next().getName() == ERole.ROLE_APPLICANT
                )
                .verifyComplete();

        // verificaciones
        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByDocumentIdentification(request.documentIdentification());
        verify(roleRepository).findByName(ERole.ROLE_APPLICANT);
        verify(passwordEncoderService).encode(request.password());
        verify(userRepository).save(any(User.class));

        User persisted = userCaptor.getValue();
        // la contraseña no debe quedar en claro
        // (validamos al menos que cambió al valor codificado)
        assert persisted.getPassword().equals("encoded-pass");
    }

    @Test
    @DisplayName("Registra usuario con roles explícitos")
    void registerWithRoles_ok() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_CLIENT");
        RegisterRequest request = buildRequest(roles);

        when(userRepository.existsByEmail(request.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(request.documentIdentification())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Mono.just(Role.builder().name(ERole.ROLE_ADMIN).build()));
        when(roleRepository.findByName(ERole.ROLE_CLIENT)).thenReturn(Mono.just(Role.builder().name(ERole.ROLE_CLIENT).build()));
        when(passwordEncoderService.encode(request.password())).thenReturn(Mono.just("encoded"));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.execute(request))
                .assertNext(u -> {
                    assert u.getRoles() != null && u.getRoles().size() == 2;
                    assert u.getRolesAsString().contains("ROLE_ADMIN");
                    assert u.getRolesAsString().contains("ROLE_CLIENT");
                })
                .verifyComplete();

        verify(roleRepository).findByName(ERole.ROLE_ADMIN);
        verify(roleRepository).findByName(ERole.ROLE_CLIENT);
    }

    @Test
    @DisplayName("Falla si el correo ya está en uso")
    void failsWhenEmailExists() {
        RegisterRequest request = buildRequest(Set.of("ROLE_CLIENT"));

        when(userRepository.existsByEmail(request.email())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.execute(request))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException &&
                        err.getMessage().equals("El correo ya está en uso"))
                .verify();

        verify(userRepository, never()).existsByDocumentIdentification(anyString());
        verifyNoInteractions(roleRepository, passwordEncoderService);
    }

    @Test
    @DisplayName("Falla si el documento ya existe")
    void failsWhenDocumentExists() {
        RegisterRequest request = buildRequest(Set.of("ROLE_CLIENT"));

        when(userRepository.existsByEmail(request.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(request.documentIdentification())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.execute(request))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException &&
                        err.getMessage().equals("El número de documento ya existe"))
                .verify();

        verifyNoInteractions(roleRepository, passwordEncoderService);
    }

    @Test
    @DisplayName("Falla si ROLE_APPLICANT no existe cuando no envían roles")
    void failsWhenDefaultRoleMissing() {
        RegisterRequest request = buildRequest(null);

        when(userRepository.existsByEmail(request.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(request.documentIdentification())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(ERole.ROLE_APPLICANT)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(request))
                .expectErrorMatches(err -> err instanceof IllegalStateException &&
                        err.getMessage().equals("No se encontró el rol ROLE_APPLICANT"))
                .verify();

        verify(passwordEncoderService, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Falla si algún rol enviado no existe")
    void failsWhenProvidedRoleNotFound() {
        RegisterRequest request = buildRequest(Set.of("ROLE_CLIENT", "ROLE_X"));

        when(userRepository.existsByEmail(request.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(request.documentIdentification())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(ERole.ROLE_CLIENT)).thenReturn(Mono.just(Role.builder().name(ERole.ROLE_CLIENT).build()));
        // para ROLE_X, ERole.valueOf lanzará IllegalArgumentException antes de ir al repo
        // así que no se debe invocar roleRepository.findByName para ese valor

        StepVerifier.create(useCase.execute(request))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(roleRepository).findByName(ERole.ROLE_CLIENT);
        verify(passwordEncoderService, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

}
