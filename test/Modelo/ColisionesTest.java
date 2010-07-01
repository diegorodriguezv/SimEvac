/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

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
public class ColisionesTest {

    ObjetoSimulación pared1, pared2, pared3, agente1, agente2, agente3;
    Point2D.Double punto1, punto2, punto3;

    public ColisionesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        System.out.println("* PruebaColisiones: @BeforeClass method");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {

        System.out.println("* PruebaColisiones: @AfterClass method");
    }

    @Before
    public void setUp() {

        System.out.println("* PruebaColisiones: @Before method");
        pared1 = new Pared(new java.awt.geom.Point2D.Double(1, 1),
                new java.awt.geom.Point2D.Double(2, 2), 0.2f);
        pared2 = new Pared(new java.awt.geom.Point2D.Double(1, 1.5),
                new java.awt.geom.Point2D.Double(2, 1.5), 0.2f);
        pared3 = new Pared(new java.awt.geom.Point2D.Double(3, 3),
                new java.awt.geom.Point2D.Double(3, 5), 0.2f);

        agente1 = new Agente(new Point2D.Double(3, 3), 1.5f, 0.75f);
        agente2 = new Agente(new Point2D.Double(4, 4), 1.5f, 0.75f);
        agente3 = new Agente(new Point2D.Double(2, 3), 1.5f, 0.75f);
        ((Agente) agente3).setOrientación(new Point2D.Double(1, 0));

        punto1 = new Point2D.Double(3.5, 3.5);
        punto2 = new Point2D.Double(1, 4);
        punto3 = new Point2D.Double(3.5, 2.3);

    }

    @After
    public void tearDown() {

        System.out.println("* PruebaColisiones: @After method");
    }

    @Test
    public void probarColisiónEntreParedes() {
        System.out.println("* PruebaColisiones: @Test method");

        assertTrue("Pared1 colisiona con Pared2", pared1.colisiona(pared2));
        assertFalse("Pared2 no colisiona con Pared3", pared2.colisiona(pared3));
        assertFalse("Pared1 no colisiona con Pared3", pared1.colisiona(pared3));

    }

    @Test
    public void probarColisiónEntreParedAgente() {
        System.out.println("* PruebaColisiones: @Test method");

        assertTrue("Pared3 colisiona con Agente1", pared3.colisiona(agente1));
        assertFalse("Pared2 no colisiona con Agente2", pared2.colisiona(agente2));
    }

    @Test
    public void probarColisiónEntreAgentePunto() {
        System.out.println("* PruebaColisiones: @Test method");

        assertFalse("Agente1 no colisiona con Punto1", agente1.colisiona(punto1));
        assertFalse("Agente2 no colisiona con Punto1", agente2.colisiona(punto1));
        assertFalse("Agente1 no colisiona con Punto2", agente1.colisiona(punto2));
        assertFalse("Agente2 no colisiona con Punto2", agente2.colisiona(punto2));
        assertFalse("Agente1 no colisiona con Punto3", agente1.colisiona(punto3));
        assertFalse("Agente2 no colisiona con Punto3", agente2.colisiona(punto3));
        // der
        ((Agente) agente1).setOrientación(new Point2D.Double(1, 0));
        ((Agente) agente2).setOrientación(new Point2D.Double(1, 0));
        assertFalse("Agente1 no colisiona con Punto1", agente1.colisiona(punto1));
        assertFalse("Agente2 no colisiona con Punto1", agente2.colisiona(punto1));
        // abajo
        ((Agente) agente1).setOrientación(new Point2D.Double(0, 1));
        ((Agente) agente2).setOrientación(new Point2D.Double(0, 1));
        assertFalse("Agente1 no colisiona con Punto1", agente1.colisiona(punto1));
        assertFalse("Agente2 no colisiona con Punto1", agente2.colisiona(punto1));
        // diag abajo-der
        ((Agente) agente1).setOrientación(new Point2D.Double(1, 1));
        ((Agente) agente2).setOrientación(new Point2D.Double(1, 1));
        assertFalse("Agente1 no colisiona con Punto1", agente1.colisiona(punto1));
        assertFalse("Agente2 no colisiona con Punto1", agente2.colisiona(punto1));
        // otra abajo-izq
        ((Agente) agente1).setOrientación(new Point2D.Double(-1, 1));
        ((Agente) agente2).setOrientación(new Point2D.Double(-1, 1));
        assertTrue("Agente1 colisiona con Punto1", agente1.colisiona(punto1));
        assertTrue("Agente2 colisiona con Punto1", agente2.colisiona(punto1));

    }

    @Test
    public void probarColisiónEntreAgentePared() {
        System.out.println("* PruebaColisiones: @Test method");

        assertTrue("Agente1 colisiona con Pared3", agente1.colisiona(pared3));
        assertFalse("Agente2 no colisiona con Pared3", agente2.colisiona(pared3));
        assertFalse("Agente1 no colisiona con Pared1", agente1.colisiona(pared1));
        assertFalse("Agente2 no colisiona con Pared1", agente2.colisiona(pared1));
        assertFalse("Agente1 no colisiona con Pared2", agente1.colisiona(pared2));
        assertFalse("Agente2 no colisiona con Pared2", agente2.colisiona(pared2));
    }

    @Test
    public void probarColisiónConPuntos() {
        System.out.println("* PruebaColisiones: @Test method");

        Point2D.Double adentro1, adentro2, afuera1, afuera2;

        adentro1 = new Point2D.Double(1.5, 1.5);
        adentro2 = new Point2D.Double(1.1, 1);
        afuera1 = new Point2D.Double(3, 3);
        afuera2 = new Point2D.Double(0.9, 1);

        assertTrue("Pared1 colisiona con adentro1", pared1.colisiona(adentro1));
        assertTrue("Pared1 colisiona con adentro2", pared1.colisiona(adentro2));
        assertFalse("Pared1 no colisiona con afuera1", pared1.colisiona(afuera1));
        assertFalse("Pared1 no colisiona con afuera2", pared1.colisiona(afuera2));

    }

    @Test
    public void probarColisiónConDistanciaPunto() {

        Point2D.Double adentro1, adentro2, afuera1, afuera2;

        adentro1 = new Point2D.Double(1.5, 1.5);
        adentro2 = new Point2D.Double(1.1, 1);
        afuera1 = new Point2D.Double(3, 3);
        afuera2 = new Point2D.Double(0.9, 1);

        assertTrue(pared1.colisiona(adentro1, 0.001));
        assertTrue(pared1.colisiona(adentro2, 0.001));
        assertTrue(pared1.colisiona(adentro1, 50));
        assertTrue(pared1.colisiona(adentro2, 50));

        assertFalse(pared1.colisiona(afuera1, 0.9999));
        assertFalse(pared1.colisiona(afuera2, 0.05));
        assertTrue(pared1.colisiona(afuera1, 1.0001));
        assertTrue(pared1.colisiona(afuera2, 0.06));

    }
}
