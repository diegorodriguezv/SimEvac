/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Física;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author diego
 */
public class PartículaTest {

    public PartículaTest() {
    }
    private Point2D.Double cero = new Point2D.Double(0, 0);

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of distanciaRecorrida method, of class Partícula.
     */
    @Test
    public void testDistanciaRecorrida() {
        System.out.println("distanciaRecorrida");
        long milisegundos = 1000L;
        Partícula instance = new Partícula();
        Double expResult = cero;
        Double result = instance.distanciaRecorrida(milisegundos);
        assertEquals(expResult, result);

        instance = new Partícula(cero, new Double(1, 1), cero);
        expResult = new Double(1, 1);
        result = instance.distanciaRecorrida(milisegundos);
        assertEquals(expResult, result);
    }

    /**
     * Test of nuevaVelocidad method, of class Partícula.
     */
    @Test
    public void testNuevaVelocidad() {
        System.out.println("nuevaVelocidad");
        long milisegundos = 1000L;
        Partícula instance = new Partícula();
        Double expResult = cero;
        Double result = instance.nuevaVelocidad(milisegundos);
        assertEquals(expResult, result);

        instance = new Partícula(cero, cero, new Double(1, 1));
        expResult = new Double(1, 1);
        result = instance.nuevaVelocidad(milisegundos);
        assertEquals(expResult, result);
    }

    /**
     * Test of actualizar method, of class Partícula.
     */
    @Test
    public void testActualizar() {
        System.out.println("actualizar");
        Point2D.Double aceleración, posiciónEsperada, velocidadEsperada, aceleraciónEsperada;

        long milisegundos = 1000L;
        aceleración = new Point2D.Double(1, 1);
        Partícula instance = new Partícula(cero, cero, aceleración);

        instance.actualizar(milisegundos);
        posiciónEsperada = new Double(1, 1);
        velocidadEsperada = new Double(1, 1);
        aceleraciónEsperada = aceleración;
        assertEquals(instance.getPosición(), posiciónEsperada);
        assertEquals(instance.getVelocidad(), velocidadEsperada);
        assertEquals(instance.getAceleración(), aceleraciónEsperada);

        instance.actualizar(milisegundos);
        posiciónEsperada = new Double(3, 3);
        velocidadEsperada = new Double(2, 2);
        aceleraciónEsperada = aceleración;
        assertEquals(instance.getPosición(), posiciónEsperada);
        assertEquals(instance.getVelocidad(), velocidadEsperada);
        assertEquals(instance.getAceleración(), aceleraciónEsperada);
    }

    @Test
    public void caídaLibre() {
        long deltaT = 10, transcurrido = 0;

        Point2D.Double posición, gravedad, posiciónEsperada, velocidadEsperada, aceleraciónEsperada;
        posición = new Point2D.Double(0, 10);
        gravedad = new Point2D.Double(0, -9.81);
        Partícula instance = new Partícula(posición, cero, gravedad);

        do {
            System.out.println(String.format("T: %1$6d Pos: %2$6.4f %3$6.4f Vel: %4$6.4f %5$6.4f",
                    transcurrido, instance.getPosición().x, instance.getPosición().y,
                    instance.getVelocidad().x, instance.getVelocidad().y));
            transcurrido += deltaT;
            instance.actualizar(deltaT);
        } while (instance.getPosición().y >= 0);
        System.out.println(String.format("T: %1$6d Pos: %2$6.4f %3$6.4f Vel: %4$6.4f %5$6.4f",
                transcurrido, instance.getPosición().x, instance.getPosición().y,
                instance.getVelocidad().x, instance.getVelocidad().y));

        // Debe caer 10m en 1.4s Vf=14m/s
        // ver: http://www.wolframalpha.com/input/?i=time+to+fall+10m        assertEquals(1400, transcurrido, 30);
        assertEquals(-14d, instance.getVelocidad().y, 0.03d);
    }

}
