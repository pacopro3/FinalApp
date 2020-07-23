
package supernodo;

import headers.objArchivo;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface Archivo extends Remote {
    boolean Actualizar(ArrayList<objArchivo> arreglo) throws RemoteException, Exception;
    ArrayList<objArchivo> buscarArchivo(String archivo) throws RemoteException, Exception;
    ArrayList<objArchivo> getLocalArchivo() throws RemoteException, Exception;
}