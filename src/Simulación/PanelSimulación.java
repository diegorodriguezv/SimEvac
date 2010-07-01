
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulación;

//~--- non-JDK imports --------------------------------------------------------
import Modelo.FachadaMundo;
import Modelo.ObjetoSimulación;
import Modelo.Puerta;

//~--- JDK imports ------------------------------------------------------------
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

/**
 * Modela
 * @author diego
 */
public class PanelSimulación extends JPanel implements Runnable {

    public static final int ESCALA_MAX = 1000;
    public static final int ESCALA_MIN = 1;
    private Image imagenDB = null;
    private Mensajes mensajesEnPantalla;
    private Point2D.Double centroVisualización;
    private volatile boolean corriendo;
    private Point2D.Double dimensionesVisualización;
    private volatile long duracionCiclo;
    private AffineTransform transformadaVisualización;
    // variables para doble buffer
    private Graphics2D graphicsDB;
    private Thread hiloDeSimulacion;
    // tamaño del panel
    private FachadaMundo mundo;
    private volatile boolean pausado;
    private volatile long tiempoActual;
    private long tiempoEspera;
    private volatile boolean centrando = false;
    private Point posiciónAnteriorCentrado;

    public PanelSimulación() {
        this(true, 200, null);
    }

    public PanelSimulación(boolean pausado, long duracionCiclo, File archivo) {
        super();
        this.pausado = pausado;
        this.duracionCiclo = duracionCiclo;

        this.tiempoActual = 0;
        this.tiempoEspera = 10;
        mundo =
                new FachadaMundo(archivo);

        Point2D.Double orígen = mundo.getOrígenVisualización();
        Point2D.Double tamaño = mundo.getTamañoVisualización();

        //
        tamaño.y = tamaño.x;

        centroVisualización =
                new Point2D.Double(
                orígen.x + tamaño.x / 2,
                orígen.y + tamaño.y / 2);

        dimensionesVisualización =
                tamaño;

        mensajesEnPantalla =
                new Mensajes();

//        setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        terminarCentrado();
                        verificarColisión(e.getX(), e.getY());
                        break;

                    case MouseEvent.BUTTON2:
                        terminarCentrado();
                        centrarEnPosición(e.getX(), e.getY());
                        break;

                    case MouseEvent.BUTTON3:
                        iniciarCentrado(e.getX(), e.getY());
                        break;

                }


            }
        });

        addMouseWheelListener(new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int veces = e.getWheelRotation();

                procesarRueda(veces, e.getX(), e.getY());
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if (centrando) {
                    moverCentro(e.getX(), e.getY());
                }

            }
        });
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                iniciarDB();

                System.out.println("w: " + e.getComponent().getWidth() + " h: " + e.getComponent().getHeight()
                        + " componentes: " + ((Container) e.getComponent()).getComponentCount() + " name:"
                        + e.getComponent().getName() + " classname:" + e.getComponent().getClass().getName());
                System.out.println("componentResized");


            }

            public void componentHidden(ComponentEvent e) {
                displayMessage(e.getComponent().getClass().getName() + " --- Hidden");
            }

            public void componentMoved(ComponentEvent e) {
                displayMessage(e.getComponent().getClass().getName() + " --- Moved");
            }

            public void componentShown(ComponentEvent e) {
                displayMessage(e.getComponent().getClass().getName() + " --- Shown");

            }

            public void displayMessage(String s) {
                System.out.println(s);
            }
        });

    }

    private void iniciarCentrado(int x, int y) {
        centrando = !centrando;
        if (centrando) {
            posiciónAnteriorCentrado = new Point(x, y);
        }

    }

    /**
     * Inicia la imágen y el objeto de gráficos para Doble Buffer.
     * @return Si se pudo iniciar el Doble Buffer
     */
    private boolean iniciarDB() {

        imagenDB = createImage(this.getWidth(), this.getHeight());

        System.out.println("imagendb w: " + this.getWidth() + " h: " + this.getHeight()
                + " componentes: " + this.getComponentCount() + " name:"
                + this.getName() + " classname:" + this.getClass().getName());

        if (imagenDB == null) {
            System.out.println("imagenDB es null");
            return false;
        } else {
            graphicsDB = (Graphics2D) imagenDB.getGraphics();
            graphicsDB.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return true;
    }

    private void terminarCentrado() {
        centrando = false;

    }

    private void moverCentro(int x, int y) {
        if (corriendo) {
//            mensajesEnPantalla.agregarMensaje(String.format("mover: x:%d y:%d", x, y), "mouse");
            Point2D.Double anterior = pantallaAUserSpace(posiciónAnteriorCentrado.x, posiciónAnteriorCentrado.y);
            Point2D.Double actual = pantallaAUserSpace(x, y);

            centroVisualización.x += anterior.x - actual.x;
            centroVisualización.y += anterior.y - actual.y;
            posiciónAnteriorCentrado =
                    new Point(x, y);

//            mensajesEnPantalla.agregarMensaje(String.format("nuevo centro x:%.2f y:%.2f", centroVisualización.x,
//                    centroVisualización.y), "mouse");
        }

    }

    private void procesarRueda(int veces, int x, int y) {
        if (corriendo) {
//            mensajesEnPantalla.agregarMensaje(String.format("rueda: veces:%d x:%d y:%d", veces, x, y), "mouse");
            if (veces < 0) {
                for (int i = 0; i
                        > veces; i--) {
                    if (dimensionesVisualización.x * 1.1 > ESCALA_MIN
                            && dimensionesVisualización.x * 1.1 < ESCALA_MAX) {
                        dimensionesVisualización.x *= 1.1;
                        dimensionesVisualización.y *= 1.1;
                    }

                }
            } else {
                for (int i = 0; i
                        < veces; i++) {
                    if (dimensionesVisualización.x / 1.1 > ESCALA_MIN
                            && dimensionesVisualización.x / 1.1 < ESCALA_MAX) {
                        dimensionesVisualización.x /= 1.1;
                        dimensionesVisualización.y /= 1.1;
                    }

                }
            }
//            mensajesEnPantalla.agregarMensaje(String.format("nuevo centro x:%.2f y:%.2f", dimensionesVisualización.x,
//                    dimensionesVisualización.y), "mouse");
        }
    }

    private void verificarColisión(int x, int y) {
        if (corriendo) {
            Point2D.Double posiciónClic = pantallaAUserSpace(x, y);
            ArrayList<ObjetoSimulación> cercanos = mundo.getObjetosCercanos(
                    posiciónClic,
                    0.8);

//            mensajesEnPantalla.agregarMensaje(String.format("colisión:%b x:%.2f y:%.2f", !cercanos.isEmpty(), posiciónClic.x, posiciónClic.y), "mouse");
            StringBuilder bob = new StringBuilder();
            for (ObjetoSimulación objetoSimulación : cercanos) {
                bob.append(objetoSimulación.toString() + ", ");

            }

            if (bob.length() > 0) {
                bob.delete(bob.length() - 2, bob.length());
            }

            mensajesEnPantalla.agregarMensaje(String.format("colisión:%b x:%.2f y:%.2f", !cercanos.isEmpty(), posiciónClic.x, posiciónClic.y), "mouse");
            mensajesEnPantalla.agregarMensaje(String.format("objetos:%s", bob.toString()), "mouse");
        }

    }

    private void centrarEnPosición(int x, int y) {
        if (corriendo) {
//            mensajesEnPantalla.agregarMensaje(String.format("clic: x:%d y:%d", x, y), "mouse");
            centroVisualización = pantallaAUserSpace(x, y);

            mensajesEnPantalla.agregarMensaje(String.format("nuevo centro x:%.2f y:%.2f", centroVisualización.x,
                    centroVisualización.y), "mouse");
        }

    }

    /**
     * @return está pausado?
     */
    public boolean isPausado() {
        return pausado;
    }

    @Override
    public void addNotify() {
        super.addNotify();

        System.out.println("w: " + this.getWidth() + " h: " + this.getHeight()
                + " componentes: " + this.getComponentCount() + " name:"
                + this.getName() + " classname:" + this.getClass().getName());
        if (this.getHeight() == 0 || this.getWidth() == 0) {

            this.setSize(this.getMinimumSize());

            System.out.println("cambio tamaño w: " + this.getWidth() + " h: " + this.getHeight()
                    + " componentes: " + this.getComponentCount() + " name:"
                    + this.getName() + " classname:" + this.getClass().getName());
        }
        iniciarDB();
        iniciarSimulación();

        System.out.println("addnotify");

    }

    /**
     * @param pausado pausar?
     */
    public void setPausado(boolean pausado) {
        this.pausado = pausado;
    }

    /**
     * @return la duracion del Ciclo
     */
    public long getDuracionCiclo() {
        return duracionCiclo;
    }

    /**
     * @param duracionCiclo la duracion del Ciclo
     */
    public void setDuracionCiclo(long duracionCiclo) {
        this.duracionCiclo = duracionCiclo;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagenDB != null) {
            g.drawImage(imagenDB, 0, 0, null);
        }

    }

    /**
     * @return el tiempoActual
     */
    public long getTiempoActual() {
        return tiempoActual;
    }

    private void pintarMarco() {
        graphicsDB.drawString(String.format("Tiempo: %1$,2d ms", getTiempoActual()), 20, 20);
        mensajesEnPantalla.pintarMensajes(graphicsDB, new Point(20, 30), 5000);
//        pintarEscala(250, 490, 200, 10);
        pintarAutoEscala(0, getHeight() - 20, getWidth(), 20);
        pintarMiraDelCentro();

    }

    private void pintarMiraDelCentro() {
        graphicsDB.setColor(Color.LIGHT_GRAY);
        int maxAncho = 10, minAncho = 2;
        //horizontales
        graphicsDB.drawLine(getWidth() / 2 - maxAncho, getHeight() / 2 + 0, getWidth() / 2 - minAncho, getHeight() / 2 + 0);
        graphicsDB.drawLine(getWidth() / 2 + minAncho, getHeight() / 2 + 0, getWidth() / 2 + maxAncho, getHeight() / 2 + 0);

        //verticales
        graphicsDB.drawLine(getWidth() / 2 + 0, getHeight() / 2 - maxAncho, getWidth() / 2 + 0, getHeight() / 2 - minAncho);
        graphicsDB.drawLine(getWidth() / 2 + 0, getHeight() / 2 + minAncho, getWidth() / 2 + 0, getHeight() / 2 + maxAncho);
    }

    private void pintarAutoEscala(int x, int y, int ancho, int alto) {
        graphicsDB.setColor(Color.BLUE);
        graphicsDB.drawLine(x, y + alto / 2, x + ancho, y + alto / 2);

        double escalaVisualización = dimensionesVisualización.x / this.getWidth();

        double longitudEscala = ancho * escalaVisualización;

        // Dividir la longitud en pedazos apropiados

        // encontrar la posición de la primera cifra significativa
        String cadena = Double.toString(longitudEscala);
        int posDecimal = cadena.indexOf('.') - 1;
        int parte = Integer.parseInt("1" + repetirCaracter('0', posDecimal));

        int numPartes = (int) longitudEscala / parte;
        for (int i = 0; i
                <= numPartes; i++) {
            int parteAEscala = (int) (parte / escalaVisualización);
            int desplazamiento;
            if (i == 0) {
                desplazamiento = 1 * posDecimal;
            } else {
                desplazamiento = -5 * posDecimal;
            }

            graphicsDB.drawString(Integer.toString(parte * i), x + i * parteAEscala + desplazamiento, y);
            graphicsDB.drawLine(x + i * parteAEscala, y + alto / 2 - 3, x + i * parteAEscala, y + alto / 2 + 3);
        }

//        graphicsDB.drawString(String.format("%d %d", parte, numPartes), x, y);
    }

    public static String repetirCaracter(
            char c, int i) {
        String tst = "";
        for (int j = 0; j
                < i; j++) {
            tst = tst + c;
        }

        return tst;
    }

    private void pintarEscala(int x, int y, int ancho, int alto) {
        graphicsDB.setColor(Color.BLUE);
        graphicsDB.drawLine(x, y + alto / 2, x + ancho, y + alto / 2);

        double longitudEscala = ancho * dimensionesVisualización.x / this.getWidth();
        graphicsDB.drawString(String.format("%.2f", longitudEscala), x, y);
    }

    /**
     * Convierte una coordenada en la pantalla en una coordenada del user space.
     * Se utiliza para calcular en que punto se hace clic.
     * @param x
     * @param y
     * @return La coordenada en unidades del mundo.
     */
    private Point2D.Double pantallaAUserSpace(int x, int y) {
        Point2D.Double orígen = new Point2D.Double(x, y);
        Point2D.Double resultado = new Point2D.Double();
        try {
            if (transformadaVisualización != null) {
                transformadaVisualización.inverseTransform(orígen, resultado);
            }



        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(PanelSimulación.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(
                    "errrrorrrr");
        } catch (Exception ex) {
            Logger.getLogger(PanelSimulación.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(
                    "errrrorrrr2");
        }
        return resultado;
    }

    /**
     * Convierte una coordenada del user space en una coordenada de la pantalla.
     * @param coordenada Coordenada en el user space
     * @return La coordenada en unidades del  user space.
     */
    private Point2D.Double userSpaceAPantalla(Point2D.Double coordenada) {
        Point2D.Double resultado = new Point2D.Double();
        if (transformadaVisualización != null) {
            transformadaVisualización.transform(coordenada, resultado);
        }

        return resultado;
    }

    public void run() {
        corriendo = true;
        if (graphicsDB == null) {
            iniciarDB();

            System.out.println("run");
        }
        if (graphicsDB == null) {
            System.out.println("graphicsDB == null en run");
        }

        while (corriendo) {
            actualizarSimulación();
            repaint();

            try {
                Thread.sleep(tiempoEspera);
            } catch (InterruptedException ex) {
                Logger.getLogger(PanelSimulación.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void actualizarSimulación() {

        // clear the background
        graphicsDB.setColor(Color.WHITE);
        graphicsDB.fillRect(0, 0, this.getWidth(), this.getHeight());

        // draw game elements
        // ...
        if (!pausado) {
            tiempoActual += duracionCiclo;
            mundo.actualizarTodos(duracionCiclo);
            pintarMundo();

            graphicsDB.setColor(Color.GREEN);
        } else {
            pintarMundo();
            graphicsDB.setColor(Color.RED);
        }

        pintarMarco();
    }

    public void pintarMundo() {

        AffineTransform atOriginal = graphicsDB.getTransform();

        escalarYtrasladarAlCentro();

        for (int i = 0; i
                < mundo.getObjetos().size(); i++) {
            ObjetoSimulación objeto = mundo.getObjetos().get(i);

            pintarObjeto(objeto);
        }

        for (int i = 0; i
                < mundo.getAgentes().size(); i++) {
            ObjetoSimulación objeto = mundo.getAgentes().get(i);

            pintarObjeto(objeto);
        }

        graphicsDB.setTransform(atOriginal);
    }

    private void pintarObjeto(ObjetoSimulación objeto) {
        String claseObjeto = objeto.getClass().getName();

        if (claseObjeto.equals("Modelo.Agente")) {
            graphicsDB.setColor(Color.magenta);
            pintarContorno(graphicsDB, objeto);
        }

        if (claseObjeto.equals("Modelo.Puerta")) {
            Puerta puerta = (Puerta) objeto;

            if (!puerta.detectarAgentesAtravesando().isEmpty()) {
                graphicsDB.setColor(Color.red);
            } else {
                graphicsDB.setColor(Color.orange);
            }

            pintarContorno(graphicsDB, objeto);
        }

        if (claseObjeto.equals("Modelo.Pared")) {
            graphicsDB.setColor(Color.black);
            pintarContorno(graphicsDB, objeto);
        }

    }

    private void pintarContorno(Graphics2D g2d, ObjetoSimulación objeto) {
        for (Line2D miLínea : objeto.getContorno()) {
            g2d.setStroke(new BasicStroke(0.01f));
            g2d.draw(miLínea);
        }

    }

    public void iniciarSimulación() {
        if ((hiloDeSimulacion == null) || !corriendo) {
            hiloDeSimulacion = new Thread(this, "HiloDeSimulación");
            hiloDeSimulacion.start();
        }
    }

    public void terminarSimulación() {
        corriendo = false;
    }

    private void escalarYtrasladarAlCentro() {
        AffineTransform transformadaEscala = graphicsDB.getTransform();
        transformadaEscala.setToScale(this.getWidth() / dimensionesVisualización.x,
                this.getWidth() / dimensionesVisualización.y);

        double escalaX, escalaY;
        escalaX =
                this.getWidth() / dimensionesVisualización.x;
        escalaY = escalaX;
//                this.getHeight() / dimensionesVisualización.y;

        AffineTransform transformadaTraslación = new AffineTransform();

        transformadaTraslación.setToTranslation((dimensionesVisualización.x / 2 - centroVisualización.x) * escalaX,
                (dimensionesVisualización.y / 2 - centroVisualización.y) * escalaY);

        transformadaEscala.preConcatenate(transformadaTraslación);
        transformadaVisualización =
                transformadaEscala;
        graphicsDB.setTransform(transformadaVisualización);

    }
}

