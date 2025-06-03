package es.taw.primerparcial.controller.IT;

import es.taw.primerparcial.config.SecurityConfig;
import es.taw.primerparcial.controller.AllController;
import es.taw.primerparcial.dao.CancionRepository;
import es.taw.primerparcial.dao.PlaylistCancionRepository;
import es.taw.primerparcial.dao.PlaylistRepository;
import es.taw.primerparcial.dao.UsuarioRepository;
import es.taw.primerparcial.dto.CreatePlaylistObject;
import es.taw.primerparcial.dto.AddSongsObject;
import es.taw.primerparcial.entity.Usuario;
import es.taw.primerparcial.entity.PlayList;
import es.taw.primerparcial.entity.Cancion;
import es.taw.primerparcial.entity.PlayListCancion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AllController.class)
@Import(SecurityConfig.class)
public class AllControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PlaylistRepository playlistRepository;

    @MockBean
    private CancionRepository cancionRepository;

    @MockBean
    private PlaylistCancionRepository playlistCancionRepository;

    @MockBean
    private UserDetailsService userDetailsService; // Para SecurityConfig

    private List<Usuario> usuarioList;
    private PlayList testPlaylist;
    private List<Cancion> songsNotInPlaylist;
    private Cancion cancionInPlaylist;

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
        testPlaylist.setPlayListCancionList(new ArrayList<>()); // Inicializar lista

        cancionInPlaylist = new Cancion(); // Aunque no se use directamente en songsNotInPlaylist, es bueno tenerlo
        cancionInPlaylist.setCancionId(10);
        cancionInPlaylist.setCancionName("Cancion en Playlist");

        // Simular que una PlayListCancion existe (aunque no la modelemos completamente aquí)
        // es.taw.primerparcial.entity.PlayListCancion plc = new es.taw.primerparcial.entity.PlayListCancion();
        // plc.setCancionId(cancionInPlaylist);
        // plc.setPlayListId(testPlaylist);
        // testPlaylist.getPlayListCancionList().add(plc);

        songsNotInPlaylist = new ArrayList<>();
        Cancion cancion1 = new Cancion();
        cancion1.setCancionId(1);
        cancion1.setCancionName("Cancion Fuera 1");
        songsNotInPlaylist.add(cancion1);
        Cancion cancion2 = new Cancion();
        cancion2.setCancionId(2);
        cancion2.setCancionName("Cancion Fuera 2");
        songsNotInPlaylist.add(cancion2);
    }

    @Test
    public void testGetApp1Index_unauthenticated() throws Exception {
        //Arrange
        // No se necesita arrange para esta prueba, ya que el estado inicial es un usuario no autenticado.

        //Act
        mockMvc.perform(get("/app1"))
        //Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    public void testGetApp1Index_authenticated() throws Exception {
        //Arrange
        when(usuarioRepository.findAll()).thenReturn(usuarioList);

        //Act
        mockMvc.perform(get("/app1"))
        //Assert
                .andExpect(status().isOk())
                .andExpect(view().name("app1/index.html")) // Ajustado según tu cambio
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", usuarioList))
                .andExpect(model().attributeExists("dto"))
                .andExpect(model().attribute("dto", org.hamcrest.Matchers.instanceOf(CreatePlaylistObject.class)));
    }

    @Test
    @WithMockUser
    public void testCreatePlaylist_success() throws Exception {
        //Arrange
        Usuario testUser = usuarioList.get(0);
        when(usuarioRepository.findById(testUser.getUsuarioId())).thenReturn(Optional.of(testUser));

        // Simular que el save en el repositorio asigna un ID a la playlist
        when(playlistRepository.save(any(es.taw.primerparcial.entity.PlayList.class))).thenAnswer(invocation -> {
            es.taw.primerparcial.entity.PlayList p = invocation.getArgument(0);
            p.setPlayListId(123); // Simular ID asignado
            p.setDateCreation(new Date()); // Asegurar que dateCreation no es null si la entidad lo requiere
            return p;
        });

        //Act
        mockMvc.perform(post("/app1/createPlaylist")
                .param("nombre", "Mi Nueva Playlist")
                .param("usuarioId", String.valueOf(testUser.getUsuarioId()))
                .with(csrf())) // Añadir token CSRF
        //Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/viewPlaylist?playlistId=123"));

        verify(playlistRepository).save(any(es.taw.primerparcial.entity.PlayList.class));
    }

    @Test
    @WithMockUser
    public void testCreatePlaylist_userNotFound() throws Exception {
        //Arrange
        int nonExistentUserId = 999;
        when(usuarioRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        // Asegurar que findAll() sigue funcionando para recargar el formulario
        when(usuarioRepository.findAll()).thenReturn(usuarioList);

        //Act
        mockMvc.perform(post("/app1/createPlaylist")
                .param("nombre", "Playlist Fallida")
                .param("usuarioId", String.valueOf(nonExistentUserId))
                .with(csrf()))
        //Assert
                .andExpect(status().isOk()) // Devuelve a la página del formulario
                .andExpect(view().name("app1/index.html"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Usuario no encontrado."))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attributeExists("dto"));
    }

    @Test
    @WithMockUser
    public void testViewPlaylist_success() throws Exception {
        //Arrange
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);

        //Act
        mockMvc.perform(get("/app1/viewPlaylist").param("playlistId", String.valueOf(testPlaylist.getPlayListId())))
        //Assert
                .andExpect(status().isOk())
                .andExpect(view().name("app1/viewPlaylist.html")) // Ajustado
                .andExpect(model().attributeExists("playlist"))
                .andExpect(model().attribute("playlist", testPlaylist))
                .andExpect(model().attributeExists("songsNotInPlaylist"))
                .andExpect(model().attribute("songsNotInPlaylist", songsNotInPlaylist))
                .andExpect(model().attributeExists("dto"))
                .andExpect(model().attribute("dto", org.hamcrest.Matchers.instanceOf(AddSongsObject.class)));
    }

    @Test
    @WithMockUser
    public void testViewPlaylist_notFound() throws Exception {
        //Arrange
        int nonExistentPlaylistId = 999;
        when(playlistRepository.findById(nonExistentPlaylistId)).thenReturn(Optional.empty());

        //Act
        mockMvc.perform(get("/app1/viewPlaylist").param("playlistId", String.valueOf(nonExistentPlaylistId)))
        //Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/"));
    }

    @Test
    @WithMockUser
    public void testAddSongs_success() throws Exception {
        //Arrange
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));
        Cancion cancionToAdd1 = songsNotInPlaylist.get(0);
        Cancion cancionToAdd2 = songsNotInPlaylist.get(1);
        when(cancionRepository.findById(cancionToAdd1.getCancionId())).thenReturn(Optional.of(cancionToAdd1));
        when(cancionRepository.findById(cancionToAdd2.getCancionId())).thenReturn(Optional.of(cancionToAdd2));

        //Act - Corrigiendo el nombre del parámetro
        mockMvc.perform(post("/app1/addSongs")
                        .param("playlistId", String.valueOf(testPlaylist.getPlayListId()))
                        .param("cancionesIds", String.valueOf(cancionToAdd1.getCancionId()), String.valueOf(cancionToAdd2.getCancionId()))
                        .with(csrf()))
                //Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/viewPlaylist?playlistId=" + testPlaylist.getPlayListId()));

        verify(playlistCancionRepository).saveAll(any(List.class));
    }

    @Test
    @WithMockUser
    public void testAddSongs_playlistNotFound() throws Exception {
        //Arrange
        int nonExistentPlaylistId = 999;
        when(playlistRepository.findById(nonExistentPlaylistId)).thenReturn(Optional.empty());
        Cancion cancionToAdd = songsNotInPlaylist.get(0);
        // No es necesario mockear cancionRepository.findById si la playlist no se encuentra primero

        //Act
        mockMvc.perform(post("/app1/addSongs")
                .param("playlistId", String.valueOf(nonExistentPlaylistId))
                .param("songIds", String.valueOf(cancionToAdd.getCancionId()))
                .with(csrf()))
        //Assert
                .andExpect(status().is3xxRedirection()) // O el comportamiento que esperes, ej. error o vista específica
                .andExpect(redirectedUrl("/app1/")); // Asumiendo que redirige a la lista si la playlist no existe
    }

    @Test
    @WithMockUser
    public void testAddSongs_someSongsNotFound() throws Exception {
        //Arrange
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));
        Cancion existingSong = songsNotInPlaylist.get(0);
        int nonExistentSongId = 999;
        when(cancionRepository.findById(existingSong.getCancionId())).thenReturn(Optional.of(existingSong));
        when(cancionRepository.findById(nonExistentSongId)).thenReturn(Optional.empty());
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);

        //Act - Corrigiendo el nombre del parámetro
        mockMvc.perform(post("/app1/addSongs")
                        .param("playlistId", String.valueOf(testPlaylist.getPlayListId()))
                        .param("cancionesIds", String.valueOf(existingSong.getCancionId()), String.valueOf(nonExistentSongId))
                        .with(csrf()))
                //Assert
                .andExpect(status().isOk())
                .andExpect(view().name("app1/viewPlaylist.html"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Algunas canciones no se encontraron y no pudieron ser añadidas."))
                .andExpect(model().attribute("playlist", testPlaylist))
                .andExpect(model().attribute("songsNotInPlaylist", songsNotInPlaylist));

        // Verificar que saveAll se ejecutó con las canciones válidas
        verify(playlistCancionRepository).saveAll(anyList());
    }

    @Test
    @WithMockUser
    public void testAddSongs_emptySongList() throws Exception {
        //Arrange
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));
        // Mockear findSongsNotInPlaylist para la recarga de la vista
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);

        //Act
        mockMvc.perform(post("/app1/addSongs")
                .param("playlistId", String.valueOf(testPlaylist.getPlayListId()))
                // No se envían songIds
                .with(csrf()))
        //Assert
                .andExpect(status().isOk()) // Vuelve a la vista de la playlist
                .andExpect(view().name("app1/viewPlaylist.html"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "No se seleccionaron canciones para añadir."))
                .andExpect(model().attribute("playlist", testPlaylist))
                .andExpect(model().attribute("songsNotInPlaylist", songsNotInPlaylist));
    }

    @Test
    @WithMockUser
    public void testAddSongs_withNullPlaylistList() throws Exception {
        // Arrange
        // Configurar una playlist con PlayListCancionList explícitamente nula
        testPlaylist.setPlayListCancionList(null);
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));

        Cancion cancionToAdd = songsNotInPlaylist.get(0);
        when(cancionRepository.findById(cancionToAdd.getCancionId())).thenReturn(Optional.of(cancionToAdd));

        // Act
        mockMvc.perform(post("/app1/addSongs")
                        .param("playlistId", String.valueOf(testPlaylist.getPlayListId()))
                        .param("cancionesIds", String.valueOf(cancionToAdd.getCancionId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/viewPlaylist?playlistId=" + testPlaylist.getPlayListId()));

        // Assert
        // Capturar la playlist guardada para verificar que su lista fue inicializada
        ArgumentCaptor<PlayList> playlistCaptor = ArgumentCaptor.forClass(PlayList.class);
        verify(playlistRepository).save(playlistCaptor.capture());

        PlayList savedPlaylist = playlistCaptor.getValue();
        assertNotNull(savedPlaylist.getPlayListCancionList());
        assertFalse(savedPlaylist.getPlayListCancionList().isEmpty());
    }
} 