package clubdeportivo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class grupoTest {
    Grupo miGrupo;
    @BeforeEach
    void setUp() {
        String codigo="Futbol";
        String actividad="Futbol";
        int nplazas=20;
        int nmatriculados=10;
        int tarifa=50;
        try {
            miGrupo = new Grupo(codigo, actividad, nplazas, nmatriculados, tarifa);
        } catch (ClubException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    @DisplayName("Test para probar excepcion de datos invalidos en constructor")
    void crearGrupoInvalido() {
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", -20, 30, 50);
        });
    }
    @Test
    void crearGrupoMatriculadosNegativos(){
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", 20, -30, 50);
        });
    }
    @Test
    void crearGrupoTarifaNegativa(){
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", 20, 10, -50);
        });
    }
    @Test
    @DisplayName("Test para probar que no pueden haber mas matriculados que plazas")
    void crearMatriculadoInvalido() {
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", 10, 15, 50);
        });
    }
    @Test
    @DisplayName("Probar que las plazas libres no sean negativas")
    void plazasLibresSiempreMayor0() {
        assertTrue(miGrupo.plazasLibres() >= 0);
    }

    @Test
    @DisplayName("Probar que no se pueden actualizar plazas negativas")
    void plazasInvalidas(){
        assertThrows(ClubException.class, () -> {
            miGrupo.actualizarPlazas(5);
        });
    }
    @Test
    void plazasNegativas(){
        assertThrows(ClubException.class, () -> {
            miGrupo.actualizarPlazas(-5);
        });
    }
    @Test
    @DisplayName("Probar que las plazas se actualizan con numeros validos")
    void plazasValidas(){
        try {
            miGrupo.actualizarPlazas(50);
        } catch (ClubException e) {
            throw new RuntimeException(e);
        }
        assertEquals(50, miGrupo.getPlazas());
    }
    @Test
    @DisplayName("No se puede matricular a más personas de las plazas permitidas")
    void matricularDemasiadasPersonas(){
        assertThrows(ClubException.class, () -> {
            miGrupo.matricular(999999);
        });
    }
    @Test
    @DisplayName("No se puede matricular a personas negativas")
    void matricularPersonasNegativas(){
        assertThrows(ClubException.class, () -> {
            miGrupo.matricular(-999999);
        });
    }
    @Test
    @DisplayName("Al matricular con datos válidos se reflejan los cambios")
    void matricularPersonas() throws ClubException {
        int matriculadosAnteriores = miGrupo.getMatriculados();
        int nuevosMatriculados = 3;
        miGrupo.matricular(nuevosMatriculados);
        assertEquals(miGrupo.getMatriculados(), matriculadosAnteriores + nuevosMatriculados);
    }
    @Test
    @DisplayName("Equals funciona en true")
    void equalsTrue() throws ClubException {
        String codigo="Futbol";
        String actividad="Futbol";
        int nplazas=20;
        int nmatriculados=10;
        int tarifa=50;
        Grupo nuevoGrupo = new Grupo(codigo, actividad, nplazas, nmatriculados, tarifa);
        assertTrue(miGrupo.equals(nuevoGrupo));
    }
    @Test
    @DisplayName("Equals funciona en false")
    void equalsFalse() throws ClubException {
        Grupo nuevoGrupo = new Grupo("miguel","miguelito",1,1,1);
        assertFalse(miGrupo.equals(nuevoGrupo));
    }
    @Test
    @DisplayName("Equals funciona en false")
    void equalsFalse2() throws ClubException {
        Grupo nuevoGrupo = new Grupo("futbol","miguelito",1,1,1);
        assertFalse(miGrupo.equals(nuevoGrupo));
    }
    @Test
    @DisplayName("Equals da false con otras objetos")
    void equalsFalseObjetos() {
        assertFalse(miGrupo.equals(new StringBuilder()));
    }
    @Test
    @DisplayName("HashCode de objetos iguales son iguales")
    void hashCodeiguales() throws ClubException {
        String codigo="Futbol";
        String actividad="Futbol";
        int nplazas=20;
        int nmatriculados=10;
        int tarifa=50;
        Grupo nuevoGrupo = new Grupo(codigo, actividad, nplazas, nmatriculados, tarifa);
        assertTrue(miGrupo.hashCode() == nuevoGrupo.hashCode());
    }
    @Test
    @DisplayName("HashCode de objetos distintos son distintos")
    void hashCodedistintos() throws ClubException {
        Grupo nuevoGrupo = new Grupo("miguel","miguelito",1,1,1);
        assertTrue(!miGrupo.equals(nuevoGrupo) && miGrupo.hashCode() != nuevoGrupo.hashCode());
    }
    @Test
    @DisplayName("ToString de objetos con equals son iguales")
    void toStringiguales() throws ClubException {
        String codigo="Futbol";
        String actividad="Futbol";
        int nplazas=20;
        int nmatriculados=10;
        int tarifa=50;
        Grupo nuevoGrupo = new Grupo(codigo, actividad, nplazas, nmatriculados, tarifa);
        assertEquals(miGrupo.toString(), nuevoGrupo.toString());
    }
    @Test
    @DisplayName("ToString de objetos sin equals son distintos")
    void toStringdistintos() throws ClubException {
        Grupo nuevoGrupo = new Grupo("miguel","miguelito",1,1,1);
        assertNotEquals(miGrupo.toString(), nuevoGrupo.toString());
    }
}
