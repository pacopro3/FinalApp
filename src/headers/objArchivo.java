package headers;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sweet
 */
public class objArchivo implements Serializable{
    
    String name,md5,supernodo,nodo;
    
    public objArchivo() {
    }
    
    public objArchivo(String name, String md5, String supernodo, String nodo){
        this.md5=md5;
        this.name=name;
        this.nodo=nodo;
        this.supernodo=supernodo;
    }

    public String getMd5() {
        return md5;
    }

    public String getName() {
        return name;
    }

    public String getNodo() {
        return nodo;
    }

    public String getSupernodo() {
        return supernodo;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNodo(String nodo) {
        this.nodo = nodo;
    }

    public void setSupernodo(String supernodo) {
        this.supernodo = supernodo;
    }    
}
