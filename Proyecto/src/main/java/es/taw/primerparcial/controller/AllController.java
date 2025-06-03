package es.taw.primerparcial.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/app1")
public class AllController {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    CancionRepository cancionRepository;
    @Autowired
    PlaylistCancionRepository playlistCancionRepository;

    @Transactional(readOnly = true)
    @GetMapping
    public String index(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("dto", new CreatePlaylistObject());
        return "app1/index.html";
    }
    @PostMapping("/createPlaylist")
    public String createPlaylist(@ModelAttribute("dto") CreatePlaylistObject dto, Model model) {
        PlayList playlist = new PlayList();
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId()).orElse(null);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("dto", dto);
            return "app1/index.html";
        }
        playlist.setUsuarioId(usuario);
        playlist.setPlayListName(dto.getNombre());
        playlist.setDateCreation(Calendar.getInstance().getTime());
        playlistRepository.save(playlist);
        return "redirect:/app1/viewPlaylist?playlistId=" + playlist.getPlayListId();
    }
    @Transactional(readOnly = true)
    @GetMapping("/viewPlaylist")
    public String viewPlaylist(@RequestParam("playlistId") Integer playlistId, Model model) {
        PlayList playlist = playlistRepository.findById(playlistId).orElse(null);
        if (playlist == null) {
            return "redirect:/app1/";
        }
        model.addAttribute("playlist", playlist);
        List<Cancion> songsNotInPlaylist = cancionRepository.findSongsNotInPlaylist(playlist);
        model.addAttribute("songsNotInPlaylist", songsNotInPlaylist);
        AddSongsObject dto = new AddSongsObject();
        model.addAttribute("dto", dto);
        return "app1/viewPlaylist.html";
    }
    @PostMapping("/addSongs")
    public String addSongs(@ModelAttribute("dto") AddSongsObject dto, @RequestParam("playlistId") Integer playlistId, Model model) {
        PlayList playlist = playlistRepository.findById(playlistId).orElse(null);
        if (playlist == null) {
            return "redirect:/app1/";
        }
        List<Integer> cancionesIds = dto.getCancionesIds();
        if (cancionesIds == null || cancionesIds.isEmpty()) {
            model.addAttribute("error", "No se seleccionaron canciones para añadir.");
            model.addAttribute("playlist", playlist);
            model.addAttribute("songsNotInPlaylist", cancionRepository.findSongsNotInPlaylist(playlist));
            model.addAttribute("dto", new AddSongsObject());
            return "redirect:/app1/viewPlaylist?playlistId=" + playlist.getPlayListId();
        }
        boolean notFound = false;
        for (Integer cancionId : cancionesIds) {
            var cancionOpt = cancionRepository.findById(cancionId);
            if (cancionOpt.isPresent()) {
                Cancion cancion = cancionOpt.get();
                PlayListCancion pc = new PlayListCancion();
                pc.setCancionId(cancion);
                pc.setPlayListId(playlist);
                playlistCancionRepository.save(pc);

                if (cancion.getPlayListCancionList() == null) {
                    cancion.setPlayListCancionList(new ArrayList<>());
                }
                cancion.getPlayListCancionList().add(pc);
                cancionRepository.save(cancion);

                if (playlist.getPlayListCancionList() == null) {
                    playlist.setPlayListCancionList(new ArrayList<>());
                }
                playlist.getPlayListCancionList().add(pc);
                playlistRepository.save(playlist);
            } else {
                notFound = true;
            }
        }
        if (notFound) {
            model.addAttribute("error", "Algunas canciones no se encontraron y no pudieron ser añadidas.");
            model.addAttribute("playlist", playlist);
            model.addAttribute("songsNotInPlaylist", cancionRepository.findSongsNotInPlaylist(playlist));
            model.addAttribute("dto", new AddSongsObject());
            return "redirect:/app1/viewPlaylist?playlistId=" + playlist.getPlayListId();
        }
        return "redirect:/app1/viewPlaylist?playlistId=" + playlist.getPlayListId();
    }
}
