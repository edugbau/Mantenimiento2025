package es.taw.primerparcial.dao;

import es.taw.primerparcial.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByUsuarioName(String usuarioName);
}
