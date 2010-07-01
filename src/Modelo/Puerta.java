package Modelo;


import Física.OperacionesGeométricas;
import java.awt.geom.Point2D;
import java.awt.geom.Area;
import java.util.ArrayList;

public class Puerta extends ObjetoSimulación {

    private double grosor;



    public Puerta(Point2D.Double p1, Point2D.Double p2, double grosor) {
        this.grosor = grosor;
        this.contorno = Contorno.obtenerRectangularEje(p1, p2, grosor);
        this.centro = OperacionesGeométricas.puntoMedio(p1, p2);
        this.largo = Point2D.distance(p1.x, p1.y, p2.x, p2.y);

        this.forma = Contorno.convertirEnForma(contorno);
        this.área = new Area(forma);
    }


    @Override
    public void actualizar(long t) {
    }



    public ArrayList<Agente> detectarAgentesAtravesando() {
        ArrayList<Agente>  objetos = mundo.getAgentesCercanos(centro, largo);
        ArrayList<Agente>  colisionan = colisionaConAgentes(objetos);
        ArrayList<Agente>  resultado = new ArrayList<Agente>();
        for (Agente objeto : colisionan) {
            if(objeto.getClass().getName().equals("Modelo.Agente")){
                resultado.add(objeto);
            }
        }
        return resultado;
    }
}

