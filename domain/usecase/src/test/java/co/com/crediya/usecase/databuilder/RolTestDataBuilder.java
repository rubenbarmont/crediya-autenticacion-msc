package co.com.crediya.usecase.databuilder;

import co.com.crediya.model.rol.Rol;

public class RolTestDataBuilder {
    private Long idRol;
    private String name;

    public RolTestDataBuilder() {
        this.idRol = 1L;
        this.name = "CLIENTE";
    }

    public RolTestDataBuilder withIdRol(Long idRol) {
        this.idRol = idRol;
        return this;
    }

    public RolTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Rol build() {
        return Rol.builder()
                .idRol(idRol)
                .name(name)
                .description("Test Role")
                .build();
    }
}
