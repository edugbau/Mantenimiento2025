/*
Integrantes:
Eduardo González Bautista
Juan Manuel Valenzuela González
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;

@DisplayName("Pruebas Exhaustivas de Árbol de Búsqueda Binario")
class BinarySearchTreeTest {

    private BinarySearchTree<Integer> arbolEnteros;

    @BeforeEach
    void setUp() {
        arbolEnteros = new BinarySearchTree<Integer>(Comparator.naturalOrder());
    }

    @Test
    @DisplayName("Crear árbol con comparador nulo debe lanzar excepción")
    void crearArbolConComparadorNulo_DaExcepcion() {
        // Act & Assert
        assertThrows(BinarySearchTreeException.class, () -> {
            new BinarySearchTree<Integer>(null);
        });
    }

    @Test
    @DisplayName("Árbol recién creado debe estar vacío")
    void constructor_daArbolVacio() {
        // Act
        String renderResult = arbolEnteros.render();

        // Assert
        assertEquals(renderResult,"");
    }

    @Test
    @DisplayName("Insertar primer elemento en árbol vacío")
    void insertarPrimerElemento_arbolVacio_funciona() {
        // Act
        arbolEnteros.insert(5);

        // Assert
        assertEquals("5", arbolEnteros.render());
    }

    @Test
    @DisplayName("Insertar múltiples elementos respetando orden")
    void insertar_MultiplesElementos_funcionaBien() {
        // Act
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Assert
        assertEquals("5(3,7)", arbolEnteros.render());
    }

    @Test
    @DisplayName("Insertar elementos duplicados van a la derecha")
    void insertar_ElementosDuplicados_vaALaDerecha() {
        // Act
        arbolEnteros.insert(5);
        arbolEnteros.insert(5);

        // Assert
        assertEquals("5(,5)", arbolEnteros.render());
    }

    @Test
    @DisplayName("Insertar elemento nulo da error")
    void insertar_ElementosNulo_daExcepcion() {
        // Act & Assert
        assertThrows(BinarySearchTreeException.class, () -> {
            arbolEnteros.insert(null);
        });
    }

    @Test
    @DisplayName("Buscar elemento en árbol vacío debe devolver falso")
    void buscar_ArbolVacio_daFalso() {
        // Act
        boolean containsResult = arbolEnteros.contains(5);

        // Assert
        assertFalse(containsResult);
    }

    @Test
    @DisplayName("Buscar elemento existente debe devolver verdadero")
    void buscar_ElementoExistente_daTrue() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act & Assert
        assertTrue(arbolEnteros.contains(5));
        assertTrue(arbolEnteros.contains(3));
        assertTrue(arbolEnteros.contains(7));
    }

    @Test
    @DisplayName("Buscar elemento no existente debe devolver falso")
    void buscar_ElementoNoExistente_daFalse() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);

        // Act
        boolean containsResult = arbolEnteros.contains(7);

        // Assert
        assertFalse(containsResult);
    }

    @Test
    @DisplayName("Buscar con valor nulo debe lanzar excepción")
    void buscar_ValorNulo_daExcepcion() {
        // Act & Assert
        assertThrows(BinarySearchTreeException.class, () -> {
            arbolEnteros.contains(null);
        });
    }

    @Test
    @DisplayName("Árbol vacío no es hoja")
    void isLeaf_arbolVacio_daFalse() {
        // Act
        boolean isLeaf = arbolEnteros.isLeaf();

        // Assert
        assertFalse(isLeaf);
    }

    @Test
    @DisplayName("Árbol con un solo elemento es hoja")
    void isLeaf_unElemento_daTrue() {
        // Arrange
        arbolEnteros.insert(5);

        // Act
        boolean isLeaf = arbolEnteros.isLeaf();

        // Assert
        assertTrue(isLeaf);
    }

    @Test
    @DisplayName("Árbol con más de un elemento no es hoja")
    void arbol_ConVariosElementos_NoEsHoja() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);

        // Act
        boolean isLeaf = arbolEnteros.isLeaf();

        // Assert
        assertFalse(isLeaf);
    }

    @Test
    @DisplayName("Obtener mínimo en árbol con un elemento")
    void minimo_unElemento_loDevuelve() {
        // Arrange
        arbolEnteros.insert(5);

        // Act
        int minimum = arbolEnteros.minimum();

        // Assert
        assertEquals(5, minimum);
    }

    @Test
    @DisplayName("Obtener mínimo en árbol con múltiples elementos")
    void minimo_variosElementos_devuelveMinimo() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act
        int minimum = arbolEnteros.minimum();

        // Assert
        assertEquals(3, minimum);
    }

    @Test
    @DisplayName("Obtener máximo en árbol con un elemento")
    void maximo_unElemento_loDevuelve() {
        // Arrange
        arbolEnteros.insert(5);

        // Act
        int maximum = arbolEnteros.maximum();

        // Assert
        assertEquals(5, maximum);
    }

    @Test
    @DisplayName("Obtener máximo en árbol con múltiples elementos")
    void maximo_variosElementos_devuelveMaximo() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act
        int maximum = arbolEnteros.maximum();

        // Assert
        assertEquals(7, maximum);
    }
    @Test
    @DisplayName("Eliminar elemento nulo lanza excepcion")
    void eliminar_ElementoNulo_daExcepcion() {
        assertThrows(BinarySearchTreeException.class, () -> {arbolEnteros.removeBranch(null);});
    }
    @Test
    @DisplayName("Eliminar rama de elemento existente")
    void eliminar_RamaElementoExistente_funciona() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act
        arbolEnteros.removeBranch(3);

        // Assert
        assertEquals("5(,7)", arbolEnteros.render());
    }

    @Test
    @DisplayName("Eliminar rama de elemento no existente lanza excepción")
    void eliminarRama_ElementoNoExistente_daExcepcion() {
        // Arrange
        arbolEnteros.insert(5);

        // Act & Assert
        assertThrows(BinarySearchTreeException.class, () -> {
            arbolEnteros.removeBranch(10);
        });
    }

    @Test
    @DisplayName("Tamaño de árbol vacío")
    void tamaño_ArbolVacio_da0() {
        // Act
        int size = arbolEnteros.size();

        // Assert
        assertEquals(0, size);
    }

    @Test
    @DisplayName("Tamaño de árbol con elementos")
    void tamaño_ArbolConElementos_funciona() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act
        int size = arbolEnteros.size();

        // Assert
        assertEquals(3, size);
    }

    @Test
    @DisplayName("Profundidad de árbol vacío")
    void profundidad_ArbolVacio_da0() {
        // Act
        int depth = arbolEnteros.depth();

        // Assert
        assertEquals(0, depth);
    }

    @Test
    @DisplayName("Profundidad de árbol con elementos")
    void profundidad_ArbolConElementos_funciona() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);
        arbolEnteros.insert(1);

        // Act
        int depth = arbolEnteros.depth();

        // Assert
        assertEquals(3, depth);
    }
}