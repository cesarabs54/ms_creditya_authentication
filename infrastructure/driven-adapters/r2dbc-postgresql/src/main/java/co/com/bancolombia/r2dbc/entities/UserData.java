package co.com.bancolombia.r2dbc.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserData implements Persistable<UUID> {

    @Id
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

    @Transient
    @SuppressWarnings("all")
    private transient boolean isNew = true;

    @Override
    public UUID getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setAsNotNew() {
        this.isNew = false;
    }
}
