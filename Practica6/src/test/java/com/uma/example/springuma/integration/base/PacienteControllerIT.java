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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        paciente.setId(1); //No se haría en el sistema real, lo hacemos en las pruebas para evitar complicaciones


        Medico medico = new Medico();
        medico.setNombre("Pimp flaco");
        medico.setDni("22222222B");
        medico.setEspecialidad("amor");

        // Guardar el médico vía API
        this.mockMvc.perform(post("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());

        // Recuperar el médico guardado para obtener su ID
        String medicoJson = this.mockMvc.perform(get("/medico/dni/22222222B"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        medico = objectMapper.readValue(medicoJson, Medico.class);


        paciente.setMedico(medico);
    }

    @Test
    @DisplayName("Crear un paciente y modificarlo correctamente")
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
                .andExpect(jsonPath("$.nombre").value("Kinder Malo"));

        //actualizar
        paciente.setNombre("Kinder Bueno");

        this.mockMvc.perform(put("/paciente").
                        contentType("application/json").
                        content(objectMapper.writeValueAsString(paciente))).
                andExpect(status().isNoContent()).
                andExpect(status().is2xxSuccessful());

        //comprobar los cambios
        this.mockMvc.perform(get("/paciente/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.nombre").value("Kinder Bueno"));
    }

    @Test
    @DisplayName("Crear y eliminar un paciente")
    public void testCrearYEliminarPaciente() throws Exception {
        // Crear paciente
        this.mockMvc.perform(post("/paciente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated());

        // Obtener paciente creado
        this.mockMvc.perform(get("/paciente/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.nombre").value("Kinder Malo"));

        // Eliminar paciente
        this.mockMvc.perform(delete("/paciente/1"))
                .andExpect(status().is2xxSuccessful());

        // Comprobar que ya no existe
        this.mockMvc.perform(get("/paciente/1"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("No se puede obtener un paciente con DNI inexistente")
    public void testObtenerPacientePorDniInexistente() throws Exception {
        this.mockMvc.perform(get("/paciente/dni/99999999Z"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("No se puede eliminar un paciente que no existe")
    public void testEliminarPacienteInexistente() throws Exception {
        this.mockMvc.perform(delete("/paciente/999"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Obtener lista de pacientes de un médico y comprobar el primero")
    public void testObtenerPacientesDeMedicoYComprobarPrimero() throws Exception {
        // Crear paciente
        this.mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated());

        // Obtener lista de pacientes del médico
        this.mockMvc.perform(get("/paciente/medico/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].nombre").value("Kinder Malo"))
                .andExpect(jsonPath("$[0].dni").value("11111111A"));
    }
}
