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
    ArrayList<objArchivo> local;
    int conexiones;
    
    public supernodoRMI(String tipo, int pto, IntSupernodo is) throws IOException {
        super(tipo,pto);
        this.is=is;
        local = new ArrayList<>();
        conexiones = 2;
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
                    setConexiones(getConexiones()-1);
                }
      }
    
    @Override
    public void run(){
        
    }

    @Override
    public boolean Actualizar(ArrayList<objArchivo> arreglo) throws RemoteException, Exception {
        //se implementa metodo para la actualización de la información que llega del nodo que tenemos anclado
        try {
            //Crear codigo para juntar local arraylist con la arraylist nueva que llega y eliminar los que ya no se encuentren en la primera
            String del;
            ArrayList<objArchivo> apoyo=new ArrayList<>();
            objArchivo aux=arreglo.get(0);
            del=aux.getNodo();
            for(int i=0;i<local.size();i++){
                aux=local.get(i);
                if(!(aux.getNodo().equals(del))){
                    apoyo.add(aux);
                }
            }
            for(int i=0;i<arreglo.size();i++){
                apoyo.add(arreglo.get(i));
            }
            local.clear();
            local.addAll(apoyo);
            apoyo.clear();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ArrayList<objArchivo> buscarArchivo(String archivo) throws RemoteException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<objArchivo> getLocalArchivo() throws RemoteException, Exception {
        //damos return del arraylist que tenemos de las variables locales que tenemos 
        return local;
    }

    public int getConexiones() {
        return conexiones;
    }

    public void setConexiones(int conexiones) {
        this.conexiones = conexiones;
    }
    
    

    
}
