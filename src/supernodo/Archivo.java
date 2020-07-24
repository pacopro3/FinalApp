
package supernodo;

import headers.objArchivo;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface Archivo extends Remote {
    boolean Actualizar(ArrayList<objArchivo> arreglo) throws RemoteException, Exception;
//Lo ejecuta el clienteRMI del nodo para actualizar cada cierto tiempo los archivos que tiene en su posesión
    ArrayList<objArchivo> buscarArchivo(String archivo) throws RemoteException, Exception;
//Lo ejecuta el clienteRMI del nodo para buscar un archivo que se encuentre en la red de otros nodos, recibe una lista de la ubicación de su busqueda
    ArrayList<objArchivo> getLocalArchivo() throws RemoteException, Exception;
    //Lo ejecuta el ClienteRMI del Supernodo para que actualice la lista que tiene otro supernodo de sus nodos hijos 
}