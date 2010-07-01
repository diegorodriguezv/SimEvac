package Modelo;


import Física.OperacionesGeométricas;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;

public class Pared extends ObjetoSimulación {

    private Line2D.Double línea;
    private double grosor;


    public Pared(Point2D.Double p1, Point2D.Double p2, double grosor) {
        //línea = new Line2D.Float(p1, p2);
        this.grosor = grosor;
        this.línea = new Line2D.Double(p1, p2);
        this.contorno = Contorno.obtenerRectangularEje(p1, p2, grosor);
        this.centro = OperacionesGeométricas.puntoMedio(p1, p2);
        this.forma = Contorno.convertirEnForma(contorno);
        this.área = new Area(forma);
    }

    public Double getLínea() {
        return línea;
    }


    @Override
    public void actualizar(long t) {
    }

    public double calcularDistancia(Point2D.Double p) {
        return línea.ptSegDist(p);
    }

//
//    public Point2D.Double calcularPuntoMásCercano(Point2D.Double p) {
//        return línea.ptLineDist(p);
//    }
}

