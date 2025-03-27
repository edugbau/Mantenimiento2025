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
import java.util.List;

@DisplayName("Pruebas Exhaustivas de Árbol de Búsqueda Binario")
class BinarySearchTreeTest {

    private BinarySearchTree<Integer> arbolEnteros;

    @BeforeEach
    void setUp() {
        arbolEnteros = new BinarySearchTree<Integer>(Comparator.naturalOrder());
    }

    @Test
    @DisplayName("Árbol recién creado debe estar vacío")
    void render_arbolVacio_devuelveCadenaVacia() {
        // Act
        String resultado = arbolEnteros.render();

        // Assert
        assertEquals("", resultado);
    }

    @Test
    @DisplayName("Árbol con un solo elemento")
    void render_unElemento_devuelveElemento() {
        // Arrange
        arbolEnteros.insert(5);

        // Act
        String resultado = arbolEnteros.render();

        // Assert
        assertEquals("5", resultado);
    }

    @Test
    @DisplayName("Árbol con elemento solo a la izquierda")
    void render_elementoIzquierda_muestraParentesisCorrectamente() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);

        // Act
        String resultado = arbolEnteros.render();

        // Assert
        assertEquals("5(3,)", resultado);
    }

    @Test
    @DisplayName("Árbol con elemento solo a la derecha")
    void render_elementoDerecha_muestraParentesisCorrectamente() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(7);

        // Act
        String resultado = arbolEnteros.render();

        // Assert
        assertEquals("5(,7)", resultado);
    }

    @Test
    @DisplayName("Árbol con elementos en ambos lados")
    void render_elementosAmbosLados_muestraEstructuraCompleta() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act
        String resultado = arbolEnteros.render();

        // Assert
        assertEquals("5(3,7)", resultado);
    }

    @Test
    @DisplayName("Árbol con múltiples niveles")
    void render_multiplesNiveles_muestraEstructuraAnidada() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);
        arbolEnteros.insert(2);
        arbolEnteros.insert(8);

        // Act
        String resultado = arbolEnteros.render();

        // Assert
        assertEquals("5(3(2,),7(,8))", resultado);
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
    void contains_ArbolVacio_daFalso() {
        // Act
        boolean containsResult = arbolEnteros.contains(5);

        // Assert
        assertFalse(containsResult);
    }

    @Test
    @DisplayName("Buscar elemento existente debe devolver verdadero")
    void contains_ElementoExistente_daTrue() {
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
    void contains_ElementoNoExistente_daFalse() {
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
    void contains_ValorNulo_daExcepcion() {
        // Act & Assert
        assertThrows(BinarySearchTreeException.class, () -> {
            arbolEnteros.contains(null);
        });
    }

    @Test
    @DisplayName("Buscar elemento nulo en árbol con elementos")
    void contains_valorNuloArbolConElementos_lanzaExcepcion() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);

        // Act & Assert
        assertThrows(BinarySearchTreeException.class, () -> {
            arbolEnteros.contains(null);
        });
    }

    @Test
    @DisplayName("Buscar en subárbol izquierdo profundo")
    void contains_elementoEnSubarbolIzquierdo_devuelveVerdadero() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        arbolEnteros.insert(1);

        // Act
        boolean resultado = arbolEnteros.contains(1);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Buscar en subárbol derecho profundo")
    void contains_elementoEnSubarbolDerecho_devuelveVerdadero() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(15);
        arbolEnteros.insert(20);
        arbolEnteros.insert(25);

        // Act
        boolean resultado = arbolEnteros.contains(25);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Buscar elemento duplicado")
    void contains_elementoDuplicado_devuelveVerdadero() {
        // Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(5);

        // Act
        boolean resultado = arbolEnteros.contains(5);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Buscar elemento menor que todos")
    void contains_elementoMenorQueTodos_devuelveFalso() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);
        arbolEnteros.insert(15);

        // Act
        boolean resultado = arbolEnteros.contains(1);

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Buscar elemento mayor que todos")
    void contains_elementoMayorQueTodos_devuelveFalso() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);
        arbolEnteros.insert(15);

        // Act
        boolean resultado = arbolEnteros.contains(20);

        // Assert
        assertFalse(resultado);
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
    @DisplayName("Arbol con elemento a la izquierda no es hoja")
    public void isLeaf_arbolIzquierda_daFalse() {
        //Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);

        //Act
        boolean isLeaf = arbolEnteros.isLeaf();

        //Assert
        assertFalse(isLeaf);
    }

    @Test
    @DisplayName("Arbol con elemento a la derecha no es hoja")
    public void isLeaf_arbolDerecha_daFalse() {
        //Arrange
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);
        //Act
        boolean isLeaf = arbolEnteros.isLeaf();
        //Assert
        assertFalse(isLeaf);

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
    @DisplayName("Minimo con arbol vacío lanza excepción")
    public void minimo_arbolVacio_daExcepcion(){
        assertThrows(BinarySearchTreeException.class, () -> {
            arbolEnteros.minimum();
        });
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
    @DisplayName("Maximo con arbol vacío lanza excepción")
    public void maximo_arbolVacio_daExcepcion(){
        assertThrows(BinarySearchTreeException.class, () -> {
            arbolEnteros.maximum();
        });
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
    @DisplayName("Eliminar raíz de árbol con un solo elemento")
    void removeBranch_raizUnica_dejaArbolVacio() {
        // Arrange
        arbolEnteros.insert(5);

        // Act
        arbolEnteros.removeBranch(5);

        // Assert
        assertEquals("", arbolEnteros.render());
    }

    @Test
    @DisplayName("Eliminar nodo hoja izquierdo")
    void removeBranch_nodoHojaIzquierdo_eliminaCorrectamente() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);
        arbolEnteros.insert(15);

        // Act
        arbolEnteros.removeBranch(5);

        // Assert
        assertEquals("10(,15)", arbolEnteros.render());
    }

    @Test
    @DisplayName("Eliminar nodo hoja derecho")
    void removeBranch_nodoHojaDerecho_eliminaCorrectamente() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);
        arbolEnteros.insert(15);

        // Act
        arbolEnteros.removeBranch(15);

        // Assert
        assertEquals("10(5,)", arbolEnteros.render());
    }

    @Test
    @DisplayName("Eliminar subárbol con múltiples niveles")
    void removeBranch_subarbolMultinivel_eliminaTodaLaRama() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);
        arbolEnteros.insert(15);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act
        arbolEnteros.removeBranch(5);

        // Assert
        assertEquals("10(,15)", arbolEnteros.render());
    }

    @Test
    @DisplayName("Eliminar raíz de árbol complejo")
    void removeBranch_raizArbolComplejo_eliminaTodo() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);
        arbolEnteros.insert(15);
        arbolEnteros.insert(3);
        arbolEnteros.insert(7);

        // Act
        arbolEnteros.removeBranch(10);

        // Assert
        assertEquals("", arbolEnteros.render());
    }

    @Test
    @DisplayName("Eliminar nodo con valores duplicados")
    void removeBranch_nodoDuplicado_eliminaCorrectamente() {
        // Arrange
        arbolEnteros.insert(10);
        arbolEnteros.insert(10);
        arbolEnteros.insert(5);

        // Act
        arbolEnteros.removeBranch(10);

        // Assert
        assertEquals("", arbolEnteros.render());
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
    @Test
    @DisplayName("inOrder de árbol vacío devuelve una lista vacía")
    public void inOrder_arbolVacio_daListaVacia(){
        //Act
        List<Integer> lista = arbolEnteros.inOrder();
        //Assert
        assertEquals(0, lista.size());
    }
    @Test
    @DisplayName("inOrder con un solo elemento devuelve solo la lista con el elemento")
    public void inOrder_hoja_daSoloUnElemento(){
        //Arrange
        arbolEnteros.insert(5);
        //Act
        List<Integer> lista = arbolEnteros.inOrder();
        //Assert
        assertEquals(List.of(5), lista);
    }
    @Test
    @DisplayName("inOrder con dos elementos los devuelve en orden")
    public void inOrder_dosElementos_daEnOrden(){
        //Arrange
        arbolEnteros.insert(5);
        arbolEnteros.insert(3);
        //Act
        List<Integer> lista = arbolEnteros.inOrder();
        //Assert
        assertEquals(List.of(3,5), lista);
    }
    @Test
    @DisplayName("inOrder con dos elementos los devuelve en orden")
    public void inOrder_dosElementos_daEnOrden2(){
        //Arrange
        arbolEnteros.insert(3);
        arbolEnteros.insert(5);
        //Act
        List<Integer> lista = arbolEnteros.inOrder();
        //Assert
        assertEquals(List.of(3,5), lista);
    }
    @Test
    @DisplayName("inOrder con elementos anidados los da en orden")
    public void inOrder_elementosAnidados_daEnOrden(){
        //Arrange
        arbolEnteros.insert(3);
        arbolEnteros.insert(5);
        arbolEnteros.insert(4);
        //Act
        List<Integer> lista = arbolEnteros.inOrder();
        //Assert
        assertEquals(List.of(3,4,5), lista);
    }
}