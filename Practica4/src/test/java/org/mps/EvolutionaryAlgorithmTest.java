package org.mps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mps.crossover.CrossoverOperator;
import org.mps.crossover.TwoPointCrossover;
import org.mps.mutation.GaussianMutation;
import org.mps.mutation.MutationOperator;
import org.mps.selection.SelectionOperator;
import org.mps.selection.TournamentSelection;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EvolutionaryAlgorithmTest {
    EvolutionaryAlgorithm algorithm;

    @BeforeEach
    public void setUp() throws EvolutionaryAlgorithmException {
        TournamentSelection tournamentSelection = new TournamentSelection(10);
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
}
