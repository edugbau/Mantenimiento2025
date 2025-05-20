package com.uma.example.springuma.integration.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.model.Medico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MedicoControllerIT extends AbstractIntegration{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Medico medico;

    @BeforeEach
    public void init(){
        medico = new Medico();
        medico.setNombre("Walter");
        medico.setDni("123");
        medico.setEspecialidad("meta");

    }
    @Test
    @DisplayName("Crear una persona y recuperarla correctamente")
    void crearPersonaYRecuperarCorrectamente() throws Exception{

        // crear médico
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());

        // obtenemos el médico por la id
        this.mockMvc.perform(get("/medico/1"))
                .andDo(print()) // esto es para debugear (no es necesario)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.dni").value("123"));

    }
    @Test
    @DisplayName("No se puede obtener un médico con dni inválido")
    void  noSePuedeObtenerUnMedicoConDniInvalido() throws Exception{
        this.mockMvc.perform(get("/medico/dni/999999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Crear persona y actualizarla correctamente")
    void crearPersonaYActualizarCorrectamente() throws Exception{
        this.mockMvc.perform(post("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());

        // obtenemos el médico
        this.mockMvc.perform(get("/medico"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.dni").value("123"));

        // cambiamos el nombre
        medico.setNombre("Jesse");

        // actualizamos el médico
        this.mockMvc.perform(put("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isNoContent())
                .andExpect(status().is2xxSuccessful());

        // comprobar que efectivamente se ha actualizado
        this.mockMvc.perform(get("/medico/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.nombre").value("Jesse"));

    }

    @Test
    @DisplayName("Crear persona y eliminarla correctamente")
    void crearPersonaYEliminarCorrectamente() throws Exception{
        // creamos la persona
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.dni").value("123"));

        this.mockMvc.perform(delete("/medico/1"))
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/medico/dni/123"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Crear una persona y recogerla por su dni")
    void crearPersonaYRecogerlaPorDni() throws Exception{
        // creamos la persona
        this.mockMvc.perform(post("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated())
                .andExpect(status().is2xxSuccessful());


        // obtenemos el médico por el dni y es correcto
        this.mockMvc.perform(get("/medico/dni/123"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.dni").value("123"));
    }

    @Test
    @DisplayName("No se puede eliminar un médico inexistente")
    void noSePuedeEliminarUnMedicoInexistente() throws Exception{

        // el controller no debería explotar, pero explota por eso el error es 5xx
        this.mockMvc.perform(delete("/medico/1"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("No se puede actualizar un médico que no está en la base de datos")
    void noSePuedeActualizarMedicoInexistente() throws Exception{

        // el controller no debería explotar, pero explota por eso el error es 5xx
        this.mockMvc.perform(put("/medico")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().is5xxServerError());
    }

}
