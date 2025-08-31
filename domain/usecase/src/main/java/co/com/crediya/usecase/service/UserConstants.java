package co.com.crediya.usecase.service;

import java.math.BigDecimal;

public final class UserConstants {

    private UserConstants() {
        // Clase de utilidad, no instanciable
    }

    // Valores de negocio
    public static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    public static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    // Nombres de campos para mensajes de error
    public static final String FIELD_NAME = "nombres";
    public static final String FIELD_LAST_NAME = "apellidos";
    public static final String FIELD_EMAIL = "correo_electronico";
    public static final String FIELD_BASE_SALARY = "salario_base";

    // Mensajes de error
    public static final String ERROR_FIELD_REQUIRED = "El campo '%s' es obligatorio.";
    public static final String ERROR_INVALID_EMAIL_FORMAT = "El formato del correo electrónico no es válido.";
    public static final String ERROR_SALARY_OUT_OF_RANGE = "El salario base debe estar entre 0 y 15,000,000.";
}
