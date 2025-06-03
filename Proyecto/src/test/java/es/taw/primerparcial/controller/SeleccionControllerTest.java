package es.taw.primerparcial.controller;

import es.taw.primerparcial.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeleccionController.class)
@Import(SecurityConfig.class) // Importar SecurityConfig para que se apliquen las reglas de acceso
public class SeleccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock UserDetailsService para satisfacer la dependencia en SecurityConfig.
    // No necesitamos su comportamiento para estas pruebas específicas gracias a @WithMockUser.
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void testGetSeleccionPage_unauthenticated() throws Exception {
        // Cuando un usuario no autenticado intenta acceder a /seleccion
        // Spring Security debería redirigirlo a la página de login.
        mockMvc.perform(get("/seleccion"))
                .andExpect(status().is3xxRedirection()) // Espera una redirección (e.g., 302 Found)
                .andExpect(redirectedUrlPattern("**/login")); // Espera que redirija a la URL de login
    }

    @Test
    @WithMockUser // Simula un usuario autenticado. Por defecto, usuario "user", contraseña "password", rol "USER"
    public void testGetSeleccionPage_authenticated() throws Exception {
        mockMvc.perform(get("/seleccion"))
                .andExpect(status().isOk())
                .andExpect(view().name("seleccion"));
    }
} 