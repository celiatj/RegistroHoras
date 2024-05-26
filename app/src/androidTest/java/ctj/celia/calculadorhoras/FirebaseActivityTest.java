package ctj.celia.calculadorhoras;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FirebaseActivityTest {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testIsNetworkAvailable() {
        // Arrange
        boolean expected = true;

        // Act
        boolean actual = FirebaseActivity.NetworkUtils.isNetworkAvailable(context);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testValidarCorreo_ValidEmail() {
        // Arrange
        String email = "test@example.com";
        boolean expected = true;

        // Act
        boolean actual = FirebaseActivity.validarCorreo(email);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testValidarCorreo_InvalidEmail() {
        // Arrange
        String email = "invalid_email";
        boolean expected = false;

        // Act
        boolean actual = FirebaseActivity.validarCorreo(email);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testValidarLongitudContraseña_LongitudMinima() {
        // Arrange
        String contraseña = "123456"; // Contraseña con longitud mínima
        boolean expected = true;

        // Act
        boolean actual = FirebaseActivity.validarContraseña(contraseña);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testValidarLongitudContraseña_LongitudInsuficiente() {
        // Arrange
        String contraseña = "12345"; // Contraseña con longitud insuficiente (menor que 6)
        boolean expected = false;

        // Act
        boolean actual = FirebaseActivity.validarContraseña(contraseña);

        // Assert
        Assert.assertEquals(expected, actual);
    }
}