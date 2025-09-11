package co.com.crediya.model.rol;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {
    private Long idRol;
    private String name;
    private String description;
}
