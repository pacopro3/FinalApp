
package supernodo;

import headers.objArchivo;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Archivo extends Remote {
    boolean Actualizar(objArchivo[] arreglo) throws RemoteException, Exception;
    objArchivo[] buscarArchivo(String archivo) throws RemoteException, Exception;
}
