package com.uma.example.springuma.integration.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PacienteControllerIT extends AbstractIntegration{

    private Paciente paciente;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        paciente = new Paciente();
        paciente.setNombre("Kinder Malo");
        paciente.setEdad(17);
        paciente.setDni("11111111A");

        Medico medico = new Medico();
        medico.setNombre("Pimp flaco");
        medico.setDni("22222222B");

        paciente.setMedico(medico);
    }

    @Test
    @DisplayName("Crear un paciente y recibirlo correctamente")
    public void crearPacienteYRecibirloCorrectamente() throws Exception {

        //crear
        this.mockMvc.perform(post("/paciente").
                contentType("application/json").
                content(objectMapper.writeValueAsString(paciente))).
                andExpect(status().isCreated()).
                andExpect(status().is2xxSuccessful());

        //obtener
        this.mockMvc.perform(get("/paciente/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").value(paciente));

        //actualizar
        paciente.setNombre("Kinder Bueno");

        this.mockMvc.perform(post("/paciente").
                        contentType("application/json").
                        content(objectMapper.writeValueAsString(paciente))).
                andExpect(status().isCreated()).
                andExpect(status().is2xxSuccessful());

        //comprobar los cambios
        this.mockMvc.perform(get("/paciente/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].nombre").value("Kinder Bueno"));
    }
    // TRABAJA EDUARDO

}
