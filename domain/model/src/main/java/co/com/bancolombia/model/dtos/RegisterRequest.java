package co.com.bancolombia.model.dtos;

import java.time.LocalDate;
import java.util.Set;

public record RegisterRequest(
        String documentIdentification,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String direction,
        String telephoneNumber,
        String email,
        String password,
        Long baseSalary,
        Set<String> roles
) {
}
