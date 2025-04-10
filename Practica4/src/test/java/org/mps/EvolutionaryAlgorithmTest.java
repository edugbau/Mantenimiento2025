package org.mps;

import org.junit.jupiter.api.BeforeEach;
import org.mps.crossover.TwoPointCrossover;
import org.mps.mutation.GaussianMutation;
import org.mps.selection.TournamentSelection;

public class EvolutionaryAlgorithmTest {
    EvolutionaryAlgorithm algorithm;

    @BeforeEach
    public void setUp() throws EvolutionaryAlgorithmException {
        TournamentSelection tournamentSelection = new TournamentSelection(10);
        GaussianMutation gaussianMutation = new GaussianMutation();
        TwoPointCrossover twoPointCrossover = new TwoPointCrossover();

        algorithm = new EvolutionaryAlgorithm(tournamentSelection, gaussianMutation, twoPointCrossover);
    }

    
}
