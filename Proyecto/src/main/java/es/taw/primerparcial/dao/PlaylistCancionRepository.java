package es.taw.primerparcial.dao;

import es.taw.primerparcial.entity.PlayListCancion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistCancionRepository extends JpaRepository<PlayListCancion, Integer> {
}
