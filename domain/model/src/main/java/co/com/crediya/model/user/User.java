package co.com.crediya.model.user;
import co.com.crediya.model.rol.Rol;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long idUser;
    private Long identityDocument;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private BigDecimal baseSalary;
    private Long idRol;
    private Rol rol;
}
