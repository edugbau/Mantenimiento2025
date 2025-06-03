package es.taw.primerparcial.controller.UnitTest;

import es.taw.primerparcial.controller.LoginController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// No necesitamos Model mock aquí ya que getLoginPage() no lo usa.

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginControllerUnitTest {

    @Test
    @DisplayName("login() debería devolver el nombre de la vista 'login'")
    public void login_invocado_devuelveNombreVistaLogin() {
        LoginController controller = new LoginController();
        String viewName = controller.login();
        assertEquals("login", viewName);
    }
} 