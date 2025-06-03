package es.taw.primerparcial.dto;

import java.util.List;

// Considerar añadir @Data de Lombok si se usa en el proyecto consistentemente.
public class AlbumRecopilatorio {
    private String albumRecopilatorioName;
    private String albumName; // Este campo no se usó en el HTML de addAlbum, pero está en el DTO.
    private List<Integer> canciones; // Lista de IDs de Cancion

    public String getAlbumRecopilatorioName() {
        return albumRecopilatorioName;
    }

    public void setAlbumRecopilatorioName(String albumRecopilatorioName) {
        this.albumRecopilatorioName = albumRecopilatorioName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public List<Integer> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<Integer> canciones) {
        this.canciones = canciones;
    }

} 