package co.com.bancolombia.model.dto;

import java.time.LocalDate;
import java.util.Set;

public record RegisterRequest(
        String documentIdentification,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        String telephoneNumber,
        String email,
        String password,
        Long baseSalary,
        Set<String> roles
) {
}
