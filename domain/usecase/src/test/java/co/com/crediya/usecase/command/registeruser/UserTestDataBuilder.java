package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UserTestDataBuilder {
    private Long idUser;
    private Long identityDocument;
    private String name;
    private String email;
    private String password;
    private Long idRol;

    public UserTestDataBuilder() {
        this.idUser = 1L;
        this.identityDocument = 12345L;
        this.name = "Test User";
        this.email = "test@test.com";
        this.password = "password123";
        this.idRol = 1L;
    }

    public UserTestDataBuilder withIdUser(Long idUser) {
        this.idUser = idUser;
        return this;
    }

    public UserTestDataBuilder withIdentityDocument(Long identityDocument) {
        this.identityDocument = identityDocument;
        return this;
    }

    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestDataBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestDataBuilder withIdRol(Long idRol) {
        this.idRol = idRol;
        return this;
    }

    public User build() {
        return User.builder()
                .idUser(idUser)
                .identityDocument(identityDocument)
                .name(name)
                .lastName("Test")
                .baseSalary(new BigDecimal("2000000"))
                .email(email)
                .password(password)
                .idRol(idRol)
                .build();
    }
}

