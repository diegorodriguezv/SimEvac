/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
public class GeometríaObjetosTest {

    private static final double DELTA = 0.0000000001;
    private Agente agente1;
    private Puerta puerta1;
    private Pared pared1;
    private Point2D.Double punto1;
    private Line2D.Double línea1;

    public GeometríaObjetosTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        punto1 = new Point2D.Double(4, 4);
        agente1 = new Agente(punto1, 2, 1);
        pared1 = new Pared(new Point2D.Double(2, 2), new Point2D.Double(3, 3), 0.2f);
        puerta1 = new Puerta(new Point2D.Double(3, 2), new Point2D.Double(2, 3), 0.2f);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void agenteTest() {
        ArrayList<Line2D.Double> contorno = agente1.getContorno();
        validarContinuidad(contorno);
        validarLímites(contorno, 2, 3, 6, 5);
        agente1.setOrientación(new Point2D.Double(1, 1));
    }

    @Test
    public void paredTest() {
        ArrayList<Line2D.Double> contorno = pared1.getContorno();
        validarContinuidad(contorno);
    }

    @Test
    public void puertaTest() {
        ArrayList<Line2D.Double> contorno = puerta1.getContorno();
        validarContinuidad(contorno);
    }

    private void validarContinuidad(ArrayList<Double> contorno) {
        Line2D.Double primeraLínea = contorno.get(0);
        Line2D.Double últimaLínea = contorno.get(contorno.size() - 1);
        // validar continuidad
        assertEquals(primeraLínea.x1, últimaLínea.x2, DELTA);
        assertEquals(primeraLínea.y1, últimaLínea.y2, DELTA);
        for (int i = 1; i < contorno.size(); i++) {
            Line2D.Double líneaAntes = contorno.get(i - 1);
            Line2D.Double línea = contorno.get(i);
            assertEquals(líneaAntes.x2, línea.x1, DELTA);
            assertEquals(líneaAntes.y2, línea.y1, DELTA);
        }
    }

    private void validarLímites(ArrayList<Double> contorno, int minx, int miny, int maxx, int maxy) {
        for (int i = 0; i < contorno.size(); i++) {
            Line2D.Double línea = contorno.get(i);
            assertTrue(línea.x1 < maxx+DELTA);
            assertTrue(línea.x2 < maxx+DELTA);
            assertTrue(línea.x1 > minx-DELTA);
            assertTrue(línea.x2 > minx-DELTA);
            assertTrue(línea.y1 < maxy+DELTA);
            assertTrue(línea.y2 < maxy+DELTA);
            assertTrue(línea.y1 > miny-DELTA);
            assertTrue(línea.y2 > miny-DELTA);
        }
    }
}
