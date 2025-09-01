package co.com.crediya.usecase.command.registeruser;

import co.com.crediya.model.user.User;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UserTestDataBuilder {

    private Long idUser;
    private Long identityDocument;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal baseSalary;

    public UserTestDataBuilder() {
        // Provide valid default values for a standard user
        this.name = "John";
        this.lastName = "Doe";
        this.email = "john.doe@example.com";
        this.baseSalary = new BigDecimal("5000000");
    }

    public UserTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserTestDataBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestDataBuilder withBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
        return this;
    }

    public User build() {
        return User.builder()
                .idUser(this.idUser)
                .identityDocument(this.identityDocument)
                .name(this.name)
                .lastName(this.lastName)
                .birthDate(this.birthDate)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .baseSalary(this.baseSalary)
                .build();
    }
}

