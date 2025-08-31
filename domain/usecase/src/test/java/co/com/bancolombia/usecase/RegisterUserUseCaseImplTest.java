package co.com.bancolombia.usecase;

import co.com.bancolombia.model.dtos.RegisterRequest;
import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.enums.ERole;
import co.com.bancolombia.model.exceptions.DuplicateResourceException;
import co.com.bancolombia.model.exceptions.ResourceNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
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

    private RegisterRequest construirSolicitud(Set<String> roles) {
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
    @DisplayName("Registra usuario sin roles → asigna ROLE_APPLICANT por defecto")
    void registraSinRoles_asignaRolPorDefecto() {
        // Arrange
        RegisterRequest solicitud = construirSolicitud(null);
        Role applicant = Role.builder().name(ERole.ROLE_APPLICANT).description("Default").build();

        when(userRepository.existsByEmail(solicitud.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(solicitud.documentIdentification())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(ERole.ROLE_APPLICANT)).thenReturn(Mono.just(applicant));
        when(passwordEncoderService.encode(solicitud.password())).thenReturn(Mono.just("encoded-pass"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // Act & Assert
        StepVerifier.create(useCase.execute(solicitud))
                .assertNext(guardado -> {
                    assertEquals(solicitud.email(), guardado.getEmail());
                    assertEquals("encoded-pass", guardado.getPassword());
                    assertNotNull(guardado.getRoles());
                    assertEquals(1, guardado.getRoles().size());
                    assertEquals(ERole.ROLE_APPLICANT, guardado.getRoles().iterator().next().getName());
                })
                .verifyComplete();

        verify(userRepository).existsByEmail(solicitud.email());
        verify(userRepository).existsByDocumentIdentification(solicitud.documentIdentification());
        verify(roleRepository).findByName(ERole.ROLE_APPLICANT);
        verify(passwordEncoderService).encode(solicitud.password());
        verify(userRepository).save(any(User.class));

        User persistido = userCaptor.getValue();
        assertEquals("encoded-pass", persistido.getPassword());
    }

    @Test
    @DisplayName("Registra usuario con roles explícitos")
    void registraConRoles_ok() {
        // Arrange
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_CLIENT");
        RegisterRequest solicitud = construirSolicitud(roles);

        when(userRepository.existsByEmail(solicitud.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(solicitud.documentIdentification())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Mono.just(Role.builder().name(ERole.ROLE_ADMIN).build()));
        when(roleRepository.findByName(ERole.ROLE_CLIENT)).thenReturn(Mono.just(Role.builder().name(ERole.ROLE_CLIENT).build()));
        when(passwordEncoderService.encode(solicitud.password())).thenReturn(Mono.just("encoded"));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // Act & Assert
        StepVerifier.create(useCase.execute(solicitud))
                .assertNext(u -> {
                    assertNotNull(u.getRoles());
                    assertEquals(2, u.getRoles().size());
                    assertTrue(u.getRolesAsString().contains("ROLE_ADMIN"));
                    assertTrue(u.getRolesAsString().contains("ROLE_CLIENT"));
                })
                .verifyComplete();

        verify(roleRepository).findByName(ERole.ROLE_ADMIN);
        verify(roleRepository).findByName(ERole.ROLE_CLIENT);
    }

    @Test
    @DisplayName("Falla si el correo ya está en uso")
    void fallaSiCorreoExiste() {
        // Arrange
        RegisterRequest solicitud = construirSolicitud(Set.of("ROLE_CLIENT"));
        when(userRepository.existsByEmail(solicitud.email())).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(useCase.execute(solicitud))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof DuplicateResourceException);
                    assertEquals("El correo ya está en uso: " + solicitud.email(), err.getMessage());
                })
                .verify();

        verify(userRepository, never()).existsByDocumentIdentification(anyString());
        verifyNoInteractions(roleRepository, passwordEncoderService);
    }

    @Test
    @DisplayName("Falla si el documento ya existe")
    void fallaSiDocumentoExiste() {
        // Arrange
        RegisterRequest solicitud = construirSolicitud(Set.of("ROLE_CLIENT"));
        when(userRepository.existsByEmail(solicitud.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(solicitud.documentIdentification())).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(useCase.execute(solicitud))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof DuplicateResourceException);
                    assertEquals("El número de documento ya existe: " + solicitud.documentIdentification(), err.getMessage());
                })
                .verify();

        verifyNoInteractions(roleRepository, passwordEncoderService);
    }

    @Test
    @DisplayName("Falla si ROLE_APPLICANT no existe cuando no envían roles")
    void fallaSiFaltaRolPorDefecto() {
        // Arrange
        RegisterRequest solicitud = construirSolicitud(null);
        when(userRepository.existsByEmail(solicitud.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(solicitud.documentIdentification())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(ERole.ROLE_APPLICANT)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(solicitud))
                .expectErrorSatisfies(err -> {
                    assertTrue(err instanceof ResourceNotFoundException);
                    assertEquals("No se encontró el rol ROLE_APPLICANT", err.getMessage());
                })
                .verify();

        verify(passwordEncoderService, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Falla si algún rol enviado no existe (valor fuera del enum)")
    void fallaSiRolEnviadoNoExiste() {
        // Arrange
        // Usamos un Set que podría iterar en cualquier orden
        RegisterRequest solicitud = construirSolicitud(Set.of("ROLE_CLIENT", "ROLE_X"));
        when(userRepository.existsByEmail(solicitud.email())).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentIdentification(solicitud.documentIdentification())).thenReturn(Mono.just(false));
        lenient().when(roleRepository.findByName(ERole.ROLE_CLIENT))
                .thenReturn(Mono.just(Role.builder().name(ERole.ROLE_CLIENT).build()));
        // Para ROLE_X, ERole.valueOf(role) lanza IllegalArgumentException antes de ir al repositorio

        // Act & Assert
        StepVerifier.create(useCase.execute(solicitud))
                .expectError(IllegalArgumentException.class)
                .verify();

        // El repositorio puede o no ser llamado dependiendo del orden de iteración del Set.
        // Relajamos la verificación para evitar falsos negativos en PIT.
        verify(roleRepository, atMost(1)).findByName(ERole.ROLE_CLIENT);
        verify(passwordEncoderService, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

}
