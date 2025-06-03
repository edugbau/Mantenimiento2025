package es.taw.primerparcial.controller.IT;

import es.taw.primerparcial.config.SecurityConfig;
import es.taw.primerparcial.controller.AlbumController;
import es.taw.primerparcial.dao.AlbumRepository;
import es.taw.primerparcial.dao.ArtistaRepository;
import es.taw.primerparcial.dao.CancionRepository;
import es.taw.primerparcial.dao.GeneroRepository;
import es.taw.primerparcial.dto.AlbumRecopilatorio;
import es.taw.primerparcial.entity.Artista;
import es.taw.primerparcial.entity.Cancion;
import es.taw.primerparcial.entity.Genero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AlbumController.class)
@Import(SecurityConfig.class)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtistaRepository artistaRepository;

    @MockBean
    private CancionRepository cancionRepository;

    @MockBean
    private AlbumRepository albumRepository;

    @MockBean
    private GeneroRepository generoRepository;

    @MockBean
    private UserDetailsService userDetailsService; // Para SecurityConfig

    private List<Artista> artistaList;
    private List<Cancion> cancionList;
    private List<Genero> generoList;

    @BeforeEach
    void setUp() {
        artistaList = new ArrayList<>();
        Artista a1 = new Artista();
        a1.setArtistaId(1);
        a1.setArtistaName("Artista Test");
        artistaList.add(a1);

        cancionList = new ArrayList<>();
        Cancion c1 = new Cancion();
        c1.setCancionId(1);
        c1.setCancionName("Cancion Test");
        cancionList.add(c1);

        generoList = new ArrayList<>();
        Genero g1 = new Genero();
        g1.setGeneroId(1);
        g1.setGeneroName("Genero Test");
        generoList.add(g1);
    }

    @Test
    public void testGetApp2Home_unauthenticated() throws Exception {
        //Arrange
        // No se necesita arrange, se prueba el acceso no autenticado.

        //Act
        mockMvc.perform(get("/app2"))
        //Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    public void testGetApp2Home_authenticated() throws Exception {
        //Arrange
        when(artistaRepository.findAll()).thenReturn(artistaList);

        //Act
        mockMvc.perform(get("/app2"))
        //Assert
                .andExpect(status().isOk())
                .andExpect(view().name("app2/home.html"))
                .andExpect(model().attributeExists("artistas"))
                .andExpect(model().attribute("artistas", artistaList));
    }

    @Test
    public void testGetAddAlbum_unauthenticated() throws Exception {
        //Arrange
        // No se necesita arrange, se prueba el acceso no autenticado.

        //Act
        mockMvc.perform(get("/app2/addAlbum"))
        //Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    public void testGetAddAlbum_authenticated() throws Exception {
        //Arrange
        when(cancionRepository.findAll()).thenReturn(cancionList);
        when(generoRepository.findAll()).thenReturn(generoList);

        //Act
        mockMvc.perform(get("/app2/addAlbum"))
        //Assert
                .andExpect(status().isOk())
                .andExpect(view().name("app2/addAlbum.html"))
                .andExpect(model().attributeExists("albumRecopilatorio"))
                .andExpect(model().attribute("albumRecopilatorio", org.hamcrest.Matchers.instanceOf(AlbumRecopilatorio.class)))
                .andExpect(model().attributeExists("canciones"))
                .andExpect(model().attribute("canciones", cancionList))
                .andExpect(model().attributeExists("generos"))
                .andExpect(model().attribute("generos", generoList));
    }

    @Test
    @WithMockUser
    public void testSaveAlbum_success() throws Exception {
        //Arrange
        String albumName = "Nuevo Album Recopilatorio";
        Cancion cancionOriginal1 = new Cancion();
        cancionOriginal1.setCancionId(101);
        cancionOriginal1.setCancionName("Cancion Original 1");
        cancionOriginal1.setArtistaList(new ArrayList<>()); // Simular lista de artistas vacía o con datos

        Cancion cancionOriginal2 = new Cancion();
        cancionOriginal2.setCancionId(102);
        cancionOriginal2.setCancionName("Cancion Original 2");

        when(cancionRepository.findById(101)).thenReturn(Optional.of(cancionOriginal1));
        when(cancionRepository.findById(102)).thenReturn(Optional.of(cancionOriginal2));

        // Simular guardado de Artista, Album, Cancion
        when(artistaRepository.save(any(Artista.class))).thenAnswer(inv -> {
            Artista a = inv.getArgument(0);
            a.setArtistaId(50); // Simular ID asignado
            return a;
        });
        when(albumRepository.save(any(es.taw.primerparcial.entity.Album.class))).thenAnswer(inv -> {
            es.taw.primerparcial.entity.Album al = inv.getArgument(0);
            al.setAlbumId(60); // Simular ID asignado
            return al;
        });
        when(cancionRepository.save(any(Cancion.class))).thenAnswer(inv -> {
            Cancion c = inv.getArgument(0);
            if(c.getCancionId() == null) c.setCancionId(70); // Simular ID para nuevas canciones
            return c;
        });

        //Act
        mockMvc.perform(post("/app2/saveAlbum")
                .param("albumRecopilatorioName", albumName)
                .param("canciones[0]", "101") // IDs de las canciones a incluir
                .param("canciones[1]", "102")
                .with(csrf()))
        //Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app2/"));

        verify(artistaRepository).save(any(Artista.class)); // Se crea un nuevo artista para el álbum recopilatorio
        verify(albumRepository).save(any(es.taw.primerparcial.entity.Album.class));
        // Se guarda una nueva canción por cada una en el DTO, y potencialmente se actualizan artistas originales
        // El número exacto de llamadas a cancionRepository.save y artistaRepository.save puede variar
        // según la lógica de asociación de artistas de las canciones originales.
        // Por simplicidad, verificamos que se llamen al menos las veces esperadas para las nuevas canciones.
        verify(cancionRepository, org.mockito.Mockito.atLeast(2)).save(any(Cancion.class));
    }

    @Test
    @WithMockUser
    public void testFilter_withGenero() throws Exception {
        //Arrange
        Genero generoToFilterBy = generoList.get(0); // Genero con ID 1
        List<Cancion> filteredCanciones = new ArrayList<>(cancionList);

        // Esta es la parte crucial: cuando Spring intente convertir el parámetro "genero=1"
        // a un objeto Genero, usará el converter registrado. Si el converter por defecto
        // (o uno personalizado) usa findById, este mock lo cubrirá.
        when(generoRepository.findById(generoToFilterBy.getGeneroId())).thenReturn(Optional.of(generoToFilterBy));

        when(cancionRepository.findByGenero(generoToFilterBy)).thenReturn(filteredCanciones);
        when(generoRepository.findAll()).thenReturn(generoList); // Para rellenar el modelo

        //Act
        mockMvc.perform(post("/app2/filter")
                .param("genero", String.valueOf(generoToFilterBy.getGeneroId())) // Enviar el ID del género
                .with(csrf()))
        //Assert
                .andExpect(status().isOk())
                .andExpect(view().name("app2/addAlbum.html"))
                .andExpect(model().attributeExists("albumRecopilatorio"))
                .andExpect(model().attributeExists("canciones"))
                .andExpect(model().attribute("canciones", filteredCanciones))
                .andExpect(model().attributeExists("generos"))
                .andExpect(model().attribute("generos", generoList));
        
        verify(cancionRepository).findByGenero(generoToFilterBy);
    }

    @Test
    @WithMockUser
    public void testFilter_withoutGenero() throws Exception {
        //Arrange
        when(cancionRepository.findAll()).thenReturn(cancionList); // Devuelve todas las canciones si no hay filtro
        when(generoRepository.findAll()).thenReturn(generoList); // Para rellenar el modelo

        //Act
        mockMvc.perform(post("/app2/filter")
                // No se envía el parámetro "genero"
                .with(csrf()))
        //Assert
                .andExpect(status().isOk())
                .andExpect(view().name("app2/addAlbum.html"))
                .andExpect(model().attributeExists("albumRecopilatorio"))
                .andExpect(model().attributeExists("canciones"))
                .andExpect(model().attribute("canciones", cancionList)) // Espera todas las canciones
                .andExpect(model().attributeExists("generos"))
                .andExpect(model().attribute("generos", generoList));
        
        verify(cancionRepository).findAll(); // Verifica que se llamo a findAll ya que no hubo filtro por genero
    }
} 