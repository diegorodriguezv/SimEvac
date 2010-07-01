/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Física;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 * Modela el distanciaRecorrida de una partícula en un espacio bidimensional. Las magnitudes
 * vectoriales se representan como puntos. Se deben interpretar como el vector
 * que parte del orígen y termina en dicho punto.
 * @author diego
 */
public class Partícula {

    private Point2D.Double posición, velocidad, aceleración;

    public Partícula() {
        this(new Point2D.Double(0, 0), new Point2D.Double(0, 0), new Point2D.Double(0, 0));
    }

    public Partícula(Double posición, Double velocidad, Double aceleración) {
        this.posición = posición;
        this.velocidad = velocidad;
        this.aceleración = aceleración;
    }

    /**
     * Calcula la distanci recorrida tras un lapso de tiempo. No tiene efectos sobre el
     * estado de la partícula.
     * @param milisegundos
     * @return Devuelve la distancia recorrida en el lapso de tiempo
     */
    public Point2D.Double distanciaRecorrida(long milisegundos) {
        Point2D.Double resultado = new Point2D.Double(0, 0);
        resultado.x += velocidad.x * milisegundos / 1000;
        resultado.y += velocidad.y * milisegundos / 1000;
        return resultado;
    }

    /**
     * Calcula la nueva velocidad tras un lapso de tiempo.  No tiene efectos sobre el
     * estado de la partícula.
     * @param milisegundos
     * @return la nueva velocidad
     */
    public Point2D.Double nuevaVelocidad(long milisegundos) {
        Point2D.Double resultado = (Point2D.Double) velocidad.clone();
        resultado.x += aceleración.x * milisegundos / 1000;
        resultado.y += aceleración.y * milisegundos / 1000;
        return resultado;
    }

    /**
     * Actualiza el estado de la partícula tras un lapso de tiempo.
     * @param milisegundos
     */
    public void actualizar(long milisegundos) {
        velocidad = nuevaVelocidad(milisegundos);
        posición.x += distanciaRecorrida(milisegundos).x;
        posición.y += distanciaRecorrida(milisegundos).y;
    }

    public Double getAceleración() {
        return aceleración;
    }

    public void setAceleración(Double aceleración) {
        this.aceleración = aceleración;
    }

    public Double getPosición() {
        return posición;
    }

    public void setPosición(Double posición) {
        this.posición = posición;
    }

    public Double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Double velocidad) {
        this.velocidad = velocidad;
    }
}
