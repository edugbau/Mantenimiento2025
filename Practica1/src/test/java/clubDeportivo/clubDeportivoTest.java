package clubDeportivo;

import clubDeportivo.ClubDeportivo;
import clubDeportivo.ClubException;
import clubDeportivo.Grupo;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class clubDeportivoTest {
    ClubDeportivo club;
    Grupo grupo;
    @BeforeEach
    void setUp() throws ClubException {
        club = new ClubDeportivo("Sons",3);
        String codigo="Futbol";
        String actividad="Futbol";
        int nplazas=20;
        int nmatriculados=10;
        int tarifa=50;
        try {
            grupo = new Grupo(codigo, actividad, nplazas, nmatriculados, tarifa);
        } catch (ClubException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Creación de grupo válida")
    void crearGrupoValido() throws ClubException {assertNotNull(club);}

    @Test
    @DisplayName("No se puede crear un club con un número de grupos negativo")
    void crearGrupoNegativo() throws ClubException {
        assertThrows(ClubException.class, () -> new ClubDeportivo("Sons", 0));
    }

    @Test
    @DisplayName("Creación de grupo válida con solo nombre")
    void crearGrupoValidoNombre() throws ClubException {assertNotNull(new ClubDeportivo("Mayans"));}


    @Test
    @DisplayName("Añadir grupo nulo")
    void anyadirGrupoNulo() throws ClubException{
        assertThrows(ClubException.class, () -> club.anyadirActividad((Grupo) null));
    }

    @Test
    @DisplayName("Añadir grupo no existente")
    void anyadirGrupoNoExistente() throws ClubException{
        assertDoesNotThrow(() -> club.anyadirActividad(grupo));
    }
    @Test
    @DisplayName("Añadir grupo existente")
    void anyadirGrupoExistente() throws ClubException{
        club.anyadirActividad(grupo);
        assertDoesNotThrow(() -> club.anyadirActividad(grupo));
    }

    @Test
    @DisplayName("Añadir grupo mediante datos")
    void anyadirGrupoMedianteDatos() throws ClubException{
        Integer matriculados = 10;
        Integer tarifa = 50;
        String[] datos = { "miguel", "jose", "20", matriculados.toString(), tarifa.toString() };
        double ingresosAnteriores = club.ingresos();
        club.anyadirActividad(grupo);
        assertEquals(ingresosAnteriores + tarifa * matriculados, club.ingresos());
    }

    @Test
    @DisplayName("Añadir grupo con datos invalidos")
    void anyadirGrupoConDatosInvalidos() throws ClubException{
        String[] datos = { "miguel", "jose", "estoNoEsUnNumero", "estoTampoco", "estoAunMenos" };
        assertThrows(ClubException.class, () -> club.anyadirActividad(datos));
    }
    @Test
    @DisplayName("Las plazas libres son correctas")
    void plazasLibresCorrectas() throws ClubException{
        club.anyadirActividad(grupo);
        int plazasLibres = grupo.plazasLibres();
        String actividad = grupo.getActividad();
        assertEquals(club.plazasLibres(actividad), plazasLibres);
    }
    @Test
    @DisplayName("Matricular demasiadas personas")
    void matricularDemasiasPersonas() throws ClubException{
        club.anyadirActividad(grupo);
        assertThrows(ClubException.class, () -> club.matricular(grupo.getActividad(), 99999));
    }
    @Test
    @DisplayName("Matricular personas adecuadas")
    void matricularPersonasAdecuadas() throws ClubException{
        club.anyadirActividad(grupo);
        int plazasLibres = grupo.plazasLibres();
        String actividad = grupo.getActividad();
        club.matricular(actividad, plazasLibres);
        assertEquals(0, grupo.plazasLibres());
    }
}
