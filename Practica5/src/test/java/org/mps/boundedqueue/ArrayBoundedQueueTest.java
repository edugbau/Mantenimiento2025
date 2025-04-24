package org.mps.boundedqueue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

public class ArrayBoundedQueueTest {

    @Test
    @DisplayName("Constructor: Crear una cola vacía con capacidad especificada")
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
    @DisplayName("Constructor: Lanza excepción al usar capacidad cero")
    void constructor_CapacidadCero_LanzaExcepcion() {
        // Arrange
        int capacidad = 0;

        // Act & Assert
        assertThatThrownBy(() -> new ArrayBoundedQueue<>(capacidad))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Constructor: Lanza excepción al usar capacidad negativa")
    void constructor_CapacidadNegativa_LanzaExcepcion() {
        // Arrange
        int capacidad = -1;

        // Act & Assert
        assertThatThrownBy(() -> new ArrayBoundedQueue<>(capacidad))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Put: Agregar un elemento válido a la cola")
    void put_ElementoValido_AgregaElementoALaCola() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act
        queue.put(10);

        // Assert
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.isEmpty()).isFalse();
        assertThat(queue.get()).isEqualTo(10);
    }

    @Test
    @DisplayName("Put: Lanza excepción al intentar agregar un elemento nulo")
    void put_ElementoNulo_LanzaExcepcion() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act & Assert
        assertThatThrownBy(() -> queue.put(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Put: Lanza excepción al intentar agregar un elemento a una cola llena")
    void put_ColaLlena_LanzaExcepcion() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(2);
        queue.put(1);
        queue.put(2);

        // Act & Assert
        assertThatThrownBy(() -> queue.put(3))
            .isInstanceOf(FullBoundedQueueException.class);
    }

    @Test
    @DisplayName("isFull: Devuelve false para una cola vacía")
    void isFull_ColaVacia_DevuelveFalse() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act
        boolean result = queue.isFull();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isFull: Devuelve false para una cola parcialmente llena")
    void isFull_ColaParcialmenteLlena_DevuelveFalse() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(1);

        // Act
        boolean result = queue.isFull();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isFull: Devuelve true para una cola completamente llena")
    void isFull_ColaLlena_DevuelveTrue() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(2);
        queue.put(1);
        queue.put(2);

        // Act
        boolean result = queue.isFull();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isEmpty: Devuelve true para una cola vacía")
    void isEmpty_ColaVacia_DevuelveTrue() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act
        boolean result = queue.isEmpty();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isEmpty: Devuelve false para una cola con elementos")
    void isEmpty_ColaConElementos_DevuelveFalse() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(1);

        // Act
        boolean result = queue.isEmpty();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("size: Devuelve 0 para una cola vacía")
    void size_ColaVacia_DevuelveCero() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act
        int result = queue.size();

        // Assert
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("size: Devuelve el número correcto de elementos en la cola")
    void size_ColaConElementos_DevuelveNumeroCorrecto() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(1);
        queue.put(2);

        // Act
        int result = queue.size();

        // Assert
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("size: Devuelve la capacidad máxima cuando la cola está llena")
    void size_ColaLlena_DevuelveCapacidadMaxima() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(1);
        queue.put(2);
        queue.put(3);

        // Act
        int result = queue.size();

        // Assert
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("getFirst: Devuelve 0 para una cola vacía")
    void getFirst_ColaVacia_DevuelveCero() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act
        int firstIndex = queue.getFirst();

        // Assert
        assertThat(firstIndex).isEqualTo(0);
    }

    @Test
    @DisplayName("getFirst: Devuelve el índice correcto después de agregar elementos")
    void getFirst_ColaConElementos_DevuelveIndiceCorrecto() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);
        queue.put(20);

        // Act
        int firstIndex = queue.getFirst();

        // Assert
        assertThat(firstIndex).isEqualTo(0);
    }

    @Test
    @DisplayName("getFirst: Devuelve el índice correcto después de eliminar elementos")
    void getFirst_EliminarElementos_DevuelveIndiceCorrecto() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);
        queue.put(20);
        queue.get(); // Elimina el primer elemento

        // Act
        int firstIndex = queue.getFirst();

        // Assert
        assertThat(firstIndex).isEqualTo(1);
    }

    @Test
    @DisplayName("getLast: Devuelve 0 para una cola vacía")
    void getLast_ColaVacia_DevuelveCero() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act
        int lastIndex = queue.getLast();

        // Assert
        assertThat(lastIndex).isEqualTo(0);
    }

    @Test
    @DisplayName("getLast: Devuelve el índice correcto después de agregar elementos")
    void getLast_ColaConElementos_DevuelveIndiceCorrecto() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);
        queue.put(20);

        // Act
        int lastIndex = queue.getLast();

        // Assert
        assertThat(lastIndex).isEqualTo(2);
    }

    @Test
    @DisplayName("getLast: Devuelve el índice correcto después de eliminar y agregar elementos")
    void getLast_EliminarYAgregarElementos_DevuelveIndiceCorrecto() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);
        queue.put(20);
        queue.get(); // Elimina el primer elemento
        queue.put(30);

        // Act
        int lastIndex = queue.getLast();

        // Assert
        assertThat(lastIndex).isEqualTo(0);
    }

    @Test
    @DisplayName("Iterator: Iterar sobre una cola vacía no tiene elementos")
    void iterator_ColaVacia_NoTieneElementos() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);

        // Act
        Iterator<Integer> iterator = queue.iterator();

        // Assert
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Iterator: Iterar sobre una cola con elementos devuelve los elementos en orden")
    void iterator_ColaConElementos_DevuelveElementosEnOrden() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);
        queue.put(20);
        queue.put(30);

        // Act
        Iterator<Integer> iterator = queue.iterator();

        // Assert
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(10);
        assertThat(iterator.next()).isEqualTo(20);
        assertThat(iterator.next()).isEqualTo(30);
        assertThat(iterator.hasNext()).isFalse();
    }


    @Test
    @DisplayName("Iterator: hasNext devuelve true cuando hay elementos por iterar")
    void iterator_HasNextConElementos_DevuelveTrue() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);
        queue.put(20);

        // Act
        Iterator<Integer> iterator = queue.iterator();

        // Assert
        assertThat(iterator.hasNext()).isTrue();
    }

    @Test
    @DisplayName("Iterator: hasNext devuelve false cuando no hay más elementos por iterar")
    void iterator_HasNextSinElementos_DevuelveFalse() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);

        // Act
        Iterator<Integer> iterator = queue.iterator();
        iterator.next(); // Consume el único elemento

        // Assert
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Iterator: next devuelve el siguiente elemento en la cola")
    void iterator_NextDevuelveElementoCorrecto() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);
        queue.put(20);

        // Act
        Iterator<Integer> iterator = queue.iterator();
        int firstElement = iterator.next();
        int secondElement = iterator.next();

        // Assert
        assertThat(firstElement).isEqualTo(10);
        assertThat(secondElement).isEqualTo(20);
    }

    @Test
    @DisplayName("Iterator: next lanza excepción cuando no hay más elementos")
    void iterator_NextSinElementos_LanzaExcepcion() {
        // Arrange
        ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(3);
        queue.put(10);

        // Act
        Iterator<Integer> iterator = queue.iterator();
        iterator.next(); // Consume el único elemento

        // Assert
        assertThatThrownBy(iterator::next)
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("next: bounded queue iterator exhausted");
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
