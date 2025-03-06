package clubDeportivo;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class clubDeportivoAltoRendimientoTest
{
    ClubDeportivoAltoRendimiento clubAltoRendimiento;
    Grupo grupo;
    @BeforeEach
    void setUp() throws ClubException {
        clubAltoRendimiento = new ClubDeportivoAltoRendimiento("Sons",3,50,5);
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

    // Tests de constructores
    @Test
    @DisplayName("Creacion del grupo valida")
    void crearGrupoValido() throws ClubException {assertNotNull(clubAltoRendimiento);}

    @Test
    @DisplayName("Creacion del grupo valida solo nombre")
    void crearGrupoValidoSoloNombre() throws ClubException {assertNotNull(new ClubDeportivoAltoRendimiento("Mayans",40,5));}

    @Test
    @DisplayName("No se puede crear un grupo con un maximo no valido")
    void crearGrupoMaximoMal() throws ClubException {
        assertThrows(ClubException.class, () -> new ClubDeportivoAltoRendimiento("Sons",3,-5,5));
    }
    @Test
    @DisplayName("No se puede crear un grupo con solo nombre y un maximo no valido")
    void crearGrupoSoloNombreMaximoMal() throws ClubException {
        assertThrows(ClubException.class, () -> new ClubDeportivoAltoRendimiento("Sons",-5,5));
    }
    @Test
    @DisplayName("No se puede crear un grupo con un incremento no valido")
    void crearGrupoIncrementoMal() throws ClubException {
        assertThrows(ClubException.class, () -> new ClubDeportivoAltoRendimiento("Sons",3,40,-5));
    }
    @Test
    @DisplayName("No se puede crear un grupo con solo nombre y un incremento no valido")
    void crearGrupoSoloNombreIncrementoMal() throws ClubException {
        assertThrows(ClubException.class, () -> new ClubDeportivoAltoRendimiento("Sons",40,-5));
    }

    // Test de ingresos
    @Test
    @DisplayName("Los ingresos del club son correctos")
    void ingresosCorrectos() throws ClubException {
        ClubDeportivo club = new ClubDeportivo("Sons",3);

        clubAltoRendimiento.anyadirActividad(grupo);
        club.anyadirActividad(grupo);

        // el incremento es 5, de cuando hicimos el setup
        assertEquals(clubAltoRendimiento.ingresos(), club.ingresos()+club.ingresos()*(5.0/100));
    }

    // Tests de añadir actividad
    @Test
    @DisplayName("Actividad añadida con datos validos")
    void actividadCorrecta() throws ClubException{
        Integer matriculados = 10;
        Integer tarifa = 50;
        String[] datos = { "samcro", "Samcro", "20", matriculados.toString(), tarifa.toString() };

        assertDoesNotThrow(() -> clubAltoRendimiento.anyadirActividad(datos));
    }

    @Test
    @DisplayName("No se puede añadir una actividad con menos de 5 datos")
    void actividadMenosDeCincoDatos() throws ClubException{
        String[] datos = { "samcro", "Samcro", "20"};

        assertThrows(ClubException.class, () -> clubAltoRendimiento.anyadirActividad(datos));
    }


    @Test
    @DisplayName("Si las plazas son mayores que el maximo permitido, se asigna el maximo permitido")
    void actividadPlazasMayoresMaximo() throws ClubException{
        String[] datos = { "samcro", "Samcro", "80", "0", "50" };

        clubAltoRendimiento.anyadirActividad(datos);

        // Debe ser igual a 50 ya que eso significa que se ha bajado el número de plazas a 50
        assertEquals(clubAltoRendimiento.plazasLibres("Samcro"),50);
    }
    @Test
    @DisplayName("No se pueden añadir grupos con datos invalidos")
    void actividadConDatosInvalidos() throws ClubException{
        String[] datos = { "samcro", "Samcro", "Jax Teller", "Clay Morrow", "Opie Winston" };

        assertThrows(ClubException.class, () -> clubAltoRendimiento.anyadirActividad(datos));
    }
}
