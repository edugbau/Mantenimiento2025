package es.taw.primerparcial.controller.UnitTest;

import es.taw.primerparcial.controller.SeleccionController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeleccionControllerUnitTest {

    @Test
    @DisplayName("seleccion() debería devolver el nombre de la vista 'seleccion'")
    public void seleccion_invocado_devuelveNombreVistaSeleccion() {
        SeleccionController controller = new SeleccionController();
        String viewName = controller.seleccion();
        assertEquals("seleccion", viewName);
    }
} 