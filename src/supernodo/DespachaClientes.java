package supernodo;
import java.net.Socket;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DespachaClientes extends Thread {
    Socket socket;
    int idcliente;
    Archivo stub;

    public DespachaClientes(Socket socket,int idcliente, Archivo stub) {
         this.socket = socket;
         this.idcliente = idcliente;
         this.stub=stub;
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
                              "file:/c:/Temp/Archivo" + idcliente + "/");
            // Ligamos el objeto remoto en el registro
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Archivo" + idcliente, stub);
            System.err.println("Conexión a traves del puerto:" + idcliente + " lista...");
        } catch (Exception e) {
            System.err.println("Excepción del servidor: " +
            e.toString());
            e.printStackTrace();
            }
        socket.close();
     }
   }