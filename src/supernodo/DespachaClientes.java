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
                              "file:/c:/Temp/" + String.valueOf(idcliente)+"/");
            // Ligamos el objeto remoto en el registro
            Registry registry = LocateRegistry.getRegistry(null);
            registry.bind(String.valueOf(idcliente), stub);
            registry.rebind(String.valueOf(idcliente), stub);
            String [] comprueba = registry.list();
            System.err.println("Despachame ya!!");
            for(int i=0;i<comprueba.length;i++){
                System.out.println("Despacha " + i + ":" + comprueba[i]);
            }
            System.err.println("Conexión a traves del puerto:" + idcliente + " lista...");
        } catch (Exception e){
            System.err.println("Excepción del servidor: " +
            e.toString());
            e.printStackTrace();
            }
        socket.close();
     }
   }