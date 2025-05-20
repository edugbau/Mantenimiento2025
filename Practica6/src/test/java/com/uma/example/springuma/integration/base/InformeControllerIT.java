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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private Paciente paciente;
    private Medico medico;
    private byte[] healthyImageBytes;

    @BeforeEach
    public void init() throws IOException {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:"+port)
            .responseTimeout(Duration.ofMillis(30000)).build();

        Medico medico = new Medico();
        medico.setNombre("duende");
        medico.setDni("123");
        medico.setEspecialidad("catapultas");

        client.post().uri("/medico")
                .body(Mono.just(medico), Medico.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        medico.setId(1);

        Paciente paciente = new Paciente();
        paciente.setNombre("Zarcort");
        paciente.setEdad(100);
        paciente.setCita("mañana");
        paciente.setDni("123456789");
        paciente.setMedico(medico);
        client.post().uri("/paciente")
                .body(Mono.just(paciente), Paciente.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        paciente.setId(1);

        // imagen = new Imagen();
        // imagen.setNombre("imagen de la catapulta");
        // imagen.setFecha(Calendar.getInstance());
        // String ejemplo = "Imagen de setas para la catapulta";
        // byte[] fileBytes = ejemplo.getBytes(StandardCharsets.UTF_8);
        // imagen.setPaciente(paciente);

        healthyImageBytes = Files.readAllBytes(new ClassPathResource("healthy.png").getFile().toPath());
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        bodyBuilder.part("image", healthyImageBytes)
                .header("Content-Disposition", "form-data; name=image; filename=test.png")
                .contentType(MediaType.IMAGE_PNG);

        bodyBuilder.part("paciente", paciente)
                .contentType(MediaType.APPLICATION_JSON);

        client.post().uri("/imagen")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk();

        FluxExchangeResult<Imagen> result = client.get().uri("/imagen/info/1")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Imagen.class);

        Imagen imagenGuardada = result.getResponseBody().blockFirst();
        informe = new Informe();;
        informe.setContenido("SE_NOS_MUERE");
        informe.setPrediccion("Predicción de prueba");
        informe.setImagen(imagenGuardada);
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

        assertEquals(informe.getContenido(), informeObtenido.getContenido());
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

        FluxExchangeResult<Informe> result = client.get().uri("/informe/imagen/1")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Informe.class);

        List<Informe> informesObtenidos = result.getResponseBody().collectList().block();

        // comprobamos que no haya informes
        assertEquals(0, informesObtenidos.size());
    }


    @Test
    @DisplayName("Obtener todos los informes asociados a una imagen")
    void obtenerInformesDeUnaImagen() throws Exception {
        client.post().uri("/informe")
                .body(Mono.just(informe), Informe.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        FluxExchangeResult<Informe> result = client.get().uri("/informe/imagen/1")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Informe.class);

        List<Informe> informesObtenidos = result.getResponseBody().collectList().block();

        assertEquals(1, informesObtenidos.size());
    }
}
