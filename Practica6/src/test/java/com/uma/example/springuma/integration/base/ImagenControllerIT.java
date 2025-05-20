package com.uma.example.springuma.integration.base;

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
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ImagenControllerIT {
    @LocalServerPort
    private Integer port;

    private WebTestClient client;
    private Paciente paciente;
    private Medico medico;
    private byte[] healthyImageBytes;
    private byte[] notHealthyImageBytes;

    @BeforeEach
    public void init() throws IOException {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:"+port)
                .responseTimeout(Duration.ofMillis(30000)).build();

        medico = new Medico();
        medico.setNombre("El tio del pongo");
        medico.setDni("123");
        medico.setEspecialidad("Catapultas");

        client.post().uri("/medico")
                .body(Mono.just(medico), Medico.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        medico.setId(1);
        paciente = new Paciente();
        paciente.setNombre("Eduardo enfermo");
        paciente.setEdad(20);
        paciente.setCita("YA YA YA");
        paciente.setDni("456");
        paciente.setMedico(this.medico);

        // Cargar las imágenes de test desde resources
        healthyImageBytes = Files.readAllBytes(new ClassPathResource("healthy.png").getFile().toPath());
        notHealthyImageBytes = Files.readAllBytes(new ClassPathResource("no_healthty.png").getFile().toPath());

        client.post().uri("/paciente")
                .body(Mono.just(paciente), Paciente.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();

        paciente.setId(1);
    }

    @Test
    @DisplayName("Subir una imagen y verificar que se guarda correctamente")
    void subirImagenYVerificarGuardado() {
        // tenemos que crear un bodybuilder para enviar las dos partes que pide el metodo
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        // el metodo pide una imagen
        bodyBuilder.part("image", healthyImageBytes)
                .header("Content-Disposition", "form-data; name=image; filename=test.png")
                .contentType(MediaType.IMAGE_PNG);

        // y un paciente
        bodyBuilder.part("paciente", paciente)
                .contentType(MediaType.APPLICATION_JSON);

        // Subimos la imagen
        client.post().uri("/imagen")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk();

        // Verificar que la imagen se ha guardado correctamente obteniendo su información
        FluxExchangeResult<Imagen> result = client.get().uri("/imagen/info/1")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Imagen.class);

        Imagen imagenGuardada = result.getResponseBody().blockFirst();

        assertNotNull(imagenGuardada);
        assertNotNull(imagenGuardada.getPaciente());
        assertEquals(paciente.getNombre(), imagenGuardada.getPaciente().getNombre());
    }

    @Test
    @DisplayName("Obtener una imagen por ID")
    void obtenerImagenPorID() {
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

        // Obtenemos la imagen por su ID
        byte[] imagenDescargada = client.get().uri("/imagen/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .returnResult().getResponseBody();

        assertTrue(imagenDescargada.length > 0);
    }

    @Test
    @DisplayName("Obtener predicción de una imagen")
    void obtenerPrediccionDeImagen() {
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

        // Obtenemos la predicción (recordemos que ahora va a ser un valor aleatorio)
        String prediccion = client.get().uri("/imagen/predict/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        assertFalse(prediccion.isEmpty());
    }

    @Test
    @DisplayName("Obtener imágenes por paciente")
    void obtenerImagenesPorPaciente() {
        // Vamos a subir dos imagenes del mismo paciente una healthy y la otra not healthy
        MultipartBodyBuilder bodyBuilder1 = new MultipartBodyBuilder();
        bodyBuilder1.part("image", healthyImageBytes)
                .header("Content-Disposition", "form-data; name=image; filename=test1.png")
                .contentType(MediaType.IMAGE_PNG);
        bodyBuilder1.part("paciente", paciente)
                .contentType(MediaType.APPLICATION_JSON);

        client.post().uri("/imagen")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder1.build()))
                .exchange()
                .expectStatus().isOk();

        MultipartBodyBuilder bodyBuilder2 = new MultipartBodyBuilder();
        bodyBuilder2.part("image", notHealthyImageBytes)
                .header("Content-Disposition", "form-data; name=image; filename=test2.png")
                .contentType(MediaType.IMAGE_PNG);
        bodyBuilder2.part("paciente", paciente)
                .contentType(MediaType.APPLICATION_JSON);

        client.post().uri("/imagen")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder2.build()))
                .exchange()
                .expectStatus().isOk();

        // Obtenemos las imágenes del paciente (asumimos que el ID del paciente es 1)
        FluxExchangeResult<Imagen> result = client.get().uri("/imagen/paciente/1")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Imagen.class);

        List<Imagen> imagenes = result.getResponseBody().collectList().block();

        assertEquals(2, imagenes.size());
    }
    @Test
    @DisplayName("Eliminar una imagen")
    void eliminarImagen() {
        // Primero subimos una imagen
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

        // Verificamos que la imagen existe
        client.get().uri("/imagen/info/1")
                .exchange()
                .expectStatus().isOk();

        // Eliminamos la imagen
        client.delete().uri("/imagen/1")
                .exchange()
                .expectStatus().isNoContent();

        // Verificamos que la imagen ya no existe
        client.get().uri("/imagen/info/1")
                .exchange()
                .expectStatus().is5xxServerError();
    }

}
