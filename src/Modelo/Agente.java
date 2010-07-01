package Modelo;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;


public class Agente extends ObjetoSimulación {


    private IMovimiento estrategiaMovimiento;

    private Point2D.Double destino;

    private double rapidez;

    private Point2D.Double posición;


    public Agente(Point2D.Double posición, double ancho, double largo) {
        this.posición = new Point2D.Double(posición.x, posición.y);
        this.ancho = ancho;
        this.largo = largo; // gordito?
        this.destino = this.posición; // Está donde debe estar!
//        trayectoria = new ArrayList<Point2D.Double>();
//        forma = new Ellipse2D.Double(posición.getX() - radio / 2,
//                posición.getY() - radio / 2, radio, radio);
//        área = new Area(forma);

       this.orientación = new Point2D.Double(1, 0);

        contorno = new ArrayList<Line2D.Double>();
        construirContorno();
    }


    @Override
    public void actualizar(long tiempo) {
        actualizarPosición(tiempo);
        construirContorno();
    }

    public void actualizarPosición(long tiempo) {
        estrategiaMovimiento.moverse(tiempo);
        this.posición = estrategiaMovimiento.getPartícula().getPosición();
        this.orientación = estrategiaMovimiento.getPartícula().getVelocidad();        
   }

    public IMovimiento getEstrategiaMovimiento() {
        return estrategiaMovimiento;
    }

    public void setEstrategiaMovimiento(IMovimiento mov) {
        this.estrategiaMovimiento = mov;
        
        // Inicializar partícula
        estrategiaMovimiento.getPartícula().setPosición(posición);
        
    }


    public Point2D.Double getDestino() {
        return destino;
    }


    public void setDestino(Point2D.Double dest) {
        this.destino = dest;
    }


    public Point2D.Double getPosición() {
        return posición;
    }


    public void setPosición(Point2D.Double pos) {
        this.posición = pos;
        construirContorno();
    }


    public double getRapidez() {
        return rapidez;
    }


    public void setRapidez(double rapidez) {
        this.rapidez = rapidez;
    }

    /**
     * @return the orientación
     */
    public Point2D.Double getOrientación() {
        return orientación;
    }

    /**
     * @param orientación the orientación to set
     */
    public void setOrientación(Point2D.Double orientación) {
        this.orientación = orientación;
        construirContorno();

    }

    private void construirContorno() {
        contorno = Contorno.obtenerRectangularCentro(posición, orientación, ancho, largo);
        centro = posición;
//        forma = Contorno.convertirEnForma(contorno);
//        área = new Area();
    }

    public void calcularFuerzas() {
        this.estrategiaMovimiento.calcularFuerzas();
    }
}

