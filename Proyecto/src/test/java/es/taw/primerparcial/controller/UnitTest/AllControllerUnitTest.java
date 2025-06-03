package es.taw.primerparcial.controller.UnitTest;

import es.taw.primerparcial.controller.AllController;
import es.taw.primerparcial.dao.CancionRepository;
import es.taw.primerparcial.dao.PlaylistCancionRepository;
import es.taw.primerparcial.dao.PlaylistRepository;
import es.taw.primerparcial.dao.UsuarioRepository;
import es.taw.primerparcial.dto.AddSongsObject;
import es.taw.primerparcial.dto.CreatePlaylistObject;
import es.taw.primerparcial.entity.Cancion;
import es.taw.primerparcial.entity.PlayList;
import es.taw.primerparcial.entity.PlayListCancion;
import es.taw.primerparcial.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class) // Para inicializar mocks y @InjectMocks
public class AllControllerUnitTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private CancionRepository cancionRepository;
    @Mock
    private PlaylistCancionRepository playlistCancionRepository;
    @Mock
    private Model model; // Mockeamos la interfaz Model de Spring

    @InjectMocks
    private AllController allController; // Se inyectan los mocks en esta instancia

    private List<Usuario> usuarioList;
    private PlayList testPlaylist;
    private List<Cancion> songsNotInPlaylist;

    @BeforeEach
    void setUp() {
        usuarioList = new ArrayList<>();
        Usuario user1 = new Usuario();
        user1.setUsuarioId(1);
        user1.setUsuarioName("TestUser1");
        usuarioList.add(user1);

        testPlaylist = new PlayList();
        testPlaylist.setPlayListId(1);
        testPlaylist.setPlayListName("Test Playlist");
        testPlaylist.setUsuarioId(user1);
        testPlaylist.setPlayListCancionList(new ArrayList<>());

        songsNotInPlaylist = new ArrayList<>();
        Cancion cancion1 = new Cancion();
        cancion1.setCancionId(100);
        cancion1.setCancionName("Cancion Fuera 1");
        songsNotInPlaylist.add(cancion1);
    }

    @Test
    @DisplayName("index() debería devolver vista 'app1/index.html' y añadir atributos al modelo")
    void index_invocado_devuelveVistaYAtributosModelo() {
        //Arrange
        when(usuarioRepository.findAll()).thenReturn(usuarioList);
        
        //Act
        String viewName = allController.index(model);
        
        //Assert
        assertEquals("app1/index.html", viewName);
        verify(usuarioRepository).findAll();
        verify(model).addAttribute("usuarios", usuarioList);
        verify(model).addAttribute(eq("dto"), any(CreatePlaylistObject.class));
    }

    @Test
    @DisplayName("createPlaylist() con usuario válido debería redirigir a viewPlaylist")
    void createPlaylist_usuarioValido_redirigeAViewPlaylist() {
        //Arrange
        CreatePlaylistObject dto = new CreatePlaylistObject();
        dto.setNombre("Mi Nueva Playlist U");
        dto.setUsuarioId(1);
        Usuario testUser = usuarioList.get(0);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUser));
        ArgumentCaptor<PlayList> playListArgumentCaptor = ArgumentCaptor.forClass(PlayList.class);
        when(playlistRepository.save(playListArgumentCaptor.capture())).thenAnswer(invocation -> {
            PlayList p = invocation.getArgument(0);
            p.setPlayListId(123);
            return p;
        });
        
        //Act
        String viewName = allController.createPlaylist(dto, model);
        
        //Assert
        assertEquals("redirect:/app1/viewPlaylist?playlistId=123", viewName);
        verify(usuarioRepository).findById(1);
        verify(playlistRepository).save(any(PlayList.class));
        PlayList savedPlaylist = playListArgumentCaptor.getValue();
        assertEquals("Mi Nueva Playlist U", savedPlaylist.getPlayListName());
        assertEquals(testUser, savedPlaylist.getUsuarioId());
        long timeDiff = new Date().getTime() - savedPlaylist.getDateCreation().getTime();
        assertTrue(timeDiff < 1000, "La fecha de creación debería ser reciente.");
    }

    @Test
    @DisplayName("createPlaylist() con usuario inexistente debería devolver vista 'app1/index.html' con error")
    void createPlaylist_usuarioNoEncontrado_devuelveVistaIndexConError() {
        //Arrange
        CreatePlaylistObject dto = new CreatePlaylistObject();
        dto.setNombre("Playlist Fallida U");
        dto.setUsuarioId(999);
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());
        when(usuarioRepository.findAll()).thenReturn(usuarioList);
        
        //Act
        String viewName = allController.createPlaylist(dto, model);
        
        //Assert
        assertEquals("app1/index.html", viewName);
        verify(usuarioRepository).findById(999);
        verify(model).addAttribute("error", "Usuario no encontrado.");
        verify(model).addAttribute("usuarios", usuarioList);
        verify(model).addAttribute("dto", dto);
        verify(playlistRepository, never()).save(any(PlayList.class));
    }

    @Test
    @DisplayName("viewPlaylist() con playlist existente debería devolver vista 'app1/viewPlaylist.html' y atributos")
    void viewPlaylist_playlistExiste_devuelveVistaYAtributosModelo() {
        //Arrange
        Integer playlistId = testPlaylist.getPlayListId();
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(testPlaylist));
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);
        
        //Act
        String viewName = allController.viewPlaylist(playlistId, model);
        
        //Assert
        assertEquals("app1/viewPlaylist.html", viewName);
        verify(playlistRepository).findById(playlistId);
        verify(cancionRepository).findSongsNotInPlaylist(testPlaylist);
        verify(model).addAttribute("playlist", testPlaylist);
        verify(model).addAttribute("songsNotInPlaylist", songsNotInPlaylist);
        verify(model).addAttribute(eq("dto"), any(AddSongsObject.class));
    }

    @Test
    @DisplayName("viewPlaylist() con playlist inexistente debería redirigir a '/app1/'")
    void viewPlaylist_playlistNoEncontrada_redirigeARaizApp1() {
        //Arrange
        Integer playlistId = 999;
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.empty());
        
        //Act
        String viewName = allController.viewPlaylist(playlistId, model);
        
        //Assert
        assertEquals("redirect:/app1/", viewName);
        verify(playlistRepository).findById(playlistId);
        verify(cancionRepository, never()).findSongsNotInPlaylist(any(PlayList.class));
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("addSongs() con playlist y canciones válidas debería redirigir a viewPlaylist")
    void addSongs_playlistYCancionesValidas_redirigeAViewPlaylist() {
        //Arrange
        Integer playlistId = testPlaylist.getPlayListId();
        AddSongsObject dto = new AddSongsObject();
        List<Integer> cancionesIds = new ArrayList<>();
        Cancion cancionToAdd1 = songsNotInPlaylist.get(0);
        cancionesIds.add(cancionToAdd1.getCancionId());
        dto.setCancionesIds(cancionesIds);
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(testPlaylist));
        when(cancionRepository.findById(cancionToAdd1.getCancionId())).thenReturn(Optional.of(cancionToAdd1));
        when(playlistCancionRepository.save(any(PlayListCancion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cancionRepository.save(any(Cancion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(playlistRepository.save(any(PlayList.class))).thenAnswer(inv -> inv.getArgument(0));
        
        //Act
        String viewName = allController.addSongs(dto, playlistId, model);
        
        //Assert
        assertEquals("redirect:/app1/viewPlaylist?playlistId=" + playlistId, viewName);
        verify(playlistRepository).findById(playlistId);
        verify(cancionRepository).findById(cancionToAdd1.getCancionId());
        ArgumentCaptor<PlayListCancion> plcCaptor = ArgumentCaptor.forClass(PlayListCancion.class);
        verify(playlistCancionRepository).save(plcCaptor.capture());
        assertEquals(cancionToAdd1, plcCaptor.getValue().getCancionId());
        assertEquals(testPlaylist, plcCaptor.getValue().getPlayListId());
        verify(cancionRepository).save(eq(cancionToAdd1));
        verify(playlistRepository, times(1)).save(eq(testPlaylist));
        assertTrue(testPlaylist.getPlayListCancionList().contains(plcCaptor.getValue()));
        assertTrue(cancionToAdd1.getPlayListCancionList().contains(plcCaptor.getValue()));
    }

    @Test
    @DisplayName("addSongs() con playlist inexistente debería redirigir a '/app1/'")
    void addSongs_playlistNoEncontrada_redirigeARaizApp1() {
        //Arrange
        Integer playlistId = 999;
        AddSongsObject dto = new AddSongsObject();
        dto.setCancionesIds(List.of(100));
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.empty());
        
        //Act
        String viewName = allController.addSongs(dto, playlistId, model);
        
        //Assert
        assertEquals("redirect:/app1/", viewName);
        verify(playlistRepository).findById(playlistId);
        verify(cancionRepository, never()).findById(anyInt());
        verify(playlistCancionRepository, never()).save(any(PlayListCancion.class));
    }

    @Test
    @DisplayName("addSongs() con canción inexistente debería redirigir a viewPlaylist y no guardar la canción")
    void addSongs_cancionNoEncontrada_redirigeAViewPlaylistYNoGuarda() {
        //Arrange
        Integer playlistId = testPlaylist.getPlayListId();
        AddSongsObject dto = new AddSongsObject();
        Integer nonExistentCancionId = 999;
        dto.setCancionesIds(List.of(nonExistentCancionId));
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(testPlaylist));
        when(cancionRepository.findById(nonExistentCancionId)).thenReturn(Optional.empty());
        // Necesario para la recarga de la vista en caso de error
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);
        
        //Act
        String viewName = allController.addSongs(dto, playlistId, model);
        
        //Assert
        assertEquals("app1/viewPlaylist.html", viewName); // Se queda en la misma página para mostrar error
        verify(playlistRepository).findById(playlistId);
        verify(cancionRepository).findById(nonExistentCancionId);
        verify(model).addAttribute("error", "Algunas canciones no se encontraron y no pudieron ser añadidas.");
        verify(playlistCancionRepository, never()).save(any(PlayListCancion.class));
        // Verifica que el modelo se recarga correctamente para la vista
        verify(model).addAttribute("playlist", testPlaylist);
        verify(model).addAttribute("songsNotInPlaylist", songsNotInPlaylist);
        verify(model).addAttribute(eq("dto"), any(AddSongsObject.class));
    }

    @Test
    @DisplayName("addSongs() con lista de canciones vacía debería redirigir a viewPlaylist y no guardar")
    void addSongs_listaCancionesVacia_redirigeAViewPlaylistYNoGuarda() {
        //Arrange
        Integer playlistId = testPlaylist.getPlayListId();
        AddSongsObject dto = new AddSongsObject();
        dto.setCancionesIds(new ArrayList<>()); // Lista vacía
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(testPlaylist));
        // Necesario para la recarga de la vista en caso de error o no acción
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);
        
        //Act
        String viewName = allController.addSongs(dto, playlistId, model);
        
        //Assert
        assertEquals("app1/viewPlaylist.html", viewName); // Se queda en la misma página
        verify(playlistRepository).findById(playlistId);
        verify(model).addAttribute("error", "No se seleccionaron canciones para añadir.");
        verify(playlistCancionRepository, never()).save(any(PlayListCancion.class));
        // Verifica que el modelo se recarga correctamente para la vista
        verify(model).addAttribute("playlist", testPlaylist);
        verify(model).addAttribute("songsNotInPlaylist", songsNotInPlaylist);
        verify(model).addAttribute(eq("dto"), any(AddSongsObject.class));
    }

    @Test
    @DisplayName("addSongs() con lista de canciones nula debería redirigir a viewPlaylist y no guardar")
    void addSongs_listaCancionesNula_redirigeAViewPlaylistYNoGuarda() {
        //Arrange
        Integer playlistId = testPlaylist.getPlayListId();
        AddSongsObject dto = new AddSongsObject();
        dto.setCancionesIds(null); // Lista nula
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(testPlaylist));
        // Necesario para la recarga de la vista en caso de error o no acción
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);
        
        //Act
        String viewName = allController.addSongs(dto, playlistId, model);
        
        //Assert
        assertEquals("app1/viewPlaylist.html", viewName); // Se queda en la misma página
        verify(playlistRepository).findById(playlistId);
        verify(model).addAttribute("error", "No se seleccionaron canciones para añadir.");
        verify(playlistCancionRepository, never()).save(any(PlayListCancion.class));
        // Verifica que el modelo se recarga correctamente para la vista
        verify(model).addAttribute("playlist", testPlaylist);
        verify(model).addAttribute("songsNotInPlaylist", songsNotInPlaylist);
        verify(model).addAttribute(eq("dto"), any(AddSongsObject.class));
    }

    // Más pruebas unitarias para AllController aquí -> Ya no, están todas :)
} 