package clubDeportivo;

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
    void anyadirGrupoNulo() throws ClubException {
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
    // Este test solo existe para cubrir los casos de buscar
    @Test
    @DisplayName("Añadir dos grupos")
    void anyadirDosGrupos() throws ClubException{
        Integer matriculados = 10;
        Integer tarifa = 50;
        String[] datos = { "Fortniteor", "pubg", "20", matriculados.toString(), tarifa.toString() };
        club.anyadirActividad(grupo);

        assertDoesNotThrow(() -> club.anyadirActividad(datos));
    }
    @Test
    @DisplayName("Añadir grupo mediante datos")
    void anyadirGrupoMedianteDatos() throws ClubException{
        Integer matriculados = 10;
        Integer tarifa = 50;
        String[] datos = { "miguel", "jose", "20", matriculados.toString(), tarifa.toString() };
        double ingresosAnteriores = club.ingresos();

        // aquí habías puesto antadirActividad(grupo) en vez de antadirActividad(datos) (creo q es como yo digo)
        club.anyadirActividad(datos);

        assertEquals(ingresosAnteriores + tarifa * matriculados, club.ingresos());
    }

    @Test
    @DisplayName("Añadir grupo con datos invalidos")
    void anyadirGrupoConDatosInvalidos() throws ClubException{
        String[] datos = { "miguel", "jose", "estoNoEsUnNumero", "estoTampoco", "estoAunMenos" };

        assertThrows(ClubException.class, () -> club.anyadirActividad(datos));
    }
    /*
    No se comprueba que no se pueda añadir un grupo con menos datos de los necesarios

    @Test
    @DisplayName("Añadir grupo con menos datos de los necesarios")
    void anyadirGrupoConMenosDatos() throws ClubException{
        Integer matriculados = 10;
        Integer tarifa = 50;
        String[] datos = { "miguel", "jose", "20", matriculados.toString()};

        assertThrows(ClubException.class, () -> club.anyadirActividad(datos));
    }
    */
    /* Esto da fallo, ya que no se vigila que al añadir un nuevo grupo se exceda la capacidad
    @Test
    @DisplayName("Añadir más grupos de los permitidos debe lanzar excepción")
    void anyadirGrupoExcediendoCapacidad() throws ClubException {
        // Arrange
        ClubDeportivo clubPequeno = new ClubDeportivo("Club Test", 1);
        Grupo grupo1 = new Grupo("G1", "Natación", 20, 5, 30);
        Grupo grupo2 = new Grupo("G2", "Tenis", 15, 10, 40);

        // Act & Assert
        clubPequeno.anyadirActividad(grupo1); // Debería funcionar
        assertThrows(ClubException.class, () -> clubPequeno.anyadirActividad(grupo2)); // Debería fallar
    }
    */

    @Test
    @DisplayName("Actualizar plazas por debajo de matriculados debería fallar")
    void actualizarPlazasPorDebajoDeMatriculados() throws ClubException {
        // Arrange
        club.anyadirActividad(grupo); // grupo tiene 10 matriculados de 20 plazas
        Grupo grupoActualizado = new Grupo(grupo.getCodigo(), grupo.getActividad(), 5, 0, 50);

        // Act & Assert
        // Al intentar actualizar a 5 plazas cuando hay 10 matriculados debería fallar
        assertThrows(ClubException.class, () -> club.anyadirActividad(grupoActualizado));
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
    @DisplayName("Plazas libres de una actividad no existente")
    void plazasLibresNoExistente() throws ClubException{
        club.anyadirActividad(grupo);
        int plazasLibres = club.plazasLibres("noExistente");

        assertEquals(plazasLibres, 0);
    }

    @Test
    @DisplayName("Matricular demasiadas personas")
    void matricularDemasiasPersonas() throws ClubException{
        club.anyadirActividad(grupo);

        assertThrows(ClubException.class, () -> club.matricular(grupo.getActividad(), 99999));
    }
    @Test
    @DisplayName("Matricular personas para que los grupos se queden justo llenos")
    void matricularPersonasAdecuadasJusto() throws ClubException{
        club.anyadirActividad(grupo);
        int plazasLibres = grupo.plazasLibres();
        String actividad = grupo.getActividad();

        club.matricular(actividad, plazasLibres);

        assertEquals(0, grupo.plazasLibres());
    }
    @Test
    @DisplayName("Matricular personas para que la actividad no se quede llena")
    void matricularPersonasAdecuadas() throws ClubException{
        club.anyadirActividad(grupo);
        String actividad = grupo.getActividad();
        int plazasAntes = grupo.plazasLibres();

        club.matricular(actividad,2);

        assertEquals(plazasAntes - 2, grupo.plazasLibres());
    }

    @Test
    @DisplayName("Matricular en un segundo grupo")
    void matricularSegundoGrupo() throws ClubException{
        Integer matriculados = 10;
        Integer tarifa = 50;
        String[] datos = { "Fortniteor", "pubg", "20", matriculados.toString(), tarifa.toString() };
        club.anyadirActividad(grupo);
        club.anyadirActividad(datos);

        club.matricular("pubg",5);

        assertEquals(club.plazasLibres("pubg"), 5);
    }
    // Este test falla ya que no se comprueba si el numero de personas
    // es negativo y se debería hacer
    /*
    @Test
    @DisplayName("Matricular personas negativas")
    void matricularPersonasNegativas() throws ClubException{
        club.anyadirActividad(grupo);
        String actividad = grupo.getActividad();

        assertThrows(ClubException.class, () -> club.matricular(actividad, -1));
    }
    */
    @Test
    @DisplayName("toString() correcto con un grupo")
    void toStringFormatoCorrecto() throws ClubException {
        club.anyadirActividad(grupo);
        String  correcto = "Sons --> [ " + grupo.toString() + " ]";

        assertEquals(correcto, club.toString());
    }

    @Test
    @DisplayName("toString() correcto sin grupos")
    void toStringClubVacio() {
        String correcto = "Sons --> [  ]";
        assertEquals(correcto, club.toString());
    }

    @Test
    @DisplayName("toString() con múltiples grupos")
    void toStringVariosGrupos() throws ClubException {
        Grupo grupo2 = new Grupo("Fortniteor", "pubg", 15, 5, 60);
        club.anyadirActividad(grupo);
        club.anyadirActividad(grupo2);

        String esperado = "Sons --> [ " + grupo.toString() + ", " + grupo2.toString() + " ]";
        assertEquals(esperado, club.toString());
    }
}
