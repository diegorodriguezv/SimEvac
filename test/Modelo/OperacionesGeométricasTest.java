/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Física.OperacionesGeométricas;
import java.awt.geom.Point2D;
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
public class OperacionesGeométricasTest {

    public OperacionesGeométricasTest() {
    }

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
     * Test of moverPunto method, of class OperacionesGeométricas.
     */
    @Test
    public void testMoverPunto() {
        System.out.println("MoverPunto");

        // Mover distancia y ángulo 0
        Point2D.Double punto = new Point2D.Double(3, 3);
        double ángulo = 0.0F;
        double distancia = 0.0F;
        Point2D.Double expResult = punto;
        Point2D.Double result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        assertEquals(expResult, result);

        //Puntos Cardinales

        ángulo = Math.toRadians(0);
        distancia = 1;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(4, 3);
        assertEquals(expResult, result);

        ángulo = Math.toRadians(90);
        distancia = 1;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(3, 4);
        assertEquals(expResult, result);

        ángulo = Math.toRadians(180);
        distancia = 1;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(2, 3);
        assertEquals(expResult, result);

        ángulo = Math.toRadians(-90);
        distancia = 1;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(3, 2);
        assertEquals(expResult, result);

       // Mover distancia 0
        ángulo = Math.toRadians(0);
        distancia = 0;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(3, 3);
        assertEquals(expResult, result);

        ángulo = Math.toRadians(90);
        distancia = 0;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(3, 3);
        assertEquals(expResult, result);

        ángulo = Math.toRadians(180);
        distancia = 0;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(3, 3);
        assertEquals(expResult, result);

        ángulo = Math.toRadians(270);
        distancia = 0;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(3, 3);
        assertEquals(expResult, result);

        ángulo = Math.toRadians(33);
        distancia = 0;
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(3, 3);
        assertEquals(expResult, result);

        // Mover en diagonal
        double delta = 0.000001;

        distancia = Math.sqrt(2);

       ángulo = Math.toRadians(45);
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(4, 4);
        //assertEquals(expResult, result);
        assertTrue(estáEnRango(result.getX(), expResult.getX(), delta));
        assertTrue(estáEnRango(result.getY(), expResult.getY(), delta));

       ángulo = Math.toRadians(135);
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(2, 4);
        //assertEquals(expResult, result);
        assertTrue(estáEnRango(result.getX(), expResult.getX(), delta));
        assertTrue(estáEnRango(result.getY(), expResult.getY(), delta));

        ángulo = Math.toRadians(225);
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(2, 2);
        assertTrue(estáEnRango(result.getX(), expResult.getX(), delta));
        assertTrue(estáEnRango(result.getY(), expResult.getY(), delta));

        ángulo = Math.toRadians(315);
        result = OperacionesGeométricas.moverPunto(punto, ángulo, distancia);
        expResult = new Point2D.Double(4, 2);
        //assertEquals(expResult, result);
        assertTrue(estáEnRango(result.getX(), expResult.getX(), delta));
        assertTrue(estáEnRango(result.getY(), expResult.getY(), delta));

       //assertEquals(expResult, result);

    }

    public static boolean estáEnRango(double valor, double valorEsperado, double delta) {
        if (Math.abs(valor - valorEsperado) < Math.abs(delta)) {
            return true;
        }
        return false;
    }

    @Test
    public void testPuntoMedio() {
        Point2D.Double p1 = new Point2D.Double(0, 0),
                p2 = new Point2D.Double(2, 2),
                p3 = new Point2D.Double(4, 0),
                p4 = new Point2D.Double(0, 4),
                p5 = new Point2D.Double(1, 1);

        assertEquals(OperacionesGeométricas.puntoMedio(p1, p2), p5);
        assertEquals(OperacionesGeométricas.puntoMedio(p2, p1), p5);
        assertEquals(OperacionesGeométricas.puntoMedio(p3, p4), p2);
        assertEquals(OperacionesGeométricas.puntoMedio(p4, p3), p2);

    }
}
