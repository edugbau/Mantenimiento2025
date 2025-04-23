package org.mps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mps.crossover.TwoPointCrossover;

import static org.junit.jupiter.api.Assertions.*;

public class TwoPointCrossoverTest {
    TwoPointCrossover twoPointCrossover;

    @BeforeEach
    public void init() {
        twoPointCrossover = new TwoPointCrossover();
    }

    @Test
    @DisplayName("Crossover con ambos padres nulos lanza excepción")
    public void crossover_AmbosPadresNulos_LanzaExcepcion() {
        // Arrange
        int[] parent1 = null;
        int[] parent2 = null;

        // Act & Assert
        assertThrows(EvolutionaryAlgorithmException.class, () ->
                twoPointCrossover.crossover(parent1, parent2)
        );
    }

    @Test
    @DisplayName("Crossover con un padre nulo lanza excepción")
    public void crossover_UnPadreNulo_LanzaExcepcion() {
        // Arrange
        int[] parent1 = {1, 2, 3};
        int[] parent2 = null;

        // Act & Assert
        assertThrows(EvolutionaryAlgorithmException.class, () ->
                twoPointCrossover.crossover(parent1, parent2)
        );
    }

    @Test
    @DisplayName("Crossover con padres de longitudes diferentes lanza excepción")
    public void crossover_PadresLongitudesDiferentes_LanzaExcepcion() {
        // Arrange
        int[] parent1 = {1, 2, 3};
        int[] parent2 = {4, 5};

        // Act & Assert
        assertThrows(EvolutionaryAlgorithmException.class, () ->
                twoPointCrossover.crossover(parent1, parent2)
        );
    }

    @Test
    @DisplayName("Crossover con padres de longitud menor o igual a uno lanza excepción")
    public void crossover_PadresLongitudMenorOIgualAUno_LanzaExcepcion() {
        // Arrange
        int[] parent1 = {1};
        int[] parent2 = {2};

        // Act & Assert
        assertThrows(EvolutionaryAlgorithmException.class, () ->
                twoPointCrossover.crossover(parent1, parent2)
        );
    }

    @Test
    @DisplayName("Crossover con padres válidos devuelve descendientes")
    public void crossover_PadresValidos_DevuelveDescendientes() throws EvolutionaryAlgorithmException {
        // Arrange
        int[] parent1 = {1, 2, 3, 4};
        int[] parent2 = {5, 6, 7, 8};

        // Act
        int[][] offspring = twoPointCrossover.crossover(parent1, parent2);

        // Assert
        assertArrayEquals(new int[parent1.length], new int[offspring[0].length]);
        assertArrayEquals(new int[parent2.length], new int[offspring[1].length]);
    }

    @Test
    @DisplayName("Crossover con padres válidos devuelve descendientes con segmentos intercambiados")
    public void crossover_PadresValidos_DevuelveDescendientesConSegmentosIntercambiados() throws Exception {
        // Arrange
        int[] parent1 = {1, 2, 3, 4, 5};
        int[] parent2 = {6, 7, 8, 9, 10};

        // Act
        int[][] offspring = twoPointCrossover.crossover(parent1, parent2);

        // Assert
        // Verificar que los descendientes tienen la misma longitud que los padres
        assertArrayEquals(new int[parent1.length], new int[offspring[0].length]);
        assertArrayEquals(new int[parent2.length], new int[offspring[1].length]);

        // Verificar que los descendientes contienen segmentos de ambos padres
        boolean contieneSegmentosDeAmbosPadres = false;
        for (int i = 0; i < parent1.length; i++) {
            if ((offspring[0][i] == parent2[i] || offspring[1][i] == parent1[i]) &&
                    (offspring[0][i] != parent1[i] || offspring[1][i] != parent2[i])) {
                contieneSegmentosDeAmbosPadres = true;
                break;
            }
        }
        assertTrue(contieneSegmentosDeAmbosPadres);
    }
}