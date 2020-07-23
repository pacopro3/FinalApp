/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supernodo;
import headers.*;
import java.io.IOException;
import java.rmi.RemoteException;
import interfaz.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
/**
 *
 * @author Sweet
 */
public class supernodoRMI extends ConexionRMI implements Archivo{
    IntSupernodo is;
    
    public supernodoRMI(String tipo, int pto, IntSupernodo is) throws IOException {
        super(tipo,pto);
        this.is=is;
    }
    
    public void MainServidor(supernodoRMI s) throws IOException {
          Archivo stub = null;
          int idcliente;
            try {
                        //puerto default del rmiregistry
                        java.rmi.registry.LocateRegistry.createRegistry(1099); 
                        System.out.println("RMI registro listo.");
                        stub = (Archivo) UnicastRemoteObject.exportObject(s, 0);
            } catch (Exception e) {
                        System.out.println("Excepcion RMI del registry:");
                        e.printStackTrace();
                        System.exit(0);
            }//catch
                while(true) {
                    DataOutputStream dos;            
                    System.out.println("Esperando..."); // Esperando conexión
                    Socket cs = ss.accept();
                    idcliente = cs.getPort();
                    dos = new DataOutputStream(cs.getOutputStream());
                    dos.writeInt(idcliente);
                    System.out.println("Cliente" + idcliente + " en línea");
                    DespachaClientes hilo = new DespachaClientes(cs,idcliente,stub);
                    hilo.start();
                }
      }
    
    @Override
    public void run(){
        
    }

    @Override
    public boolean Actualizar(ArrayList<objArchivo> arreglo) throws RemoteException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<objArchivo> buscarArchivo(String archivo) throws RemoteException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<objArchivo> getLocalArchivo() throws RemoteException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
