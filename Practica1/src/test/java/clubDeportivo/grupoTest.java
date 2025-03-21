package clubDeportivo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//MIEMBROS: Eduardo González Bautista y Juan Manuel Valenzuela González

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
    void crearGrupoPlazasNegativas_DaExcepcion() {
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", -20, 30, 50);
        });
    }
    @Test
    void crearGrupoMatriculadosNegativos_DaExcepcion() {
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", 20, -30, 50);
        });
    }
    @Test
    void crearGrupoTarifaNegativa_LanzaExcepcion(){
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", 20, 10, -50);
        });
    }
    @Test
    @DisplayName("Test para probar que no pueden haber mas matriculados que plazas")
    void crearMatriculadoInvalido_LanzaExcepcion() {
        assertThrows(ClubException.class, () -> {
            Grupo miGrupo = new Grupo("Futbol", "Futbol", 10, 15, 50);
        });
    }
    @Test
    @DisplayName("Test para crear grupo con plazas igual a matriculados")
    void crearGrupoPlazasIgualMatriculados_Funciona() {
        try {
            // Arrange & Act
            Grupo grupo = new Grupo("Futbol", "Futbol", 10, 10, 50);

            // Assert
            assertEquals(10, grupo.getPlazas());
            assertEquals(10, grupo.getMatriculados());
            assertEquals(0, grupo.plazasLibres());
        } catch (ClubException e) {
            fail("No debería lanzar excepción");
        }
    }
    @Test
    @DisplayName("Probar que las plazas libres no sean negativas")
    void plazasLibresSiempreMayor0_EsTrue() {
        assertTrue(miGrupo.plazasLibres() >= 0);
    }

    @Test
    @DisplayName("Probar que las plazas libres funcionan correctamente")
    void plazasLibresCorrectas_Funciona() {
        // Arrange
        int esperado = miGrupo.getPlazas() - miGrupo.getMatriculados();

        // Act
        int resultado = miGrupo.plazasLibres();

        // Assert
        assertEquals(esperado, resultado);
    }
    @Test
    @DisplayName("Probar que las plazas libres son cero cuando está lleno")
    void plazasLibresCero_Funciona() {
        try {
            // Arrange
            Grupo grupoLleno = new Grupo("Lleno", "Actividad", 5, 5, 30);

            // Act
            int plazasLibres = grupoLleno.plazasLibres();

            // Assert
            assertEquals(0, plazasLibres);
        } catch (ClubException e) {
            fail("No debería lanzar excepción");
        }
    }
    @Test
    @DisplayName("Probar que no se pueden actualizar plazas negativas")
    void plazasInvalidas_LanzaExcepcion(){
        int n = 5;
        assertThrows(ClubException.class, () -> {
            miGrupo.actualizarPlazas(n);
        });
    }
    @Test
    void plazasNegativas_LanzaExcepcion(){
        int n = -5;
        assertThrows(ClubException.class, () -> {
            miGrupo.actualizarPlazas(n);
        });
    }
    @Test
    @DisplayName("Probar que las plazas se actualizan con numeros validos")
    void plazasValidas(){
        int n = 50;
        try {
            miGrupo.actualizarPlazas(n);
        } catch (ClubException e) {
            throw new RuntimeException(e);
        }
        assertEquals(n, miGrupo.getPlazas());
    }
    @Test
    @DisplayName("No se puede matricular a más personas de las plazas permitidas")
    void matricularDemasiadasPersonas_LanzaExcepcion(){
        int n = 999999;
        assertThrows(ClubException.class, () -> {
            miGrupo.matricular(n);
        });
    }
    @Test
    @DisplayName("No se puede matricular a personas negativas")
    void matricularPersonasNegativas_LanzaExcepcion(){
        int n = -999999;
        assertThrows(ClubException.class, () -> {
            miGrupo.matricular(n);
        });
    }
    @Test
    @DisplayName("No se puede matricular a cero personas")
    void matricularCeroPersonas_LanzaExcepcion(){
        int n = 0;
        assertThrows(ClubException.class, () -> {
            miGrupo.matricular(n);
        });
    }
    @Test
    @DisplayName("Probar que no se pueden actualizar plazas a cero")
    void plazasCero_LanzaExcepcion(){
        int n = 0;
        assertThrows(ClubException.class, () -> {
            miGrupo.actualizarPlazas(n);
        });
    }
    @Test
    @DisplayName("Probar que las plazas se actualizan con número igual a matriculados")
    void plazasIgualesAMatriculados_Funciona(){
        try {
            // Arrange
            int matriculados = miGrupo.getMatriculados();

            // Act
            miGrupo.actualizarPlazas(matriculados);

            // Assert
            assertEquals(matriculados, miGrupo.getPlazas());
            assertEquals(0, miGrupo.plazasLibres());
        } catch (ClubException e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
    @Test
    @DisplayName("Al matricular con datos válidos se reflejan los cambios")
    void matricularPersonas_Funciona() throws ClubException {
        int matriculadosAnteriores = miGrupo.getMatriculados();
        int nuevosMatriculados = 3;
        miGrupo.matricular(nuevosMatriculados);
        assertEquals(miGrupo.getMatriculados(), matriculadosAnteriores + nuevosMatriculados);
    }
    @Test
    @DisplayName("Equals funciona en true")
    void equalsTrue_Funciona() throws ClubException {
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
    void equalsFalse_Funciona() throws ClubException {
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

    // POR LO QUE SEA hay un get publico
    @Test
    @DisplayName("getCodigo devuelve el codigo correcto")
    void getCodigoTest() throws ClubException {
        assertEquals("Futbol", miGrupo.getCodigo());
    }
}
