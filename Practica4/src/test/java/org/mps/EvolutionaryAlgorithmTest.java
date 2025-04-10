package org.mps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mps.crossover.CrossoverOperator;
import org.mps.crossover.TwoPointCrossover;
import org.mps.mutation.GaussianMutation;
import org.mps.mutation.MutationOperator;
import org.mps.selection.SelectionOperator;
import org.mps.selection.TournamentSelection;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EvolutionaryAlgorithmTest {
    EvolutionaryAlgorithm algorithm;

    @BeforeEach
    public void setUp() throws EvolutionaryAlgorithmException {
        TournamentSelection tournamentSelection = new TournamentSelection(5);
        GaussianMutation gaussianMutation = new GaussianMutation();
        TwoPointCrossover twoPointCrossover = new TwoPointCrossover();

        algorithm = new EvolutionaryAlgorithm(tournamentSelection, gaussianMutation, twoPointCrossover);
    }


    @Test
    public void constructor_SelectionOperatorNulo_DaExcepcion() {
        MutationOperator mutationOperator = individual -> individual; // Mock simple
        CrossoverOperator crossoverOperator = (parent1, parent2) -> new int[][]{parent1, parent2}; // Mock simple

        assertThrows(EvolutionaryAlgorithmException.class, () ->
                new EvolutionaryAlgorithm(null, mutationOperator, crossoverOperator)
        );
    }

    @Test
    public void constructor_MutationOperatorNulo_DaExcepcion() {
        SelectionOperator selectionOperator = population -> population; // Mock simple
        CrossoverOperator crossoverOperator = (parent1, parent2) -> new int[][]{parent1, parent2}; // Mock simple

        assertThrows(EvolutionaryAlgorithmException.class, () ->
                new EvolutionaryAlgorithm(selectionOperator, null, crossoverOperator)
        );
    }

    @Test
    public void constructor_CrossoverOperatorNulo_DaExcepcion() {
        SelectionOperator selectionOperator = population -> population; // Mock simple
        MutationOperator mutationOperator = individual -> individual; // Mock simple

        assertThrows(EvolutionaryAlgorithmException.class, () ->
                new EvolutionaryAlgorithm(selectionOperator, mutationOperator, null)
        );
    }

    @Test
    public void constructor_TodosLosOperadoresNulos_DaExcepcion() {
        assertThrows(EvolutionaryAlgorithmException.class, () ->
                new EvolutionaryAlgorithm(null, null, null)
        );
    }

    @DisplayName("El metodo se ejecuta correctamente con una poblacion valida")
    @Test
    public void optimize_PoblacionValida_DevuelvePoblacion () throws EvolutionaryAlgorithmException {
        // Arrange
        int[][] poblacion = {
                {1, 2, 3,4,5,6},
                {7,8,9,10,11,12}
        };
        int[][] resultado;

        // Act
        resultado = algorithm.optimize(poblacion);

        // Assert
        assertEquals(resultado.length, poblacion.length);
    }

    @DisplayName("El metodo se ejecuta correctamente con una poblacion valida de mas de dos individuos")
    @Test
    public void optimize_PoblacionValidaDeMuchosIndividuos_DevuelvePoblacion () throws EvolutionaryAlgorithmException {
        // Arrange
        int[][] poblacion = {
                {1, 2, 3,4,5,6},
                {7,8,9,10,11,12},
                {10, 20, 30,40,50,60},
                {70,80,90,100,101,102}
        };
        int[][] resultado;

        // Act
        resultado = algorithm.optimize(poblacion);

        // Assert
        assertEquals(resultado.length, poblacion.length);
    }

    @DisplayName("Si la poblacion es nula y lanza una excepcion")
    @Test
    public void optimiza_PoblacionNula_LanzaExcepcion(){
        // Arrange
        int [][] poblacion = null;
        assertThrows(EvolutionaryAlgorithmException.class, () -> algorithm.optimize(poblacion));
    }

    @DisplayName("Si la poblacion es vacia y lanza una excepcion")
    @Test
    public void optimiza_PoblacionVacia_LanzaExcepcion(){
        int [][] poblacion = {};
        assertThrows(EvolutionaryAlgorithmException.class, () -> algorithm.optimize(poblacion));
    }

    @DisplayName("Si la poblacion es impar lanza excepcion")
    @Test
    public void  optimiza_PoblacionImpar_LanzaExcepcion(){
        int[][] poblacion = {
                {1, 2, 3,4,5,6},
                {7,8,9,10,11,12},
                {13,14,15,16,17,18}
        };
        assertThrows(EvolutionaryAlgorithmException.class, () -> algorithm.optimize(poblacion));
    }


}
