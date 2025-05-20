package com.uma.example.springuma.integration.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.model.Imagen;
import com.uma.example.springuma.model.Informe;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InformeControllerIT extends AbstractIntegration{
    @LocalServerPort
    private Integer port;

    private WebTestClient client;

    private Informe informe;
    private Imagen imagen;
    private Paciente paciente;
    private Medico medico;

    @BeforeEach
    public void init(){
        client = WebTestClient.bindToServer().baseUrl("http://localhost:"+port)
            .responseTimeout(Duration.ofMillis(30000)).build();

        informe = new Informe();;
        informe.setContenido("SE_NOS_MUERE");

        imagen = new Imagen();
        imagen.setNombre("imagen de la catapulta");
        imagen.setFecha(Calendar.getInstance());
        String ejemplo = "Imagen de setas para la catapulta";
        byte[] fileBytes = ejemplo.getBytes(StandardCharsets.UTF_8);
        imagen.setFile_content(fileBytes);

        Paciente paciente = new Paciente();
        paciente.setNombre("Zarcort");
        paciente.setEdad(100);
        paciente.setCita("mañana");
        paciente.setDni("123456789");

        Medico medico = new Medico();
        medico.setNombre("duende");
        medico.setDni("123");
        medico.setEspecialidad("catapultas");

        paciente.setMedico(medico);

        imagen.setPaciente(paciente);

        informe.setImagen(imagen);
    }

    @Test
    @DisplayName("Crear un informe y guardarlo correctamente")
    void createInformeYGuardarlo() throws Exception {
        client.post().uri("/informe")
                .body(Mono.just(informe), Informe.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult(); // para coger el resultado

        FluxExchangeResult<Informe> result = client.get().uri("/informe/1")
                .exchange()
                .expectStatus().isOk().returnResult(Informe.class);

        Informe informeObtenido = result.getResponseBody().blockFirst();

        assertEquals(informe,informeObtenido);
    }

    @Test
    @DisplayName("Crear un informe y eliminarlo correctamente")
    void createInformeYEliminarlo() throws Exception {
        client.post().uri("/informe")
                .body(Mono.just(informe), Informe.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        client.get().uri("/informe/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.contenido").isEqualTo(informe.getContenido());


        client.method(HttpMethod.DELETE).uri("/informe/1")
                .exchange()
                .expectStatus().isNoContent();

        client.get().uri("/informe/1")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("No se puede eliminar un cliente que no existe")
    void noSePuedeEliminarUnClienteQueNoExiste() throws Exception {
        client.method(HttpMethod.DELETE).uri("/informe/1")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Obtener todos los informes asociados a una imagen")
    void obtenerInformesDeUnaImagen() throws Exception {

        Informe informe2 = new Informe();
        informe2.setContenido("Todo en orden");
        informe2.setPrediccion("No se muere");
        informe2.setImagen(imagen); // Usar la misma imagen

        client.post().uri("/informe")
                .body(Mono.just(informe2), Informe.class)
                .exchange()
                .expectStatus().isCreated();


        // 5. Llamar al endpoint a probar: GET /informe/imagen/{id}
        FluxExchangeResult<Informe> result = client.get().uri("/informe/imagen/1")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Informe.class);

        List<Informe> informesObtenidos = result.getResponseBody().collectList().block();

        assertEquals(2, informesObtenidos.size());

        // Verificar que los informes contienen los valores esperados
        boolean contieneInforme1 = false;
        boolean contieneInforme2 = false;

        for (Informe informe : informesObtenidos) {
            if (informe.getContenido().equals("Contenido del primer informe") &&
                    informe.getContenido().equals(informe.getContenido())) {
                contieneInforme1 = true;
            }
            if (informe.getContenido().equals("Contenido del segundo informe") &&
                    informe.getContenido().equals(informe2.getContenido())) {
                contieneInforme2 = true;
            }
        }

        assertTrue(contieneInforme1);
        assertTrue(contieneInforme2);
    }
}
