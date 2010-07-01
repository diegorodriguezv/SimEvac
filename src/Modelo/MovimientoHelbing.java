package Modelo;

import Física.Partícula;
import Física.OperacionesVectoriales;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

public class MovimientoHelbing implements IMovimiento {

    public static final double A = 25;
    public static final double B = 0.8;
    public static final double COEFICIENTE_COMPRESIÓN = 3000;
    public static final double COEFICIENTE_TANGENCIAL = 300;
    public static final double DISTANCIA_LLEGO = 5;
    public static final double FACTOR_FRENADO = 0.5;
    protected static final double DISTANCIA_FRENADO = 5;
    private static final double DISTANCIA_DETENCION = 0.2;
    private Partícula partícula;
    private static final double RAPIDEZ_PREFERIDA = 2;
    private ArrayList<Point2D.Double> ruta;
    private int puntoRutaActual = -1;
    private static final double TIEMPO_RELAJACION = 5;
    private FachadaMundo mundo;

    public void setAgente(Agente agente) {
        this.agente = agente;
    }
    private Agente agente;
    private double masa = 80;

    public void setRuta(ArrayList<Double> ruta) {

        puntoRutaActual = 0;
        if (ruta == null || ruta.size() == 0) {
            throw new IllegalArgumentException("Error: Se intentó establecer una ruta vacía");
        }
        this.ruta = ruta;
    }

    public Partícula getPartícula() {
        return partícula;
    }


    public MovimientoHelbing(FachadaMundo mundo, Agente agente) {
        partícula = new Partícula();
        this.mundo = mundo;
        this.agente = agente;
    }


    public void moverse(long milisegundos) {

        partícula.actualizar(milisegundos);
    }

    private Point2D.Double calcularAceleraciónFrenado(Point2D.Double direcciónPreferida) {
        Point2D.Double direcciónOpuesta = OperacionesVectoriales.multiplicar(direcciónPreferida, -1);
        double distanciaAlPunto = ruta.get(puntoRutaActual).distance(this.partícula.getPosición());

        double rapidezFrenado = FACTOR_FRENADO / distanciaAlPunto;
        Point2D.Double fuerza = OperacionesVectoriales.multiplicar(
                OperacionesVectoriales.sumaVectorial(
                OperacionesVectoriales.multiplicar(direcciónOpuesta, rapidezFrenado),
                OperacionesVectoriales.multiplicar(partícula.getVelocidad(), -1)),
                1f / TIEMPO_RELAJACION);
        return fuerza;

    }

    private Point2D.Double calcularDirecciónPreferida() {
        Point2D.Double siguientePunto = ruta.get(puntoRutaActual);

        Point2D.Double dirección;
        // Encontrar la dirección preferida
        dirección = OperacionesVectoriales.sumaVectorial(siguientePunto,
                OperacionesVectoriales.multiplicar(partícula.getPosición(), -1));
        return OperacionesVectoriales.normalizar(dirección);
    }

    private Point2D.Double calcularAceleraciónRelajada(Point2D.Double direcciónPreferida) {
        Point2D.Double fuerza = OperacionesVectoriales.multiplicar(
                OperacionesVectoriales.sumaVectorial(
                OperacionesVectoriales.multiplicar(direcciónPreferida, RAPIDEZ_PREFERIDA),
                OperacionesVectoriales.multiplicar(partícula.getVelocidad(), -1)),
                1f / TIEMPO_RELAJACION);
        return fuerza;

    }

    private Point2D.Double calcularAceleraciónInfluenciaOtrosAgentes() {
        Point2D.Double aceleraciónOtrosAgentes = new Point2D.Double(0, 0);

        //todo: introducir constante DISTANCIA_INTERACCION_AGENTES
        for (Agente agenteCercano : mundo.getAgentesCercanos(partícula.getPosición(), 2)) {
            if (agenteCercano != this.agente) {
                aceleraciónOtrosAgentes = OperacionesVectoriales.sumaVectorial(
                        aceleraciónOtrosAgentes,
                        calcularInteracciónAgente(agenteCercano));
            }
        }
        return aceleraciónOtrosAgentes;
    }

    private Point2D.Double calcularInteracciónAgente(Agente agente) {
        Point2D.Double resultado = new Point2D.Double(0, 0);
        Point2D.Double separaciónEntreCentroAgentes = OperacionesVectoriales.sumaVectorial(partícula.getPosición(),
                OperacionesVectoriales.multiplicar(agente.getCentro(), -1));

        Point2D.Double separaciónEntreCentroAgentesNormalizada = OperacionesVectoriales.normalizar(separaciónEntreCentroAgentes);
        Point2D.Double vectorTangente =
                new Point2D.Double(-separaciónEntreCentroAgentesNormalizada.y, separaciónEntreCentroAgentesNormalizada.x);
        double magnitudSeparaciónEntreCentroAgentes = OperacionesVectoriales.magnitud(separaciónEntreCentroAgentes);
        double distanciaPenetración = agente.getAncho() - magnitudSeparaciónEntreCentroAgentes;
        double funcion_g;
        // Incluir fuerzas de interacción granular si están en contacto
        if (distanciaPenetración > 0) {
            funcion_g = 1;
        } // Únicamente fuerza de repulsión
        else {
            funcion_g = 0;
        }
        double factor = A * Math.exp((distanciaPenetración) / B) + COEFICIENTE_COMPRESIÓN * funcion_g * distanciaPenetración;
        resultado = OperacionesVectoriales.multiplicar(separaciónEntreCentroAgentesNormalizada, factor);

        double deltaVelocidadTangencial = OperacionesVectoriales.productoPunto(
                OperacionesVectoriales.sumaVectorial(agente.getEstrategiaMovimiento().getPartícula().getVelocidad(),
                OperacionesVectoriales.multiplicar(getPartícula().getVelocidad(), -1)),
                vectorTangente);

        double factor2 = COEFICIENTE_TANGENCIAL * funcion_g * distanciaPenetración * deltaVelocidadTangencial;

        resultado = OperacionesVectoriales.sumaVectorial(resultado, OperacionesVectoriales.multiplicar(vectorTangente, factor2));

        resultado = OperacionesVectoriales.multiplicar(resultado, 1 / masa);
        return resultado;
    }

    private Point2D.Double calcularAceleraciónInfluenciaParedes() {
        Point2D.Double aceleraciónOtrosAgentes = new Point2D.Double(0, 0);

        //todo: introducir constante DISTANCIA_INTERACCION_AGENTES
        for (ObjetoSimulación pared : mundo.getParedesCercanas(partícula.getPosición(), 2)) {
            if (pared != this.agente && pared.getClass().getName().equals("Modelo.Pared")) {
                aceleraciónOtrosAgentes = OperacionesVectoriales.sumaVectorial(
                        aceleraciónOtrosAgentes,
                        calcularInteracciónPared((Pared) pared));
            }
        }
        return aceleraciónOtrosAgentes;
    }

    private Point2D.Double calcularInteracciónPared(Pared pared) {
        Point2D.Double resultado = new Point2D.Double(0, 0);
        Line2D.Double líneaPared = new Line2D.Double(pared.getLínea().getP1(), pared.getLínea().getP2());

        // Esta es una de las direcciones, escoger la que aplica a esta partícula
        // dependiendo de a qué lado esté
        int posiciónRelativa = líneaPared.relativeCCW(partícula.getPosición());
        Point2D.Double p1 = null, p2 = null;
        switch (posiciónRelativa) {
            case 1:
                p2 = new Double(líneaPared.getX1(), líneaPared.getY1());
                p1 = new Double(líneaPared.getX2(), líneaPared.getY2());
                break;
            case -1: // Invertir el órden de los puntos
                p1 = new Double(líneaPared.getX1(), líneaPared.getY1());
                p2 = new Double(líneaPared.getX2(), líneaPared.getY2());
                break;
            case 0:
                p1 = new Double(líneaPared.getX1(), líneaPared.getY1());
                p2 = new Double(líneaPared.getX2(), líneaPared.getY2());
                break;
            default:
//                throw new Exception("error grave, esto no debería pasar");
                break;
        }


        Point2D.Double direcciónTangencialPared = OperacionesVectoriales.normalizar(
                OperacionesVectoriales.sumaVectorial(p2,
                OperacionesVectoriales.multiplicar(p1, -1)));


        Point2D.Double direcciónNormalPared = new Double(-direcciónTangencialPared.y, direcciónTangencialPared.x);

        double magnitudSeparación = pared.calcularDistancia(partícula.getPosición());
        double distanciaPenetración = agente.getAncho() - magnitudSeparación;
        double funcion_g;
        // Incluir fuerzas de interacción granular si están en contacto
        if (distanciaPenetración > 0) {
            funcion_g = 1;
        } // Únicamente fuerza de repulsión
        else {
            funcion_g = 0;
        }
        double factor = A * Math.exp((distanciaPenetración) / B) + COEFICIENTE_COMPRESIÓN * funcion_g * distanciaPenetración;
        resultado = OperacionesVectoriales.multiplicar(direcciónNormalPared, factor);

        double factorTangencial = OperacionesVectoriales.productoPunto(getPartícula().getVelocidad(),
                direcciónTangencialPared);

        double factor2 = -COEFICIENTE_TANGENCIAL * funcion_g * distanciaPenetración * factorTangencial;

        resultado = OperacionesVectoriales.sumaVectorial(resultado, OperacionesVectoriales.multiplicar(direcciónTangencialPared, factor2));

        resultado = OperacionesVectoriales.multiplicar(resultado, 1 / masa);
        return resultado;
    }

    public void calcularFuerzas() {
        Point2D.Double direcciónPreferida = calcularDirecciónPreferida();
        //HACK: Frenado
        // Solamente si es el último punto en la ruta
        double distanciaAlSiguientePunto;
        distanciaAlSiguientePunto = ruta.get(puntoRutaActual).distance(this.partícula.getPosición());
        if (ruta.size() == puntoRutaActual + 1) {
            if (distanciaAlSiguientePunto < DISTANCIA_FRENADO) {
                partícula.setAceleración(calcularAceleraciónFrenado(direcciónPreferida));
            } else {

                partícula.setAceleración(
                        OperacionesVectoriales.sumaVectorial(
                        OperacionesVectoriales.sumaVectorial(
                        calcularAceleraciónInfluenciaOtrosAgentes(),
                        calcularAceleraciónRelajada(direcciónPreferida)), calcularAceleraciónInfluenciaParedes()));
            }
            if (distanciaAlSiguientePunto < DISTANCIA_DETENCION) {
                // Parar en seco!
                partícula.setVelocidad(new Point2D.Double(0, 0));
                partícula.setAceleración(new Point2D.Double(0, 0));
            }
        } else {
            if (distanciaAlSiguientePunto < DISTANCIA_LLEGO) {
                // Ya llegamos! avanzar en la ruta
                puntoRutaActual++;
            }
            partícula.setAceleración(
                    OperacionesVectoriales.sumaVectorial(
                    OperacionesVectoriales.sumaVectorial(
                    calcularAceleraciónInfluenciaOtrosAgentes(),
                    calcularAceleraciónRelajada(direcciónPreferida)), calcularAceleraciónInfluenciaParedes()));
        }
    }
}

