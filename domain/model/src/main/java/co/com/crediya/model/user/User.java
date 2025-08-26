package co.com.crediya.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class User {

    private final UUID id;
    private final String name;
    private final String lastName;
    private final LocalDate birthDate;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final BigDecimal baseSalary;

}
