package es.taw.primerparcial.controller.UnitTest;

import es.taw.primerparcial.controller.AlbumController;
import es.taw.primerparcial.dao.AlbumRepository;
import es.taw.primerparcial.dao.ArtistaRepository;
import es.taw.primerparcial.dao.CancionRepository;
import es.taw.primerparcial.dao.GeneroRepository;
import es.taw.primerparcial.dto.AlbumRecopilatorio;
import es.taw.primerparcial.entity.Artista;
import es.taw.primerparcial.entity.Cancion;
import es.taw.primerparcial.entity.Genero;
import es.taw.primerparcial.entity.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerUnitTest {

    @Mock
    private ArtistaRepository artistaRepository;
    @Mock
    private CancionRepository cancionRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private GeneroRepository generoRepository;
    @Mock
    private Model model;

    @InjectMocks
    private AlbumController albumController;

    private List<Artista> artistaList;
    private List<Cancion> cancionList;
    private List<Genero> generoList;
    private Genero testGenero;

    @BeforeEach
    void setUp() {
        artistaList = new ArrayList<>();
        Artista a1 = new Artista();
        a1.setArtistaId(1);
        a1.setArtistaName("Artista Test U");
        artistaList.add(a1);

        cancionList = new ArrayList<>();
        Cancion c1 = new Cancion();
        c1.setCancionId(1);
        c1.setCancionName("Cancion Test U");
        cancionList.add(c1);

        generoList = new ArrayList<>();
        testGenero = new Genero(); // Usaremos este para filtrar
        testGenero.setGeneroId(1);
        testGenero.setGeneroName("Genero Test U");
        generoList.add(testGenero);
    }

    @Test
    @DisplayName("doInit() debería devolver vista 'app2/home.html' y atributos del modelo")
    void doInit_invocado_devuelveVistaYAtributosModelo() {
        when(artistaRepository.findAll()).thenReturn(artistaList);
        String viewName = albumController.doInit(model);
        assertEquals("app2/home.html", viewName);
        verify(artistaRepository).findAll();
        verify(model).addAttribute("artistas", artistaList);
    }

    @Test
    @DisplayName("doAddAlbum() debería devolver vista 'app2/addAlbum.html' y atributos del modelo")
    void doAddAlbum_invocado_devuelveVistaYAtributosModelo() {
        when(cancionRepository.findAll()).thenReturn(cancionList);
        when(generoRepository.findAll()).thenReturn(generoList);
        String viewName = albumController.doAddAlbum(model);
        assertEquals("app2/addAlbum.html", viewName);
        verify(cancionRepository).findAll();
        verify(generoRepository).findAll();
        verify(model).addAttribute(eq("albumRecopilatorio"), any(AlbumRecopilatorio.class));
        verify(model).addAttribute("canciones", cancionList);
        verify(model).addAttribute("generos", generoList);
    }

    @Test
    @DisplayName("doSaveAlbum() con canciones debería redirigir a '/app2/' y guardar entidades")
    void doSaveAlbum_conCanciones_redirigeYGuardaEntidades() {
        AlbumRecopilatorio dto = new AlbumRecopilatorio();
        dto.setAlbumRecopilatorioName("Super Hits U");
        Cancion cancionOriginal1 = new Cancion();
        cancionOriginal1.setCancionId(101);
        cancionOriginal1.setCancionName("Original Song 1");
        Artista artistaOriginalCancion = new Artista();
        artistaOriginalCancion.setArtistaId(505);
        artistaOriginalCancion.setArtistaName("Artista Original de Cancion");
        artistaOriginalCancion.setCancionList(new ArrayList<>());
        Album albumOriginalCancion = new Album();
        albumOriginalCancion.setAlbumId(606);
        albumOriginalCancion.setArtistaId(artistaOriginalCancion);
        cancionOriginal1.setAlbumId(albumOriginalCancion);
        cancionOriginal1.setArtistaList(List.of(artistaOriginalCancion));
        dto.setCanciones(List.of(cancionOriginal1.getCancionId()));
        when(cancionRepository.findById(cancionOriginal1.getCancionId())).thenReturn(Optional.of(cancionOriginal1));
        ArgumentCaptor<Artista> artistaCaptor = ArgumentCaptor.forClass(Artista.class);
        when(artistaRepository.save(artistaCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));
        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
        when(albumRepository.save(albumCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));
        ArgumentCaptor<Cancion> cancionCaptor = ArgumentCaptor.forClass(Cancion.class);
        when(cancionRepository.save(cancionCaptor.capture())).thenAnswer(inv -> inv.getArgument(0)); 
        String viewName = albumController.doSaveAlbum(dto, model);
        assertEquals("redirect:/app2/", viewName);
        verify(artistaRepository).save(argThat(a -> a.getArtistaName().equals("Super Hits U")));
        verify(albumRepository).save(argThat(a -> a.getAlbumName().equals("Super Hits U") && a.getArtistaId().getArtistaName().equals("Super Hits U")));
        verify(cancionRepository).save(argThat(c -> c.getCancionName().startsWith(cancionOriginal1.getCancionName() + "(Super Hits U)")));
        verify(artistaRepository, times(3)).save(any(Artista.class));
        List<Artista> savedArtistas = artistaCaptor.getAllValues();
        assertEquals("Super Hits U", savedArtistas.get(0).getArtistaName());
        List<Cancion> savedCanciones = cancionCaptor.getAllValues();
        assertEquals(1, savedCanciones.size());
        Cancion nuevaCancionGuardada = savedCanciones.get(0);
        assertEquals(cancionOriginal1.getCancionName() + "(Super Hits U)", nuevaCancionGuardada.getCancionName());
        assertNotNull(nuevaCancionGuardada.getAlbumId());
        assertEquals("Super Hits U", nuevaCancionGuardada.getAlbumId().getAlbumName());
        assertTrue(artistaOriginalCancion.getCancionList().stream().anyMatch(c -> c.getCancionName().equals(nuevaCancionGuardada.getCancionName())));
    }

    @Test
    @DisplayName("doSaveAlbum() sin canciones debería redirigir y guardar solo álbum y artista")
    void doSaveAlbum_sinCanciones_redirigeYGuardaAlbumArtista() {
        AlbumRecopilatorio dto = new AlbumRecopilatorio();
        dto.setAlbumRecopilatorioName("Empty Album U");
        dto.setCanciones(null);
        when(artistaRepository.save(any(Artista.class))).thenAnswer(inv -> inv.getArgument(0));
        when(albumRepository.save(any(Album.class))).thenAnswer(inv -> inv.getArgument(0));
        String viewName = albumController.doSaveAlbum(dto, model);
        assertEquals("redirect:/app2/", viewName);
        verify(artistaRepository).save(argThat(a -> a.getArtistaName().equals("Empty Album U")));
        verify(albumRepository).save(argThat(a -> a.getAlbumName().equals("Empty Album U")));
        verify(cancionRepository, never()).findById(anyInt());
        verify(cancionRepository, never()).save(any(Cancion.class));
    }

    @Test
    @DisplayName("doFilter() con género debería devolver vista 'app2/addAlbum.html' y canciones filtradas")
    void doFilter_conGenero_devuelveVistaYCancionesFiltradas() {
        List<Cancion> filteredCanciones = new ArrayList<>(cancionList);
        when(cancionRepository.findByGenero(testGenero)).thenReturn(filteredCanciones);
        when(generoRepository.findAll()).thenReturn(generoList);
        String viewName = albumController.doFilter(testGenero, model);
        assertEquals("app2/addAlbum.html", viewName);
        verify(cancionRepository).findByGenero(testGenero);
        verify(generoRepository).findAll();
        verify(model).addAttribute(eq("albumRecopilatorio"), any(AlbumRecopilatorio.class));
        verify(model).addAttribute("canciones", filteredCanciones);
        verify(model).addAttribute("generos", generoList);
    }

    @Test
    @DisplayName("doFilter() sin género debería redirigir a '/app2/addAlbum'")
    void doFilter_sinGenero_redirigeAAddAlbum() {
        String viewName = albumController.doFilter(null, model);
        assertEquals("redirect:/app2/addAlbum", viewName);
        verify(cancionRepository, never()).findByGenero(any(Genero.class));
        verify(generoRepository, never()).findAll();
        verify(model, never()).addAttribute(anyString(), any());
    }
} 