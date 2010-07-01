/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Física;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 * Encapsula operaciones que se pueden realizar sobre un vector.
 * Un vector se representa como un punto y se debe interpretar como el vector
 * desde el orígen hasta el punto.
 * 
 * @author diego
 */
public class OperacionesVectoriales {

    public static Point2D.Double multiplicar(Point2D.Double vector, double factor) {
        return new Point2D.Double(vector.x * factor, vector.y * factor);
    }

    public static Point2D.Double sumaVectorial(Point2D.Double vector1, Point2D.Double vector2) {
        return new Point2D.Double(vector1.x + vector2.x, vector1.y + vector2.y);
    }

    public static double magnitud(Point2D.Double vector) {
        return vector.distance(0, 0);
    }

    public static Point2D.Double normalizar(Point2D.Double vector){
        return multiplicar(vector, 1/magnitud(vector));
    }

    public static double productoPunto(Double vector1, Double vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }
}
