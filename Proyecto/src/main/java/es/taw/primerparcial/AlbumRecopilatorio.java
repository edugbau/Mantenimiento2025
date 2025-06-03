package es.taw.primerparcial;

import es.taw.primerparcial.entity.Cancion;

import java.util.List;

public class AlbumRecopilatorio {
    private String albumRecopilatorioName;
    private String albumName;
    private List<Integer> canciones;

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
