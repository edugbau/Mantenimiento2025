package es.taw.primerparcial.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import es.taw.primerparcial.config.SecurityConfig; // Importar SecurityConfig

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(LoginController.class)
@Import(SecurityConfig.class) // Importar SecurityConfig para que se apliquen las reglas de acceso
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetLoginPage() throws Exception {
        //Arrange
        // No se necesita "arrange" para esta prueba, ya que solo se verifica el acceso a la página de login.

        //Act
        mockMvc.perform(get("/login"))
        //Assert
                .andExpect(status().isOk()) // Esperamos un 200 OK ya que /login está permitido
                .andExpect(view().name("login"));
    }
} 