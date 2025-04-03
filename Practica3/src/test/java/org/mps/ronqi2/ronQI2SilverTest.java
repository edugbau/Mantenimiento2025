package org.mps.ronqi2;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mps.dispositivo.DispositivoSilver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ronQI2SilverTest {
    RonQI2Silver aparato;
    
    /*
     * Analiza con los caminos base qué pruebas se han de realizar para comprobar que al inicializar funciona como debe ser. 
     * El funcionamiento correcto es que si es posible conectar ambos sensores y configurarlos, 
     * el método inicializar de ronQI2 o sus subclases, 
     * debería devolver true. En cualquier otro caso false. Se deja programado un ejemplo.
     */
    @BeforeEach
    void init(){
        aparato  = new RonQI2Silver();
    }

    @DisplayName("Inicializar con dispositivo nulo da error")
    @Test
    public void inicializar_dispositivoNulo_daExcepcion(){
        //Arrange
        aparato.disp = null;
        //Act y Assert
        assertThrows(NullPointerException.class, () -> aparato.inicializar());
    }
    @Test
    public void inicializar_sensorPresionFalso_daFalso(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.conectarSensorPresion()).thenReturn(false);
        //Act
        boolean res = aparato.inicializar();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, never()).conectarSensorSonido();
        verify(aparato.disp, never()).configurarSensorPresion();
        verify(aparato.disp, never()).configurarSensorSonido();
    }

    @Test
    public void inicializar_sensorSonidoFalso_daFalso(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.conectarSensorPresion()).thenReturn(true);
        when(aparato.disp.conectarSensorSonido()).thenReturn(false);
        //Act
        boolean res = aparato.inicializar();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, times(1)).conectarSensorSonido();
        verify(aparato.disp, times(1)).configurarSensorPresion();
        verify(aparato.disp, never()).configurarSensorSonido();
    }

    @Test
    public void inicializar_configPresionFalso_daFalso(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.conectarSensorPresion()).thenReturn(true);
        when(aparato.disp.configurarSensorPresion()).thenReturn(false);
        when(aparato.disp.conectarSensorSonido()).thenReturn(true);
        when(aparato.disp.configurarSensorSonido()).thenReturn(true);
        //Act
        boolean res = aparato.inicializar();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, times(1)).conectarSensorSonido();
        verify(aparato.disp, times(1)).configurarSensorPresion();
        verify(aparato.disp, times(1)).configurarSensorSonido();
    }

    @Test
    public void inicializar_configSonidoFalso_daFalso(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.conectarSensorPresion()).thenReturn(true);
        when(aparato.disp.configurarSensorPresion()).thenReturn(true);
        when(aparato.disp.conectarSensorSonido()).thenReturn(true);
        when(aparato.disp.configurarSensorSonido()).thenReturn(false);
        //Act
        boolean res = aparato.inicializar();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, times(1)).conectarSensorSonido();
        verify(aparato.disp, times(1)).configurarSensorPresion();
        verify(aparato.disp, times(1)).configurarSensorSonido();
    }
    
    /*
     * Un inicializar debe configurar ambos sensores, comprueba que cuando se inicializa de forma correcta (el conectar es true), 
     * se llama una sola vez al configurar de cada sensor.
     */
    @DisplayName("Inicializar llama a los métodos de configuración una vez")
    @Test
    public void inicializar_llamaConfiguracionUnaVez(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.conectarSensorPresion()).thenReturn(true);
        when(aparato.disp.configurarSensorPresion()).thenReturn(true);
        when(aparato.disp.conectarSensorSonido()).thenReturn(true);
        when(aparato.disp.configurarSensorSonido()).thenReturn(true);
        //Act
        boolean res = aparato.inicializar();
        //Assert
        assertTrue(res);
        verify(aparato.disp, times(1)).configurarSensorPresion();
        verify(aparato.disp, times(1)).configurarSensorSonido();
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, times(1)).conectarSensorSonido();
    }

    /*
     * Un reconectar, comprueba si el dispositivo desconectado, en ese caso, conecta ambos y devuelve true si ambos han sido conectados. 
     * Genera las pruebas que estimes oportunas para comprobar su correcto funcionamiento. 
     * Centrate en probar si todo va bien, o si no, y si se llama a los métodos que deben ser llamados.
     */
    @DisplayName("Si el dispositivo está conectado, devuelve false")
    @Test
    public void reconectar_DispositivoConectado_daFalse(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.estaConectado()).thenReturn(true);
        //Act
        boolean res = aparato.reconectar();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).estaConectado();
        verify(aparato.disp, never()).conectarSensorPresion();
        verify(aparato.disp, never()).conectarSensorSonido();
    }
    @DisplayName("Si el dispositivo está desconectado y ambos sensores se conectan, devuelve true")
    @Test
    public void reconectar_DispositivoDesconectadoYSensoresConectan_daTrue(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.estaConectado()).thenReturn(false);
        when(aparato.disp.conectarSensorPresion()).thenReturn(true);
        when(aparato.disp.conectarSensorSonido()).thenReturn(true);
        //Act
        boolean res = aparato.reconectar();
        //Assert
        assertTrue(res);
        verify(aparato.disp, times(1)).estaConectado();
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, times(1)).conectarSensorSonido();
    }

    @DisplayName("Si el dispositivo está desconectado y falla conectar presión, devuelve false")
    @Test
    public void reconectar_DispositivoDesconectadoYFallaPresion_daFalse(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.estaConectado()).thenReturn(false);
        when(aparato.disp.conectarSensorPresion()).thenReturn(false);
        //Act
        boolean res = aparato.reconectar();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).estaConectado();
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, never()).conectarSensorSonido();
    }

    @DisplayName("Si el dispositivo está desconectado y falla conectar sonido, devuelve false")
    @Test
    public void reconectar_DispositivoDesconectadoYFallaSonido_daFalse(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.estaConectado()).thenReturn(false);
        when(aparato.disp.conectarSensorPresion()).thenReturn(true);
        when(aparato.disp.conectarSensorSonido()).thenReturn(false);
        //Act
        boolean res = aparato.reconectar();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).estaConectado();
        verify(aparato.disp, times(1)).conectarSensorPresion();
        verify(aparato.disp, times(1)).conectarSensorSonido();
    }



    @DisplayName("Cuando el dispositivo está conectado, estaConectado devuelve true")
    @Test
    public void estaConectado_DispositivoConectado_daTrue() {
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.estaConectado()).thenReturn(true);
        //Act
        boolean res = aparato.estaConectado();
        //Assert
        assertTrue(res);
        verify(aparato.disp, times(1)).estaConectado();
    }

    @DisplayName("Cuando el dispositivo está desconectado, estaConectado devuelve false")
    @Test
    public void estaConectado_DispositivoDesconectado_daFalse() {
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.estaConectado()).thenReturn(false);
        //Act
        boolean res = aparato.estaConectado();
        //Assert
        assertFalse(res);
        verify(aparato.disp, times(1)).estaConectado();
    }

    @DisplayName("Cuando el dispositivo es nulo, estaConectado lanza NullPointerException")
    @Test
    public void estaConectado_DispositivoNulo_daExcepcion() {
        //Arrange
        aparato.disp = null;
        //Act y Assert
        assertThrows(NullPointerException.class, () -> aparato.estaConectado());
    }

    @DisplayName("Añadir dispositivo asigna correctamente el dispositivo")
    @Test
    public void anyadirDispositivo_DispositivoValido_AsignaDispositivo() {
        //Arrange
        DispositivoSilver dispositivo = mock(DispositivoSilver.class);
        //Act
        aparato.anyadirDispositivo(dispositivo);
        //Assert
        assertSame(dispositivo, aparato.disp);
    }

    @DisplayName("Añadir dispositivo nulo permite la asignación")
    @Test
    public void anyadirDispositivo_DispositivoNulo_AsignaNulo() {
        //Arrange y Act
        aparato.anyadirDispositivo(null);
        //Assert
        assertNull(aparato.disp);
    }

    
    /*
     * El método evaluarApneaSuenyo, evalua las últimas 5 lecturas realizadas con obtenerNuevaLectura(), 
     * y si ambos sensores superan o son iguales a sus umbrales, que son thresholdP = 20.0f y thresholdS = 30.0f;, 
     * se considera que hay una apnea en proceso. Si hay menos de 5 lecturas también debería realizar la media.
     * /
     
     /* Realiza un primer test para ver que funciona bien independientemente del número de lecturas.
     * Usa el ParameterizedTest para realizar un número de lecturas previas a calcular si hay apnea o no (por ejemplo 4, 5 y 10 lecturas).
     * https://junit.org/junit5/docs/current/user-guide/index.html#writing-tests-parameterized-tests
     */

    @DisplayName("Obtener una nueva lectura añade las lecturas a los arrays correspondientes")
    @Test
    public void obtenerNuevaLectura_anyadeLecturas(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(15.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(20.0f);
        //Act
        aparato.obtenerNuevaLectura();
        //Assert
        verify(aparato.disp, times(1)).leerSensorPresion();;
        verify(aparato.disp, times(1)).leerSensorSonido();
    }

    @DisplayName("Si añades varias lecturas se añaden a los arrays")
    @Test
    public void obtenerNuevaLectura_anyadeVariasLecturas() {
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(15.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(20.0f);
        //Act
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        //Assert
        verify(aparato.disp, times(3)).leerSensorPresion();
        verify(aparato.disp, times(3)).leerSensorSonido();
    }

    @DisplayName("Si añades una nueva lectura se añade el valor correctamente y no sobrepasa el umbral")
    @Test
    public void evaluarApneaSuenyo_anayadeValorCorrecto_NoSobrepasaElUmbral(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(15.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(20.0f);
        aparato.obtenerNuevaLectura();
        boolean pasaElUmbral = false;

        //Act
        pasaElUmbral = aparato.evaluarApneaSuenyo();

        // Assert
        assertFalse(pasaElUmbral);
    }

    @DisplayName("Si añades una varias lecturas se añaden los valores correctamente y no sobrepasa el umbral")
    @Test
    public void evaluarApneaSuenyo_anayadeValoresCorrectos_NoSobrepasaElUmbral(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(15.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(20.0f);
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        boolean pasaElUmbral = false;

        //Act
        pasaElUmbral = aparato.evaluarApneaSuenyo();

        // Assert
        assertFalse(pasaElUmbral);
    }

    @DisplayName("Si añades lecturas que sobrepasan el umbral, evaluarApneaSuenyo devuelve false")
    @Test
    public void evaluarApneaSuenyo_anayadeValoresSobreUmbral_SobrepasaElUmbral(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(25.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(35.0f);
        aparato.obtenerNuevaLectura();
        boolean pasaElUmbral = false;

        //Act
        pasaElUmbral = aparato.evaluarApneaSuenyo();

        // Assert
        assertTrue(pasaElUmbral);
    }

    @DisplayName("Si añades mas de 5 lecturas, borra las primeras")
    @Test
    public void evaluarApneaSuenyo_anyadeMasDe5Lecturas_eliminaLasPrimeras(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(100.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(100.0f);
        aparato.obtenerNuevaLectura();
        when(aparato.disp.leerSensorPresion()).thenReturn(15.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(20.0f);
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        aparato.obtenerNuevaLectura();
        boolean pasaElUmbral = false;

        //Act
        pasaElUmbral = aparato.evaluarApneaSuenyo();

        // Assert
        assertFalse(pasaElUmbral);
    }
    @DisplayName("Si añades un valor de presion que no sobrepasa y el sonido si no sobrepasa el umbral")
    @Test
    public void evaluarApneaSuenyo_anayadeValorPresionNoSobrepasaYSonidoSobrepasa_NoSobrepasaElUmbral(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(15.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(100.0f);
        aparato.obtenerNuevaLectura();
        boolean pasaElUmbral = false;

        //Act
        pasaElUmbral = aparato.evaluarApneaSuenyo();

        // Assert
        assertFalse(pasaElUmbral);
    }
    @DisplayName("Si añades un valor de presion que sobrepasa y el sonido no no sobrepasa el umbral")
    @Test
    public void evaluarApneaSuenyo_anayadeValorPresionSobrepasaYSonidoNoSobrepasa_NoSobrepasaElUmbral(){
        //Arrange
        aparato.disp = mock(DispositivoSilver.class);
        when(aparato.disp.leerSensorPresion()).thenReturn(100.0f);
        when(aparato.disp.leerSensorSonido()).thenReturn(20.0f);
        aparato.obtenerNuevaLectura();
        boolean pasaElUmbral = false;

        //Act
        pasaElUmbral = aparato.evaluarApneaSuenyo();

        // Assert
        assertFalse(pasaElUmbral);
    }
}
