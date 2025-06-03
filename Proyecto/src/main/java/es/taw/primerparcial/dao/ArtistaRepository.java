package es.taw.primerparcial.dao;

import es.taw.primerparcial.entity.Artista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistaRepository extends JpaRepository<Artista, Integer> {

}
