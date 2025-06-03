package es.taw.primerparcial.controller;

import es.taw.primerparcial.dto.AlbumRecopilatorio;
import es.taw.primerparcial.dao.AlbumRepository;
import es.taw.primerparcial.dao.ArtistaRepository;
import es.taw.primerparcial.dao.CancionRepository;
import es.taw.primerparcial.dao.GeneroRepository;
import es.taw.primerparcial.entity.Album;
import es.taw.primerparcial.entity.Artista;
import es.taw.primerparcial.entity.Cancion;
import es.taw.primerparcial.entity.Genero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/app2")
public class AlbumController {
    @Autowired
    private ArtistaRepository artistaRepository;
    @Autowired
    private CancionRepository cancionRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GeneroRepository generoRepository;

    @Transactional(readOnly = true)
    @GetMapping
    public String doInit(Model model) {
        List<Artista> artistas = artistaRepository.findAll();
        model.addAttribute("artistas", artistas);
        return "app2/home.html";
    }

    @Transactional(readOnly = true)
    @GetMapping("/addAlbum")
    public String doAddAlbum(Model model) {
        AlbumRecopilatorio albumRecopilatorio = new AlbumRecopilatorio();
        model.addAttribute("albumRecopilatorio", albumRecopilatorio);
        List<Cancion> canciones = cancionRepository.findAll();
        model.addAttribute("canciones", canciones);
        List<Genero> generos = generoRepository.findAll();
        model.addAttribute("generos", generos);
        return "app2/addAlbum.html";
    }

    @PostMapping("/saveAlbum")
    public String doSaveAlbum(@ModelAttribute AlbumRecopilatorio albumRecopilatorio, Model model) {
        Artista artista = new Artista();
        Album album = new Album();

        artista.setArtistaName(albumRecopilatorio.getAlbumRecopilatorioName());
        album.setAlbumName(albumRecopilatorio.getAlbumRecopilatorioName());
        album.setArtistaId(artista);
        album.setDateReleased(new Date(2025 - 1900, 4 - 1, 29));

        artistaRepository.save(artista);
        albumRepository.save(album);

        if (albumRecopilatorio.getCanciones() != null) {
            for (Integer i : albumRecopilatorio.getCanciones()) {
                cancionRepository.findById(i).ifPresent(cancion -> {
                    Cancion newCancion = new Cancion();
                    newCancion.setCancionName(cancion.getCancionName() + "(" + albumRecopilatorio.getAlbumRecopilatorioName() + ")");
                    newCancion.setAlbumId(album);
                    if (cancion.getArtistaList() != null) {
                        newCancion.setArtistaList(new ArrayList<>(cancion.getArtistaList()));
                    }
                    newCancion.setDateReleased(cancion.getDateReleased());
                    cancionRepository.save(newCancion);

                    if (cancion.getArtistaList() != null) {
                        for (Artista artista1 : cancion.getArtistaList()) {
                            List<Cancion> cancionArtistaList = artista1.getCancionList();
                            if (cancionArtistaList == null) cancionArtistaList = new ArrayList<>();
                            cancionArtistaList.add(newCancion);
                            artista1.setCancionList(cancionArtistaList);
                            artistaRepository.save(artista1);
                        }
                    }

                    if (cancion.getAlbumId() != null && cancion.getAlbumId().getArtistaId() != null) {
                        Artista original = cancion.getAlbumId().getArtistaId();
                        List<Cancion> cancionArtistaList = original.getCancionList();
                        if (cancionArtistaList == null) cancionArtistaList = new ArrayList<>();
                        cancionArtistaList.add(newCancion);
                        original.setCancionList(cancionArtistaList);
                        artistaRepository.save(original);
                    }
                });
            }
        }
        return "redirect:/app2/";
    }

    @Transactional(readOnly = true)
    @PostMapping("/filter")
    public String doFilter(@RequestParam(required = false) Genero genero, Model model) {
        if (genero == null) {
            return "redirect:/app2/addAlbum";
        }
        AlbumRecopilatorio albumRecopilatorio = new AlbumRecopilatorio();
        model.addAttribute("albumRecopilatorio", albumRecopilatorio);
        List<Cancion> canciones = cancionRepository.findByGenero(genero);
        model.addAttribute("canciones", canciones);
        List<Genero> generos = generoRepository.findAll();
        model.addAttribute("generos", generos);
        return "app2/addAlbum.html";
    }
} 