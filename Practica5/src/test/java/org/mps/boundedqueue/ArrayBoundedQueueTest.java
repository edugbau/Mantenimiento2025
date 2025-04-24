package org.mps.boundedqueue;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ArrayBoundedQueueTest {
    //TODO: put, get, isFull, isEmpty, size, getFirst, getLast, iterator, hasNext, next

    @Test
    void constructor_CapacidadEspecificada_CreaColaVacia() {
        // Arrange
        int capacidad = 5;

        // Act
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);

        // Assert
        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.isEmpty()).isTrue();
        assertThat(queue.isFull()).isFalse();
    }

    @Test
    void constructor_CapacidadCero_LanzaExcepcion() {
        // Arrange
        int capacidad = 0;

        // Act & Assert
        assertThatThrownBy(() -> new ArrayBoundedQueue<>(capacidad))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ArrayBoundedException: capacity must be positive");
    }

    @Test
    void constructor_CapacidadNegativa_LanzaExcepcion() {
        // Arrange
        int capacidad = -1;

        // Act & Assert
        assertThatThrownBy(() -> new ArrayBoundedQueue<>(capacidad))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ArrayBoundedException: capacity must be positive");
    }
}
