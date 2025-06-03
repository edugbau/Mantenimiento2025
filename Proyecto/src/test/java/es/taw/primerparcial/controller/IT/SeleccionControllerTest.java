package es.taw.primerparcial.controller.IT;

import es.taw.primerparcial.config.SecurityConfig;
import es.taw.primerparcial.controller.SeleccionController;
import es.taw.primerparcial.dao.UsuarioRepository;
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
@Import(SecurityConfig.class)
public class SeleccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    // Añadir este mock para resolver la dependencia
    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void testGetSeleccionPage_unauthenticated() throws Exception {
        mockMvc.perform(get("/seleccion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    public void testGetSeleccionPage_authenticated() throws Exception {
        mockMvc.perform(get("/seleccion"))
                .andExpect(status().isOk())
                .andExpect(view().name("seleccion"));
    }
}