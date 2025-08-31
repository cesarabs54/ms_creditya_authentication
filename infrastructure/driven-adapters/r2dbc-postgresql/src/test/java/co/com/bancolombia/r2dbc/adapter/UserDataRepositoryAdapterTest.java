package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.r2dbc.entities.UserData;
import co.com.bancolombia.r2dbc.entities.UserRoleData;
import co.com.bancolombia.r2dbc.mappers.UserMapper;
import co.com.bancolombia.r2dbc.repositories.UserDataRepository;
import co.com.bancolombia.r2dbc.repositories.UserRoleDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataRepositoryAdapterTest {

    @Mock
    private UserDataRepository userDataRepository;
    @Mock
    private UserRoleDataRepository userRoleDataRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserDataRepositoryAdapter adapter;

    private Role role1;
    private Role role2;

    @BeforeEach
    void init() {
        role1 = Role.builder().roleId(UUID.randomUUID()).build();
        role2 = Role.builder().roleId(UUID.randomUUID()).build();
    }

    @Test
    void save_NewUserWithRoles_Success() {
        // given: usuario sin userId y con roles
        User incoming = User.builder()
                .documentIdentification("123")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john@doe.com")
                .password("secret")
                .baseSalary(1000L)
                .roles(Set.of(role1, role2))
                .build();

        // El mapper creará un UserData tomando el userId del User que reciba (que será seteado por el adapter)
        lenient().when(userMapper.toData(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return UserData.builder()
                    .userId(u.getUserId())
                    .documentIdentification(u.getDocumentIdentification())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .birthDate(u.getBirthDate())
                    .address(u.getAddress())
                    .telephoneNumber(u.getTelephoneNumber())
                    .email(u.getEmail())
                    .password(u.getPassword())
                    .baseSalary(u.getBaseSalary())
                    .build();
        });

        // Mock del repository.save(userData): retorna el mismo UserData como si lo hubiera persistido
        when(userDataRepository.save(any(UserData.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Mock del guardado de roles: retorna el mismo UserRoleData
        when(userRoleDataRepository.save(any(UserRoleData.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // El mapper de vuelta a modelo
        when(userMapper.toModel(any(UserData.class))).thenAnswer(invocation -> {
            UserData d = invocation.getArgument(0);
            return User.builder()
                    .userId(d.getUserId())
                    .documentIdentification(d.getDocumentIdentification())
                    .firstName(d.getFirstName())
                    .lastName(d.getLastName())
                    .birthDate(d.getBirthDate())
                    .email(d.getEmail())
                    .password(d.getPassword())
                    .baseSalary(d.getBaseSalary())
                    .roles(Set.of(role1, role2))
                    .build();
        });

        // when
        Mono<User> result = adapter.save(incoming);

        // then
        StepVerifier.create(result)
                .assertNext(saved -> {
                    // Debe tener un userId asignado por el adapter
                    assert saved.getUserId() != null;
                    assert saved.getEmail().equals("john@doe.com");
                    assert saved.getRoles() != null && saved.getRoles().size() == 2;
                })
                .verifyComplete();

        // Verifica que se guardó el usuario y dos roles
        verify(userDataRepository, times(1)).save(any(UserData.class));
        verify(userRoleDataRepository, times(2)).save(any(UserRoleData.class));
        verify(userMapper, times(1)).toModel(any(UserData.class));
    }

    @Test
    void save_ExistingUser_UpdatesWithoutSavingRoles() {
        // given: usuario existente con userId predefinido
        UUID existingId = UUID.randomUUID();
        User incoming = User.builder()
                .userId(existingId)
                .firstName("Jane")
                .roles(Set.of(role1)) // Debe tener al menos un rol para pasar la validación inicial
                .build();

        UserData dataToSave = UserData.builder().userId(existingId).firstName("Jane").build();
        when(userMapper.toData(any(User.class))).thenReturn(dataToSave);
        when(userDataRepository.save(any(UserData.class))).thenReturn(Mono.just(dataToSave));
        when(userMapper.toModel(any(UserData.class))).thenReturn(
                User.builder().userId(existingId).firstName("Jane").roles(Set.of(role1)).build());

        // when
        Mono<User> result = adapter.save(incoming);

        // then
        StepVerifier.create(result)
                .expectNextMatches(u -> existingId.equals(u.getUserId()) && "Jane".equals(u.getFirstName()))
                .verifyComplete();

        verify(userDataRepository, times(1)).save(dataToSave);
        // No debe intentar guardar roles en la rama de update
        verify(userRoleDataRepository, never()).save(any());
    }

    @Test
    void save_WithoutRoles_ShouldError() {
        // given: usuario sin roles (null)
        User incomingNullRoles = User.builder().firstName("NoRoles").roles(null).build();
        StepVerifier.create(adapter.save(incomingNullRoles))
                .expectErrorSatisfies(err -> {
                    assert err instanceof IllegalArgumentException;
                    assert err.getMessage().contains("al menos un rol");
                })
                .verify();

        // given: usuario con roles vacíos
        User incomingEmptyRoles = User.builder().firstName("EmptyRoles").roles(Set.of()).build();
        StepVerifier.create(adapter.save(incomingEmptyRoles))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userDataRepository, userRoleDataRepository, userMapper);
    }

    @Test
    void save_NewUser_ErrorSavingRole_ShouldPropagateError() {
        // given: usuario nuevo con un rol
        User incoming = User.builder()
                .firstName("John")
                .roles(Set.of(role1))
                .build();

        // Mapear a data tomando el userId seteado por el adapter
        when(userMapper.toData(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return UserData.builder().userId(u.getUserId()).firstName(u.getFirstName()).build();
        });

        when(userDataRepository.save(any(UserData.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Fallo al guardar el rol
        when(userRoleDataRepository.save(any(UserRoleData.class)))
                .thenReturn(Mono.error(new RuntimeException("role insert error")));

        // No debería mapear a modelo porque la cadena falla antes
        // when
        Mono<User> result = adapter.save(incoming);

        // then
        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("role insert error"))
                .verify();

        verify(userDataRepository, times(1)).save(any(UserData.class));
        verify(userRoleDataRepository, times(1)).save(any(UserRoleData.class));
        verify(userMapper, never()).toModel(any(UserData.class));
    }
}