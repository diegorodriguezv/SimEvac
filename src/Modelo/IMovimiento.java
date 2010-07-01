
package Modelo;


import Física.Partícula;

public interface IMovimiento {

    public Partícula getPartícula() ;


    public void moverse (long milisegundos);

    public void calcularFuerzas();

}

