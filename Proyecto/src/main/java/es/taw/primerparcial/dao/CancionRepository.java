package es.taw.primerparcial.dao;

import es.taw.primerparcial.entity.Cancion;
import es.taw.primerparcial.entity.Genero;
import es.taw.primerparcial.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CancionRepository extends JpaRepository<Cancion, Integer> {
    @Query("select c from Cancion c where c not in " +
            "(select pc.cancionId from PlayListCancion pc where pc.playListId = :playlist)")
    public List<Cancion> findSongsNotInPlaylist(PlayList playlist);

    @Query("select c from Cancion c where :genero member of c.albumId.generoList")
    List<Cancion> findByGenero(@Param("genero") Genero genero);
}
