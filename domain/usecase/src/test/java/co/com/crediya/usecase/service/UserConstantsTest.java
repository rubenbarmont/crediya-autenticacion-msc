package co.com.crediya.usecase.service;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserConstantsTest {

    @Test
    void testPrivateConstructor() {
        // Arrange
        assertThrows(InvocationTargetException.class, () -> {
            Constructor<UserConstants> constructor = UserConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            // Act
            constructor.newInstance();
            // Assert: se espera que lance InvocationTargetException, causada por IllegalStateException
        }, "El constructor de la clase de utilidad no debería ser instanciable.");
    }
}
