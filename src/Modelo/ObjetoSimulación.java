package Modelo;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class ObjetoSimulación {

    protected Shape forma;
    protected Area área;
    protected Point2D.Double centro;
    protected FachadaMundo mundo;

    protected ArrayList<Line2D.Double> contorno;
    protected double ancho, largo;
    protected Point2D.Double orientación;

    public double getAncho() {
        return ancho;
    }

    public double getLargo() {
        return largo;
    }

    public Double getOrientación() {
        return orientación;
    }

    public Area getÁrea() {
        return área;
    }

    public Double getCentro() {
        return centro;
    }

    public void setMundo(FachadaMundo mundo) {
        this.mundo = mundo;
    }

    public Shape getForma() {
        return forma;
    }

    public ArrayList<Line2D.Double> getContorno() {
        return contorno;
    }

    
    public ObjetoSimulación() {
    }

    
    public void actualizar(long tiempo) {
    }

    
//    public boolean colisiona(ObjetoSimulación objeto) {
//        for (Line2D miLínea : contorno) {
//            for (Line2D suLínea : objeto.getContorno()) {
//                if (miLínea.intersectsLine(suLínea)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
        public boolean colisiona(ObjetoSimulación objeto) {

        Area miÁrea = new Area(Contorno.convertirEnForma(contorno));
        Area suÁrea = new Area(Contorno.convertirEnForma(objeto.getContorno()));
        miÁrea.intersect(suÁrea);
        return !miÁrea.isEmpty();
    }

    public ArrayList<ObjetoSimulación> colisiona(ArrayList<ObjetoSimulación> listaObjetos) {

        ArrayList<ObjetoSimulación> resultado = new ArrayList<ObjetoSimulación>();
        for (ObjetoSimulación objeto : listaObjetos) {

            if (this.colisiona(objeto)) {
                resultado.add(objeto);
            }
        }
        return resultado;
    }


    public ArrayList<Agente> colisionaConAgentes(ArrayList<Agente> listaAgentes) {

        ArrayList<Agente> resultado = new ArrayList<Agente>();
        for (Agente agente : listaAgentes) {

            if (this.colisiona(agente)) {
                resultado.add(agente);
            }
        }
        return resultado;
    }

    public void setOrientación(Double orientación) {

        this.orientación = orientación;
    }

    public boolean colisiona(Point2D.Double punto) {
        área = new Area(Contorno.convertirEnForma(contorno));
        return área.contains(punto);
    }

    public boolean colisiona(Point2D.Double punto, double distancia) {
        Rectangle2D.Double zona = new Rectangle2D.Double(punto.getX() - distancia,
                punto.getY() - distancia, distancia * 2, distancia * 2);
        Area áreaZona = new Area(zona);
        área = new Area(Contorno.convertirEnForma(contorno));
        áreaZona.intersect(área);
        if (áreaZona.isEmpty()) {
            return false;
        }
        return true;
    }
}

