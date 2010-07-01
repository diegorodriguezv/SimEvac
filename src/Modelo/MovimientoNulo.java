package Modelo;

import Física.Partícula;


public class MovimientoNulo implements IMovimiento {


    public MovimientoNulo() {
    }


    public void moverse(long milisegundos) {
    }

    public Partícula getPartícula() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void calcularFuerzas() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

