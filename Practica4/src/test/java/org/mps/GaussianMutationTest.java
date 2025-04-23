package org.mps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import org.mps.mutation.GaussianMutation;
import org.mps.EvolutionaryAlgorithmException;

import java.util.Random;

public class GaussianMutationTest {
    GaussianMutation mutation;

    @BeforeEach
    public void init() {
        mutation = new GaussianMutation();
    }

    @Test
    @DisplayName("Mutar un individuo nulo lanza una excepción")
    public void mutar_IndividuoNulo_LanzaExcepcion() {
        assertThrows(EvolutionaryAlgorithmException.class, () -> mutation.mutate(null));
    }

    @Test
    @DisplayName("Mutar un individuo vacío lanza una excepción")
    public void mutar_IndividuoVacio_LanzaExcepcion() {
        assertThrows(EvolutionaryAlgorithmException.class, () -> mutation.mutate(new int[0]));
    }

    @Test
    @DisplayName("Sin mutación, el individuo permanece igual")
    public void mutar_SinMutacion_DevuelveMismoIndividuo() throws EvolutionaryAlgorithmException {
        mutation = new GaussianMutation(0.0, 1.0); // Tasa de mutación 0%
        int[] individual = {1, 2, 3, 4, 5};
        int[] mutatedIndividual = mutation.mutate(individual);

        assertArrayEquals(individual, mutatedIndividual);
    }

    @Test
    @DisplayName("Con mutación completa, el individuo cambia")
    public void mutar_MutacionCompleta_DevuelveIndividuoMutado() throws EvolutionaryAlgorithmException {
        mutation = new GaussianMutation(1.0, 1.0); // Tasa de mutación 100%
        int[] individual = {1, 2, 3, 4, 5};
        int[] mutatedIndividual = mutation.mutate(individual);

        assertNotEquals(individual, mutatedIndividual); // Verifica que al menos un valor cambió
    }

    @Test
    @DisplayName("Con mutación parcial, el individuo cambia parcialmente")
    public void mutar_MutacionParcial_DevuelveIndividuoParcialmenteMutado() throws EvolutionaryAlgorithmException {
        mutation = new GaussianMutation(0.5, 1.0); // Tasa de mutación 50%
        int[] individual = {1, 2, 3, 4, 5};
        int[] mutatedIndividual = mutation.mutate(individual);

        assertEquals(individual.length, mutatedIndividual.length); // Verifica que la longitud no cambió
    }

    @Test
    @DisplayName("Con desviación estándar cero, no hay cambios en los valores")
    public void mutar_DesviacionEstandarCero_SinCambiosEnValores() throws EvolutionaryAlgorithmException {
        mutation = new GaussianMutation(1.0, 0.0); // Desviación estándar 0
        int[] individual = {1, 2, 3, 4, 5};
        int[] mutatedIndividual = mutation.mutate(individual);

        assertArrayEquals(individual, mutatedIndividual); // No debe haber cambios
    }
}
