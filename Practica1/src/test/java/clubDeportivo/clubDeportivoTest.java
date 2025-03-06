package clubDeportivo;

import clubdeportivo.ClubDeportivo;
import clubdeportivo.ClubException;
import clubdeportivo.Grupo;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class clubDeportivoTest {
    ClubDeportivo club;
    @BeforeEach
    void setUp() throws ClubException {
        club = new ClubDeportivo("Sons",3);
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
    @DisplayName("Añadir grupo nuevo")
    void anyadirGrupoNuevo() throws ClubException {
        //Arrange
        String[] datos = {"Samcro", "Motociclismo", "2", "8", "1000"};
        // String[] datos1 = {"Sambel", "Motociclismo", "5", "5", "1500"};

        //Act
        club.anyadirActividad(datos);
        //club.anyadirActividad(datos1);

        //Assert

    }
}
