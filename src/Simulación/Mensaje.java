/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulación;

import java.awt.Color;
import java.util.Date;

/**
 * Representa un mensaje para mostrar al usuario
 * @author diego
 */
public class Mensaje {

    private String descripción;
    private Color color;
    private String orígen;
    private Date creación;

    public Mensaje(String descripción, String orígen) {
        this.descripción = descripción;
        this.orígen = orígen;
        this.color = Color.BLACK;

        this.creación = new Date();
    }

    public Color getColor() {
        return color;
    }

    public Date getCreación() {
        return creación;
    }

    public String getDescripción() {
        return descripción;
    }

    public String getOrígen() {
        return orígen;
    }
}
