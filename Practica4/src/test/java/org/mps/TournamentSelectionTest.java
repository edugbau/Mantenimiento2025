package org.mps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mps.selection.TournamentSelection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TournamentSelectionTest {
    TournamentSelection tournamentSelection;

    @BeforeEach
    public void init() throws EvolutionaryAlgorithmException {
        tournamentSelection = new TournamentSelection(5);
    }

    @DisplayName("Crear un torneo con tamaño 0 lanza excepcion")
    @Test
    public void constructor_TamañoCero_LanzaExcepcion(){
        assertThrows(EvolutionaryAlgorithmException.class, () ->
                new TournamentSelection(0)
        );
    }

    @DisplayName("Intentar la seleccion de una poblacion nula falla")
    @Test
    public void select_Nulo_LanzaExcepcion(){
        assertThrows(EvolutionaryAlgorithmException.class, () -> tournamentSelection.select(null));
    }

    @DisplayName("Intentar la seleccion de una poblacion con 0 caracteristicas falla")
    @Test
    public void select_PoblacionVacia_LanzaExcepcion(){
        int[] poblacion = {};
        assertThrows(EvolutionaryAlgorithmException.class, () -> tournamentSelection.select(poblacion));
    }

    @DisplayName("Intentar la seleccion de una poblacion con menos caracteristicas que el tamaño del torneo falla")
    @Test
    public void select_PoblacionMenorQueTorneo_LanzaExcepcion(){
        int[] poblacion = {1,2,3};
        assertThrows(EvolutionaryAlgorithmException.class, () -> tournamentSelection.select(poblacion));
    }

    @DisplayName("Hacer la seleccion con valores correctos funciona bien")
    @Test
    public void select_PoblacionCorrecta_Funciona() throws EvolutionaryAlgorithmException {
        // Arrange
        int[] poblacion = {1,2,3,4,5,6,7};
        int [] resultado;

        // Act
        resultado = tournamentSelection.select(poblacion);

        // Assert
        assertEquals(poblacion.length, resultado.length);
    }

    // no se puede probar cual muestra es la mayor porque
    // la muestra que escoge es de forma aleatoria
}
