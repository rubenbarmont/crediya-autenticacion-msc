package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserRequestDTO {

    @NotNull(message = "El campo 'documento de identidad' es obligatorio.")
    private Long identityDocument;

    @NotBlank(message = "El campo 'nombres' es obligatorio.")
    private String name;

    @NotBlank(message = "El campo 'apellidos' es obligatorio.")
    private String lastName;

    private LocalDate birthDate;
    private String address;
    private String phoneNumber;

    @NotBlank(message = "El campo 'correo_electronico' es obligatorio.")
    @Email(message = "El formato del correo electrónico no es válido.")
    private String email;

    @NotNull(message = "El campo 'salario_base' es obligatorio.")
    @DecimalMin(value = "0.0", message = "El salario base no puede ser negativo.")
    @DecimalMax(value = "15000000.0", message = "El salario base no puede exceder 15,000,000.")
    private BigDecimal baseSalary;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private Long idRol;
}
