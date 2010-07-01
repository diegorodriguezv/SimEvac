package Modelo;

import Persistencia.Xml;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FachadaMundo {

    private static final double GROSOR_PARED = 0.4F;
    private ArrayList<ObjetoSimulación> paredes;
    private ArrayList<Agente> agentes;
    private Point2D.Double orígenVisualización, tamañoVisualización;

    public FachadaMundo(File archivo) {
        iniciarObjetos(archivo);
    }

    public ArrayList<ObjetoSimulación> getParedesCercanas(Point2D.Double p, double distancia) {
        ArrayList<ObjetoSimulación> objetosCercanos = new ArrayList<ObjetoSimulación>();
        for (ObjetoSimulación objetoSimulación : paredes) {
            if (objetoSimulación.colisiona(p, distancia)) {
                objetosCercanos.add(objetoSimulación);
            }
        }

        return objetosCercanos;
    }

    public ArrayList<ObjetoSimulación> getObjetosCercanos(Point2D.Double p, double distancia) {
        ArrayList<ObjetoSimulación> objetosCercanos = new ArrayList<ObjetoSimulación>();
        for (ObjetoSimulación objetoSimulación : paredes) {
            if (objetoSimulación.colisiona(p, distancia)) {
                objetosCercanos.add(objetoSimulación);
            }
        }
        for (ObjetoSimulación objetoSimulación : agentes) {
            if (objetoSimulación.colisiona(p, distancia)) {
                objetosCercanos.add(objetoSimulación);
            }
        }
        return objetosCercanos;
    }

    public ArrayList<Agente> getAgentesCercanos(Point2D.Double p, double distancia) {
        ArrayList<Agente> agentesCercanos = new ArrayList<Agente>();
        for (Agente agente : agentes) {
            if (agente.colisiona(p, distancia)) {
                agentesCercanos.add(agente);
            }
        }
        return agentesCercanos;
    }


    public void actualizarTodos(long tiempo) {
        for (int i = 0; i < agentes.size(); i++) {
            Agente objeto = agentes.get(i);
            objeto.calcularFuerzas();
        }
        for (int i = 0; i < agentes.size(); i++) {
            Agente objeto = agentes.get(i);
            objeto.actualizar(tiempo);
        }
    }

    public ArrayList<ObjetoSimulación> getObjetos() {
        return paredes;
    }

    public ArrayList<Agente> getAgentes() {
        return agentes;
    }

    private void iniciarObjetos(File archivo) {
//        CargarMundo1();
//        CargarMundo2();
//        CargarMundo3();

        File dir1 = new File(".");

        try {
            System.out.println("Current dir : " + dir1.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (archivo == null) {
            CargarMundo3();

        } else {
            CargarMundoDesdeArchivo(archivo.getPath());
        }
    }

    public void CargarMundoDesdeArchivo(String rutaArchivo) {


        Xml mundo = new Xml(rutaArchivo, "mundo");
        System.out.println("cargando archivo: " + rutaArchivo);
        System.out.println("versión: " + mundo.child("versión").content());
        System.out.println("título: " + mundo.child("título").content());
        System.out.println("descripción: " + mundo.child("descripción").content());

        paredes = new ArrayList<ObjetoSimulación>();
        ObjetoSimulación nuevoObjeto;
        agentes = new ArrayList<Agente>();
        Xml punto = mundo.child("visualización").children("punto").get(0);
        orígenVisualización = new Double(Float.parseFloat(punto.string("x")), Float.parseFloat(punto.string("y")));
        punto = mundo.child("visualización").children("punto").get(1);
        tamañoVisualización = new Double(Float.parseFloat(punto.string("x")), Float.parseFloat(punto.string("y")));

        for (Xml pared : mundo.child("paredes").children("pared")) {
            System.out.println("pared: " + pared.string("grosor"));
            Point2D.Double p1, p2;
            punto = pared.children("punto").get(0);
            p1 = new Double(Float.parseFloat(punto.string("x")), Float.parseFloat(punto.string("y")));
            punto = pared.children("punto").get(1);
            p2 = new Double(Float.parseFloat(punto.string("x")), Float.parseFloat(punto.string("y")));
            nuevoObjeto = new Pared(p1, p2, Float.parseFloat(pared.string("grosor")));
            paredes.add(nuevoObjeto);
            System.out.println("x,y: " + punto.string("x") + "," + punto.string("y"));

        }
        for (Xml puerta : mundo.child("puertas").children("puerta")) {
            System.out.println("pared: " + puerta.string("grosor"));
            Point2D.Double p1, p2;
            punto = puerta.children("punto").get(0);
            p1 = new Double(Float.parseFloat(punto.string("x")), Float.parseFloat(punto.string("y")));
            punto = puerta.children("punto").get(1);
            p2 = new Double(Float.parseFloat(punto.string("x")), Float.parseFloat(punto.string("y")));
            nuevoObjeto = new Puerta(p1, p2, Float.parseFloat(puerta.string("grosor")));
            paredes.add(nuevoObjeto);
            System.out.println("x,y: " + punto.string("x") + "," + punto.string("y"));
        }
        Xml movimientos = mundo.child("agentes").child("movimientos");

        System.out.println("movimientos: " + movimientos.string("directorio"));

        String dir = movimientos.string("directorio");

        Map<String, ArrayList<Point2D.Double>> rutasMap = new HashMap<String, ArrayList<Double>>();
        Map<String, IMovimiento> movimientosMap = new HashMap<String, IMovimiento>();


        for (Xml ruta : movimientos.child("rutas").children("ruta")) {
            ArrayList<Point2D.Double> estaRuta = new ArrayList<Double>();
            System.out.println("ruta: " + ruta.string("nombre"));

            for (Xml puntoRuta : ruta.children("punto")) {
                System.out.println("x,y: " + puntoRuta.string("x") + "," + puntoRuta.string("y"));
                estaRuta.add(new Double(Float.parseFloat(puntoRuta.string("x")),
                        Float.parseFloat(puntoRuta.string("y"))));
            }
            rutasMap.put(ruta.string("nombre"), estaRuta);
        }
        for (Xml movimiento : movimientos.children("movimiento")) {
            System.out.println("movimiento: " + movimiento.string("nombre") +
                    movimiento.string("archivo"));
            movimientosMap.put(movimiento.string("nombre"),
                    crearMovimiento(rutasMap.get(movimiento.string("ruta")), null));

        }

        for (Xml multitud : mundo.child("agentes").children("multitud")) {

            System.out.println("cantidad: " + multitud.string("cantidad"));

            Xml esquinaÁrea1 = multitud.child("área").children("punto").get(0);
            Xml esquinaÁrea2 = multitud.child("área").children("punto").get(1);
            Xml varAncho = multitud.child("variación_ancho");
            System.out.println("[min,max]: [" + varAncho.string("min") + "," + varAncho.string("max") + "]");
            Xml varLargoAncho = multitud.child("variación_relacion_largo_ancho");
            System.out.println("[min,max]: [" + varLargoAncho.string("min") + "," + varLargoAncho.string("max") + "]");
            construirMultitud(rutasMap.get(multitud.string("ruta")),
                    new Double(Float.parseFloat(esquinaÁrea1.string("x")), Float.parseFloat(esquinaÁrea1.string("y"))),
                    new Double(Float.parseFloat(esquinaÁrea2.string("x")), Float.parseFloat(esquinaÁrea2.string("y"))),
                    Integer.parseInt(multitud.string("cantidad")),
                    Float.parseFloat(varAncho.string("min")), Float.parseFloat(varAncho.string("max")));
        }

        // agregar la referencia al mundo para todos los objetos
        for (ObjetoSimulación objetoSimulación : paredes) {
            objetoSimulación.setMundo(this);
        }

        for (Agente agente : agentes) {
            agente.setMundo(this);
        }

    }

    public Double getOrígenVisualización() {
        return orígenVisualización;
    }

    public Double getTamañoVisualización() {
        return tamañoVisualización;
    }

    private void ImprimirMundoDesdeArchivo(String rutaArchivo) {


        Xml mundo = new Xml(rutaArchivo, "mundo");

        System.out.println("versión: " + mundo.child("versión").content());
        System.out.println("título: " + mundo.child("título").content());
        System.out.println("descripción: " + mundo.child("descripción").content());


        for (Xml punto : mundo.child("visualización").children("punto")) {
            System.out.println("x,y: " + punto.string("x") + "," + punto.string("y"));
        }

        for (Xml pared : mundo.child("paredes").children("pared")) {

            System.out.println("pared: " + pared.string("grosor"));

            for (Xml punto : pared.children("punto")) {
                System.out.println("x,y: " + punto.string("x") + "," + punto.string("y"));
            }
        }
        for (Xml puerta : mundo.child("puertas").children("puerta")) {

            System.out.println("pared: " + puerta.string("grosor"));

            for (Xml punto : puerta.children("punto")) {
                System.out.println("x,y: " + punto.string("x") + "," + punto.string("y"));
            }
        }
        for (Xml movimientos : mundo.child("agentes").children("movimientos")) {

            System.out.println("movimientos: " + movimientos.string("directorio"));

            for (Xml ruta : movimientos.child("rutas").children("ruta")) {

                System.out.println("ruta: " + ruta.string("nombre"));

                for (Xml punto : ruta.children("punto")) {
                    System.out.println("x,y: " + punto.string("x") + "," + punto.string("y"));
                }
            }
            for (Xml movimiento : movimientos.children("movimiento")) {
                System.out.println("movimiento: " + movimiento.string("nombre") +
                        movimiento.string("archivo"));

            }
        }

        for (Xml multitud : mundo.child("agentes").children("multitud")) {

            System.out.println("cantidad: " + multitud.string("cantidad"));

            for (Xml punto : multitud.child("área").children("punto")) {
                System.out.println("x,y: " + punto.string("x") + "," + punto.string("y"));
            }
            Xml varAncho = multitud.child("variación_ancho");
            System.out.println("[min,max]: [" + varAncho.string("min") + "," + varAncho.string("max") + "]");
            Xml varLargoAncho = multitud.child("variación_relacion_largo_ancho");
            System.out.println("[min,max]: [" + varLargoAncho.string("min") + "," + varLargoAncho.string("max") + "]");
        }

    }

    private void CargarMundo1() {
        orígenVisualización = new Double(0, 0);
        tamañoVisualización = new Double(500, 500);
        paredes = new ArrayList<ObjetoSimulación>();
        ObjetoSimulación nuevoObjeto;
        agentes = new ArrayList<Agente>();

        Point2D.Double p1 = new Point2D.Double(50, 200);
        Point2D.Double p2 = new Point2D.Double(400, 200);
        Point2D.Double p3 = new Point2D.Double(400, 450);
        Point2D.Double p4 = new Point2D.Double(50, 250);
        Point2D.Double p5 = new Point2D.Double(350, 250);
        Point2D.Double p6 = new Point2D.Double(350, 450);

        Point2D.Double p7 = new Point2D.Double(250, 210);
        Point2D.Double p8 = new Point2D.Double(375, 225);
        Point2D.Double p9 = new Point2D.Double(375, 350);

        Point2D.Double p10 = new Point2D.Double(375, 495);

        nuevoObjeto = new Pared(p1, p2, 0.2f);
        paredes.add(nuevoObjeto);
        nuevoObjeto = new Pared(p2, p3, 0.2f);
        paredes.add(nuevoObjeto);
        nuevoObjeto = new Pared(p4, p5, 0.2f);
        paredes.add(nuevoObjeto);
        nuevoObjeto = new Pared(p5, p6, 0.2f);
        paredes.add(nuevoObjeto);

        nuevoObjeto = new Puerta(p1, p4, 0.2f);
        paredes.add(nuevoObjeto);
        nuevoObjeto = new Puerta(p6, p3, 0.2f);
        paredes.add(nuevoObjeto);

        ArrayList<Point2D.Double> ruta = new ArrayList<Point2D.Double>();
        ruta.add(new Point2D.Double(100, 100));
        ruta.add(new Point2D.Double(300, 100));
        Agente nuevoAgente = new Agente(p7, 5f, 2.5f);
        nuevoAgente.setDestino(p10);
        nuevoAgente.setRapidez(2f);
        nuevoAgente.setEstrategiaMovimiento(crearMovimiento(ruta, nuevoAgente));
        agentes.add(nuevoAgente);
        nuevoAgente = new Agente(p8, 5f, 2.5f);
        nuevoAgente.setDestino(p10);
        nuevoAgente.setRapidez(2f);
        nuevoAgente.setEstrategiaMovimiento(crearMovimiento(ruta, nuevoAgente));
        agentes.add(nuevoAgente);
        nuevoAgente = new Agente(p9, 5f, 2.5f);
        nuevoAgente.setDestino(p10);
        nuevoAgente.setRapidez(2f);
        nuevoAgente.setEstrategiaMovimiento(crearMovimiento(ruta, nuevoAgente));
        agentes.add(nuevoAgente);

        // agregar la referencia al mundo para todos los objetos
        for (ObjetoSimulación objetoSimulación : paredes) {
            objetoSimulación.setMundo(this);
        }
        for (Agente agente : agentes) {
            agente.setMundo(this);
        }

    }

    private void CargarMundo2() {
        orígenVisualización = new Double(0, 0);
        tamañoVisualización = new Double(500, 500);
        paredes = new ArrayList<ObjetoSimulación>();
        ObjetoSimulación nuevoObjeto;
        agentes = new ArrayList<Agente>();

        Point2D.Double p1;
        Point2D.Double p2;

        p1 = new Point2D.Double(250, 400);
        p2 = new Point2D.Double(250, 150);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 150);
        p2 = new Point2D.Double(400, 150);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 150);
        p2 = new Point2D.Double(200, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(100, 200);
        p2 = new Point2D.Double(200, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(100, 200);
        p2 = new Point2D.Double(100, 50);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(100, 50);
        p2 = new Point2D.Double(200, 50);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 50);
        p2 = new Point2D.Double(200, 100);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 100);
        p2 = new Point2D.Double(450, 100);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(450, 100);
        p2 = new Point2D.Double(450, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(450, 200);
        p2 = new Point2D.Double(300, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(300, 200);
        p2 = new Point2D.Double(300, 400);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(300, 400);
        p2 = new Point2D.Double(250, 400);
        nuevoObjeto = new Puerta(p1, p2, GROSOR_PARED * 8);
        paredes.add(nuevoObjeto);

        ArrayList<Point2D.Double> ruta = new ArrayList<Point2D.Double>();
        ruta.add(new Point2D.Double(190, 125));
        ruta.add(new Point2D.Double(400, 145));
        ruta.add(new Point2D.Double(400, 155));
        ruta.add(new Point2D.Double(300, 195));
        ruta.add(new Point2D.Double(275, 360));
        ruta.add(new Point2D.Double(275, 450));

        Agente nuevoAgente = new Agente(new Point2D.Double(150, 100), 0.8f, 0.4f);
        nuevoAgente.setEstrategiaMovimiento(crearMovimiento(ruta, nuevoAgente));
        agentes.add(nuevoAgente);
        nuevoAgente = new Agente(new Point2D.Double(150, 125), 0.8f, 0.4f);
        nuevoAgente.setEstrategiaMovimiento(crearMovimiento(ruta, nuevoAgente));
        agentes.add(nuevoAgente);
        nuevoAgente = new Agente(new Point2D.Double(150, 190), 0.8f, 0.4f);
        nuevoAgente.setEstrategiaMovimiento(crearMovimiento(ruta, nuevoAgente));
        agentes.add(nuevoAgente);

        // agregar la referencia al mundo para todos los objetos
        for (ObjetoSimulación objetoSimulación : paredes) {
            objetoSimulación.setMundo(this);
        }

        for (Agente agente : agentes) {
            agente.setMundo(this);
        }
    }

    private void CargarMundo3() {
        orígenVisualización = new Double(0, 0);
        tamañoVisualización = new Double(500, 500);
        paredes = new ArrayList<ObjetoSimulación>();
        ObjetoSimulación nuevoObjeto;
        agentes = new ArrayList<Agente>();

        Point2D.Double p1;
        Point2D.Double p2;

        p1 = new Point2D.Double(250, 400);
        p2 = new Point2D.Double(250, 150);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 150);
        p2 = new Point2D.Double(400, 150);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 130);
        p2 = new Point2D.Double(200, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(100, 200);
        p2 = new Point2D.Double(200, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(100, 200);
        p2 = new Point2D.Double(100, 50);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(100, 50);
        p2 = new Point2D.Double(200, 50);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 50);
        p2 = new Point2D.Double(200, 120);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(200, 100);
        p2 = new Point2D.Double(450, 100);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(450, 100);
        p2 = new Point2D.Double(450, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(450, 200);
        p2 = new Point2D.Double(300, 200);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(300, 200);
        p2 = new Point2D.Double(300, 400);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        // obstáculo
        p1 = new Point2D.Double(300, 150);
        p2 = new Point2D.Double(330, 130);
        nuevoObjeto = new Pared(p2, p1, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        p1 = new Point2D.Double(330, 130);
        p2 = new Point2D.Double(340, 120);
        nuevoObjeto = new Pared(p1, p2, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        // puerta
        p1 = new Point2D.Double(300, 400);
        p2 = new Point2D.Double(250, 400);
        nuevoObjeto = new Puerta(p1, p2, GROSOR_PARED * 8);
        paredes.add(nuevoObjeto);

        ArrayList<Point2D.Double> ruta = new ArrayList<Point2D.Double>();
        ruta.add(new Point2D.Double(210, 125));
//        ruta.add(new Point2D.Double(405, 145));
        ruta.add(new Point2D.Double(410, 150));
        ruta.add(new Point2D.Double(290, 200));
        ruta.add(new Point2D.Double(275, 360));
        ruta.add(new Point2D.Double(275, 450));

        construirMultitud(ruta, new Double(105, 55), new Double(90, 140), 100, 0.5, 0.7);

        // agregar la referencia al mundo para todos los objetos
        for (ObjetoSimulación objetoSimulación : paredes) {
            objetoSimulación.setMundo(this);
        }

        for (Agente agente : agentes) {
            agente.setMundo(this);
        }
    }

    private void construirMultitud(ArrayList<Double> ruta, Point2D.Double orígenVentana,
            Point2D.Double tamañoVentana, int cantidad, double minRadio, double maxRadio) {

        for (int i = 0; i < cantidad; i++) {
            double x, y, ancho, largo;
            Point2D.Double orientación = new Double();
            Random generador = new Random();
            x = númeroAleatorio(orígenVentana.x, orígenVentana.x + tamañoVentana.x, generador);
            y = númeroAleatorio(orígenVentana.y, orígenVentana.y + tamañoVentana.y, generador);
            orientación.x = númeroAleatorio(0, 1, generador);
            orientación.y = númeroAleatorio(0, 1, generador);
            ancho = númeroAleatorio(minRadio, maxRadio, generador);
            largo = ancho * númeroAleatorio(0.4, 0.7, generador); // gordos y flacos
//            ancho = 10;
//            largo = 0.1;
            Agente nuevoAgente = new Agente(new Point2D.Double(x, y), ancho, largo);
            nuevoAgente.setEstrategiaMovimiento(crearMovimiento(ruta, nuevoAgente));
            nuevoAgente.setOrientación(orientación);
            if (nuevoAgente.colisionaConAgentes(agentes).isEmpty()) {
                agentes.add(nuevoAgente);
            } else {
                i--;
            }
        }
    }

    private double númeroAleatorio(double rangoIni, double rangoFin, Random generador) {

        double num = generador.nextDouble(); // de 0.0 a 1.0
        double resultado;
        resultado = num * (rangoFin - rangoIni) + rangoIni;
        return resultado;

    }

    private MovimientoHelbing crearMovimiento(ArrayList<Point2D.Double> ruta, Agente agente) {
        MovimientoHelbing nuevoMovimiento = new MovimientoHelbing(this, agente);

        nuevoMovimiento.setRuta(ruta);
        return nuevoMovimiento;
    }

    private void CargarMundoCalibración() {
        paredes = new ArrayList<ObjetoSimulación>();
        ObjetoSimulación nuevoObjeto;
        agentes = new ArrayList<Agente>();

        Point2D.Double puntoNuevo;
        Point2D.Double puntoAnterior;

        puntoAnterior = new Point2D.Double(0, 0);
        puntoNuevo = new Point2D.Double(0, 500);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        puntoAnterior = puntoNuevo;
        puntoNuevo = new Point2D.Double(500, 500);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        puntoAnterior = puntoNuevo;
        puntoNuevo = new Point2D.Double(500, 0);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);
        puntoAnterior = puntoNuevo;

        puntoNuevo = new Point2D.Double(0, 0);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);
        puntoAnterior = puntoNuevo;

        puntoNuevo = new Point2D.Double(500, 500);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        puntoAnterior = new Point2D.Double(500, 0);
        puntoNuevo = new Point2D.Double(0, 500);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        puntoAnterior = new Point2D.Double(250, 0);
        puntoNuevo = new Point2D.Double(250, 500);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        puntoAnterior = new Point2D.Double(0, 250);
        puntoNuevo = new Point2D.Double(500, 250);
        nuevoObjeto = new Pared(puntoAnterior, puntoNuevo, GROSOR_PARED);
        paredes.add(nuevoObjeto);

        // agregar la referencia al mundo para todos los objetos
        for (ObjetoSimulación objetoSimulación : paredes) {
            objetoSimulación.setMundo(this);
        }

        for (Agente agente : agentes) {
            agente.setMundo(this);
        }
    }
}

