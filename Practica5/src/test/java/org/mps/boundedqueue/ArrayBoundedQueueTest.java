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

    // GET
    @Test
    void get_ColaVacia_LanzaExcepcion() {
        // Arrange
        int capacidad = 3;
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);
        // La cola está vacía por el @BeforeEach

        // Act & Assert
        assertThatThrownBy(() -> queue.get())
                .isInstanceOf(EmptyBoundedQueueException.class);
    }

    @Test
    void get_ColaConUnElemento_DevuelveElementoYColaVacia() {
        // Arrange
        int capacidad = 3;
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);
        Integer element = 10;
        queue.put(element);

        // Act
        Integer result = queue.get();

        // Assert
        assertThat(result).isEqualTo(element);
        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.isEmpty()).isTrue();
        assertThat(queue.getFirst()).isEqualTo(1); // first avanza
        assertThat(queue.getLast()).isEqualTo(1); // nextFree no cambia
    }

    @Test
    void get_ColaConVariosElementos_DevuelvePrimerElementoYActualizaIndices() {
        // Arrange
        int capacidad = 3;
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);
        Integer element1 = 10;
        Integer element2 = 20;
        queue.put(element1);
        queue.put(element2); // queue: [10, 20, null], first=0, nextFree=2, size=2

        // Act
        Integer result = queue.get(); // Debe devolver 10

        // Assert
        assertThat(result).isEqualTo(element1);
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.isEmpty()).isFalse();
        assertThat(queue.isFull()).isFalse();
        assertThat(queue.getFirst()).isEqualTo(1); // first avanza a 1
        assertThat(queue.getLast()).isEqualTo(2); // nextFree se mantiene en 2
    }

    @Test
    void get_ColaLlena_DevuelvePrimerElementoYActualizaIndices() {
        // Arrange
        int capacidad = 3;
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);
        queue.put(10);
        queue.put(20);
        queue.put(30); // queue: [10, 20, 30], first=0, nextFree=0, size=3 (llena)

        // Act
        Integer result = queue.get(); // Devuelve 10

        // Assert
        assertThat(result).isEqualTo(10);
        assertThat(queue.size()).isEqualTo(2);
        assertThat(queue.isFull()).isFalse();
        assertThat(queue.getFirst()).isEqualTo(1); // first avanza a 1
        assertThat(queue.getLast()).isEqualTo(0); // nextFree se mantiene en 0
    }

    @Test
    void get_ColaConDosElementosTrasGet_DevuelveSegundoElementoYActualizaIndices() {
        // Arrange
        int capacidad = 3;
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);
        queue.put(10);
        queue.put(20);
        queue.put(30);
        queue.get(); // queue: [null, 20, 30], first=1, nextFree=0, size=2

        // Act
        Integer result = queue.get(); // Devuelve 20

        // Assert
        assertThat(result).isEqualTo(20);
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.getFirst()).isEqualTo(2); // first avanza a 2
        assertThat(queue.getLast()).isEqualTo(0); // nextFree se mantiene en 0
    }

    @Test
    void get_ColaConWrapAroundTrasPut_DevuelveTercerElementoOriginalYActualizaIndices() {
        // Arrange
        int capacidad = 3;
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);
        queue.put(10);
        queue.put(20);
        queue.put(30);
        queue.get(); // queue: [null, 20, 30], first=1, nextFree=0, size=2
        queue.get(); // queue: [null, null, 30], first=2, nextFree=0, size=1
        queue.put(40); // queue: [40, null, 30], first=2, nextFree=1, size=2

        // Act
        Integer result = queue.get(); // Devuelve 30

        // Assert
        assertThat(result).isEqualTo(30);
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.getFirst()).isEqualTo(0); // first avanza a 0 (wrap around)
        assertThat(queue.getLast()).isEqualTo(1); // nextFree se mantiene en 1
    }

    @Test
    void get_ColaConUnElementoTrasWrapAround_DevuelveUltimoElementoYColaVacia() {
        // Arrange
        int capacidad = 3;
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(capacidad);
        queue.put(10);
        queue.put(20);
        queue.put(30);
        queue.get(); // queue: [null, 20, 30], first=1, nextFree=0, size=2
        queue.get(); // queue: [null, null, 30], first=2, nextFree=0, size=1
        queue.put(40); // queue: [40, null, 30], first=2, nextFree=1, size=2
        queue.get(); // queue: [40, null, null], first=0, nextFree=1, size=1

        // Act
        Integer result = queue.get(); // Devuelve 40

        // Assert
        assertThat(result).isEqualTo(40);
        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.isEmpty()).isTrue();
        assertThat(queue.getFirst()).isEqualTo(1); // first avanza a 1
        assertThat(queue.getLast()).isEqualTo(1); // nextFree se mantiene en 1
    }
}
