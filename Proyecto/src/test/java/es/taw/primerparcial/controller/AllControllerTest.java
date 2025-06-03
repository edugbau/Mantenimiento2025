package es.taw.primerparcial.controller;

import es.taw.primerparcial.config.SecurityConfig;
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
        mockMvc.perform(get("/app1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    public void testGetApp1Index_authenticated() throws Exception {
        when(usuarioRepository.findAll()).thenReturn(usuarioList);

        mockMvc.perform(get("/app1"))
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
        Usuario testUser = usuarioList.get(0);
        when(usuarioRepository.findById(testUser.getUsuarioId())).thenReturn(Optional.of(testUser));

        // Simular que el save en el repositorio asigna un ID a la playlist
        when(playlistRepository.save(any(es.taw.primerparcial.entity.PlayList.class))).thenAnswer(invocation -> {
            es.taw.primerparcial.entity.PlayList p = invocation.getArgument(0);
            p.setPlayListId(123); // Simular ID asignado
            p.setDateCreation(new Date()); // Asegurar que dateCreation no es null si la entidad lo requiere
            return p;
        });

        mockMvc.perform(post("/app1/createPlaylist")
                .param("nombre", "Mi Nueva Playlist")
                .param("usuarioId", String.valueOf(testUser.getUsuarioId()))
                .with(csrf())) // Añadir token CSRF
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/viewPlaylist?playlistId=123"));

        verify(playlistRepository).save(any(es.taw.primerparcial.entity.PlayList.class));
    }

    @Test
    @WithMockUser
    public void testCreatePlaylist_userNotFound() throws Exception {
        int nonExistentUserId = 999;
        when(usuarioRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        // Asegurar que findAll() sigue funcionando para recargar el formulario
        when(usuarioRepository.findAll()).thenReturn(usuarioList);

        mockMvc.perform(post("/app1/createPlaylist")
                .param("nombre", "Playlist Fallida")
                .param("usuarioId", String.valueOf(nonExistentUserId))
                .with(csrf()))
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
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));
        when(cancionRepository.findSongsNotInPlaylist(testPlaylist)).thenReturn(songsNotInPlaylist);

        mockMvc.perform(get("/app1/viewPlaylist").param("playlistId", String.valueOf(testPlaylist.getPlayListId())))
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
        int nonExistentPlaylistId = 999;
        when(playlistRepository.findById(nonExistentPlaylistId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/app1/viewPlaylist").param("playlistId", String.valueOf(nonExistentPlaylistId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/"));
    }

    @Test
    @WithMockUser
    public void testAddSongs_success() throws Exception {
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));
        Cancion cancionToAdd1 = songsNotInPlaylist.get(0); // ID 1
        Cancion cancionToAdd2 = songsNotInPlaylist.get(1); // ID 2
        when(cancionRepository.findById(cancionToAdd1.getCancionId())).thenReturn(Optional.of(cancionToAdd1));
        when(cancionRepository.findById(cancionToAdd2.getCancionId())).thenReturn(Optional.of(cancionToAdd2));

        // Cuando se guarda PlayListCancion, simplemente devolvemos el objeto que se pasó.
        when(playlistCancionRepository.save(any(PlayListCancion.class))).thenAnswer(inv -> inv.getArgument(0));
        // Similar para cancion y playlist, aunque el estado exacto no se verifica profundamente aquí
        when(cancionRepository.save(any(Cancion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(playlistRepository.save(any(PlayList.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/app1/addSongs")
                .param("playlistId", String.valueOf(testPlaylist.getPlayListId()))
                .param("cancionesIds", String.valueOf(cancionToAdd1.getCancionId())) // Enviamos IDs como string
                .param("cancionesIds", String.valueOf(cancionToAdd2.getCancionId()))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/viewPlaylist?playlistId=" + testPlaylist.getPlayListId()));

        verify(playlistCancionRepository, org.mockito.Mockito.times(2)).save(any(PlayListCancion.class));
        verify(cancionRepository, org.mockito.Mockito.times(2)).save(any(Cancion.class));
        verify(playlistRepository, org.mockito.Mockito.times(3)).save(any(PlayList.class)); // 1 en setup (si aplica), 2 aquí por cada canción añadida a la lista de la playlist
    }

    @Test
    @WithMockUser
    public void testAddSongs_playlistNotFound() throws Exception {
        int nonExistentPlaylistId = 999;
        when(playlistRepository.findById(nonExistentPlaylistId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/app1/addSongs")
                .param("playlistId", String.valueOf(nonExistentPlaylistId))
                .param("cancionesIds", "1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/"));
    }

    @Test
    @WithMockUser
    public void testAddSongs_someSongsNotFound() throws Exception {
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));
        Cancion existingCancion = songsNotInPlaylist.get(0); // ID 1
        int nonExistentCancionId = 999;

        when(cancionRepository.findById(existingCancion.getCancionId())).thenReturn(Optional.of(existingCancion));
        when(cancionRepository.findById(nonExistentCancionId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/app1/addSongs")
                .param("playlistId", String.valueOf(testPlaylist.getPlayListId()))
                .param("cancionesIds", String.valueOf(existingCancion.getCancionId()))
                .param("cancionesIds", String.valueOf(nonExistentCancionId))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/viewPlaylist?playlistId=" + testPlaylist.getPlayListId()));

        // Se guarda la canción existente
        verify(playlistCancionRepository, org.mockito.Mockito.times(1)).save(any(PlayListCancion.class));
    }

    @Test
    @WithMockUser
    public void testAddSongs_emptySongList() throws Exception {
        when(playlistRepository.findById(testPlaylist.getPlayListId())).thenReturn(Optional.of(testPlaylist));

        mockMvc.perform(post("/app1/addSongs")
                .param("playlistId", String.valueOf(testPlaylist.getPlayListId()))
                // No se envían cancionesIds
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app1/viewPlaylist?playlistId=" + testPlaylist.getPlayListId()));

        verify(playlistCancionRepository, org.mockito.Mockito.never()).save(any(PlayListCancion.class));
    }
} 