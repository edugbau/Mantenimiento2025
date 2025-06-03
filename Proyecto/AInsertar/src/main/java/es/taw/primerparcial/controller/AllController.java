package es.taw.primerparcial.controller;

import es.taw.primerparcial.AlbumRecopilatorio;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import java.util.Date;
import java.util.List;

@Controller
public class AllController {
    @Autowired
    private ArtistaRepository artistaRepository;
    @Autowired
    private CancionRepository cancionRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GeneroRepository generoRepository;

    @GetMapping("/")
    public String doInit(Model model) {
        List<Artista> artistas = (List<Artista>) artistaRepository.findAll();
        model.addAttribute("artistas", artistas);
        return "home";
    }
    @GetMapping("/addAlbum")
    public String doAddAlbum(Model model) {
        AlbumRecopilatorio albumRecopilatorio = new AlbumRecopilatorio();
        model.addAttribute("albumRecopilatorio", albumRecopilatorio);
        List<Cancion> canciones = cancionRepository.findAll();
        model.addAttribute("canciones", canciones);
        List<Genero> generos = generoRepository.findAll();
        model.addAttribute("generos", generos);
        return "addAlbum";
    }
    @PostMapping("/saveAlbum")
    public String doSaveAlbum(@ModelAttribute AlbumRecopilatorio albumRecopilatorio, Model model) {
        Artista artista = new Artista();
        Album album = new Album();

        artista.setArtistaName(albumRecopilatorio.getAlbumRecopilatorioName());
        album.setAlbumName(albumRecopilatorio.getAlbumRecopilatorioName());
        album.setArtistaId(artista);
        album.setDateReleased(new Date(2025,4,29));

        artistaRepository.save(artista);
        albumRepository.save(album);


        for (Integer i : albumRecopilatorio.getCanciones()) {
            Cancion cancion = cancionRepository.findById(i).get();
            Cancion newCancion = new Cancion();
            newCancion.setCancionName(cancion.getCancionName() + "(" + albumRecopilatorio.getAlbumRecopilatorioName() + ")");
            newCancion.setAlbumId(album);
            newCancion.setArtistaList(cancion.getArtistaList().subList(0,cancion.getArtistaList().size())); //TODO: como coño cambio esta guarrada


            newCancion.setDateReleased(cancion.getDateReleased());
            cancionRepository.save(newCancion);
            for(Artista artista1 : cancion.getArtistaList()) {
                List<Cancion> cancionArtistaList = artista1.getCancionList();
                cancionArtistaList.add(newCancion);
                artista1.setCancionList(cancionArtistaList);
                artistaRepository.save(artista1);
            }
            Artista original = cancion.getAlbumId().getArtistaId();
            List<Cancion> cancionArtistaList = original.getCancionList();
            cancionArtistaList.add(newCancion);
            original.setCancionList(cancionArtistaList);
            artistaRepository.save(original );

        }
        return "redirect:/";
    }

    @PostMapping("/filter")
    public String doFilter(@RequestParam Genero genero, Model model) {
        if(genero == null){
            return "redirect:/addAlbum";
        }
        AlbumRecopilatorio albumRecopilatorio = new AlbumRecopilatorio();
        model.addAttribute("albumRecopilatorio", albumRecopilatorio);
        List<Cancion> canciones = cancionRepository.findByGenero(genero);
        model.addAttribute("canciones", canciones);
        List<Genero> generos = generoRepository.findAll();
        model.addAttribute("generos", generos);
        return "addAlbum";
    }
}
