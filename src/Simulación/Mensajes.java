/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulación;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Maneja un conjunto de mensajes.
 * @author diego
 */
public class Mensajes {

    private final List listaMensajes;

    public Mensajes() {
        listaMensajes = Collections.synchronizedList(new ArrayList<Mensaje>());
    }

    public void agregarMensaje(String descripción, String orígen) {
        Mensaje mensaje = new Mensaje(descripción, orígen);
        synchronized (listaMensajes) {
            listaMensajes.add(mensaje);
        }
    }

    public String obtenerCadena() {
        return obtenerCadena("\n\f", 0, null);
    }

    public String obtenerCadena(String separador, int milisegundos, String orígen) {

        StringBuilder bob = new StringBuilder();
        for (Mensaje mensaje : filtrarMensajes(milisegundos, orígen)) {
            bob.append(mensaje.toString() + separador);
        }
        return bob.toString();
    }

    private ArrayList<Mensaje> filtrarMensajes(int milisegundos, String orígen) {
        ArrayList<Mensaje> listaFiltrada = new ArrayList<Mensaje>();
        synchronized (listaMensajes) {
            for (Object objeto : listaMensajes) {
                Mensaje mensaje = (Mensaje) objeto;
                if (orígen == null ||
                        orígen.isEmpty() ||
                        mensaje.getOrígen().equals(orígen)) {
                    if (milisegundos == 0 ||
                            mensaje.getCreación().getTime() + milisegundos >= ((new Date()).getTime())) {
                        listaFiltrada.add(mensaje);
                    }
                }
            }
        }
        return listaFiltrada;
    }

    public void EliminarTodos() {
        synchronized (listaMensajes) {
            listaMensajes.clear();
        }
    }

    public void pintarMensajes(Graphics2D g2d, Point orígen, int milisegundos) {
        int orígenLineaX = orígen.x;
        int orígenLineaY = orígen.y;
        int grosorLínea = 10;

        for (Mensaje mensaje : filtrarMensajes(milisegundos, null)) {
            String línea = mensaje.getDescripción();
            g2d.setColor(mensaje.getColor());
            g2d.drawString(línea, orígenLineaX, orígenLineaY);
            orígenLineaY += grosorLínea;
        }
    }
}
