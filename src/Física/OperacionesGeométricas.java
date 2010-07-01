/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Física;

import java.awt.geom.Point2D;

/**
 *
 * @author diego
 */
public class OperacionesGeométricas {

    public static Point2D.Double moverPunto(Point2D.Double punto, double ángulo, double distancia) {
        double x = Math.cos(ángulo) * distancia + punto.getX();
        double y = Math.sin(ángulo) * distancia + punto.getY();

        Point2D.Double nuevoPunto = new Point2D.Double(x, y);
        return nuevoPunto;
    }

    public static Point2D.Double puntoMedio(Point2D.Double p1, Point2D.Double p2){
        return new Point2D.Double((p1.x + p2.x)/2, (p1.y + p2.y)/2);
    }
}
