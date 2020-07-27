package supernodo;
import java.net.Socket;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DespachaClientes extends Thread {
    Socket socket;
    int idcliente,pto;
    Archivo stub;

    public DespachaClientes(int pto,Socket socket,int idcliente, Archivo stub) {
         this.socket = socket;
         this.idcliente = idcliente;
         this.stub=stub;
         this.pto=pto;
    }
 
     public void run() {
          try {
             despacha();
          }
          catch (Exception e) {
             e.printStackTrace(System.err);
          }
    }

    protected void despacha() throws Exception {
        try {
            System.setProperty("java.rmi.server.codebase",
                              "file:/c:/Temp/" + String.valueOf(idcliente)+"/");
            //System.setProperty("java.rmi.server.hostname","192.168.1.2");
            // Ligamos el objeto remoto en el registro
            Registry registry = LocateRegistry.getRegistry((55000-pto));
            registry.bind(String.valueOf(idcliente), stub);
            registry.rebind(String.valueOf(idcliente), stub);
            String [] comprueba = registry.list();
            System.err.println("Despachame ya!!");
            for(int i=0;i<comprueba.length;i++){
                System.out.println("Despacha " + i + ":" + comprueba[i]);
            }
            System.err.println("Conexión a traves del puerto:" + idcliente + " lista...");
        } catch (Exception e){
            System.err.println("Excepción del servidor: ");
            e.printStackTrace();
            }
        socket.close();
     }
   }