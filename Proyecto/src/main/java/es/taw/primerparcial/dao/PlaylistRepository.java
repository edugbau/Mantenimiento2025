package es.taw.primerparcial.dao;

import es.taw.primerparcial.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<PlayList, Integer> {
}
