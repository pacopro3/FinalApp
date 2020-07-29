/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package headers;

import java.io.Serializable;

/**
 *
 * @author Sweet
 */
public class objPeticion implements Serializable{

    private int partes;
    private int posicion;
    private String name;
    private int origen;
    private int destino;
    
    public objPeticion() {
    }

    public objPeticion(int partes, int posicion, String name, int origen, int destino) {
        this.partes = partes;
        this.posicion = posicion;
        this.name = name;
        this.destino=destino;
        this.origen=origen;
    }

    public String getName() {
        return name;
    }

    public int getPartes() {
        return partes;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getDestino() {
        return destino;
    }

    public int getOrigen() {
        return origen;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPartes(int partes) {
        this.partes = partes;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public void setDestino(int destino) {
        this.destino = destino;
    }

    public void setOrigen(int origen) {
        this.origen = origen;
    }
    
    
    
}
