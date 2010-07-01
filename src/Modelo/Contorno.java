/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Física.OperacionesGeométricas;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author diego
 */
public class Contorno {

    /**
     * Devuelve un Contorno rectangular definido por los 2 puntos extremos de su
     * eje longitudinal. Es el equivalente de ENGORDAR una línea al pasarla de 1D
     * a 2D.
     * @param p1 Primer punto del Eje Longitudinal
     * @param p2 Segundo punto del Eje Longitudinal
     * @param ancho Ancho total del rectángulo
     * @return el Contorno rectangular
     */
    public static ArrayList<Line2D.Double> obtenerRectangularEje(Point2D.Double p1, Point2D.Double p2, double ancho) {

        ArrayList<Line2D.Double> contorno = new ArrayList<Line2D.Double>();

        double ángulo = Math.atan2(p2.y - p1.y, p2.x - p1.x);

        Point2D.Double p1a, p1b, p2a, p2b;

        p1a = (Point2D.Double) OperacionesGeométricas.moverPunto(p1, ángulo - Math.toRadians(90), ancho / 2);
        p1b = (Point2D.Double) OperacionesGeométricas.moverPunto(p1, ángulo + Math.toRadians(90), ancho / 2);

        p2a = (Point2D.Double) OperacionesGeométricas.moverPunto(p2, ángulo - Math.toRadians(90), ancho / 2);
        p2b = (Point2D.Double) OperacionesGeométricas.moverPunto(p2, ángulo + Math.toRadians(90), ancho / 2);

        contorno.add(new Line2D.Double(p1a, p2a));
        contorno.add(new Line2D.Double(p2a, p2b));
        contorno.add(new Line2D.Double(p2b, p1b));
        contorno.add(new Line2D.Double(p1b, p1a));

        return contorno;
    }

    /**
     * Devuelve un contorno rectangular definido por un centro, una orientación, el ancho y el alto
     * @param posición Centro del rectángulo
     * @param orientación coordenada desde el orígen
     * @param ancho
     * @param alto
     * @return
     */
    public static ArrayList<Line2D.Double> obtenerRectangularCentro(Point2D.Double posición, Point2D.Double orientación, double ancho, double alto) {
        ArrayList<Line2D.Double> contorno = new ArrayList<Line2D.Double>();
        double ángulo = Math.atan2(orientación.y, orientación.x);
        Point2D.Double p1;
        Point2D.Double p2;

        // Construir el eje longitudinal
        p1 = OperacionesGeométricas.moverPunto(posición, ángulo, alto / 2f);
        p2 = OperacionesGeométricas.moverPunto(posición, ángulo, -alto / 2f);
        contorno = obtenerRectangularEje(p1, p2, ancho);
        return contorno;
    }

    /**
     * Devuelve un objeto que hereda de forma @see=Shape
     * @param contorno
     * @return
     */
    public static Path2D.Double convertirEnForma(ArrayList<Line2D.Double> contorno) {
        int numSegmentos = contorno.size();
        int i = 0;
        Path2D.Double resultado = new Path2D.Double();
        resultado.moveTo(contorno.get(i).getX1(), contorno.get(i).getY1());
        for (Line2D line2D : contorno) {
            double x = line2D.getX2();
            double y = line2D.getY2();
            resultado.lineTo(x, y);
            i++;
        }

        return resultado;
    }
}
