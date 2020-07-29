/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supernodo;
import headers.*;
import interfaz.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 *
 * @author Sweet
 */
public class clientesupernodoRMI extends ConexionRMI {

    private String host;
    private IntSupernodo isn;
    int ptoL,ptoS;
    Archivo stub;
    ArrayList<objArchivo> array;
    objArchivo ap;
    
    public clientesupernodoRMI(String tipo, int pto, String hhost,IntSupernodo isn) throws IOException {
        super(tipo,pto,hhost);
        this.host=hhost;
        this.ptoS=pto;
        this.isn=isn;
        ptoL=isn.getPuerto();
    }

    public void MainCliente(){
        DataInputStream dis;
        DataOutputStream dos; 
	try {
            dos = new DataOutputStream(cs.getOutputStream());
            dos.writeInt(ptoL);
            Registry registry = LocateRegistry.getRegistry((55000-ptoS));
            Thread.sleep(2000);
            stub = (Archivo)registry.lookup(String.valueOf(ptoL));
            System.out.println("PtoS:" + ptoS +"\nPtoL:" + ptoL);
        } catch (Exception e) {
            System.err.println("Excepción del cliente: " +
                             e.toString());
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try{
            while(true){
                array = new ArrayList<>(stub.getLocalArchivo());
                isn.removeMasivoSN(String.valueOf(ptoS));
                objArchivo aux = new objArchivo();
                if(!array.isEmpty())
                    for(int i=0;i<array.size();i++){
                        aux=array.get(i);
                        isn.addTable(aux.getName(), aux.getMd5(), aux.getSupernodo(), aux.getNodo());
                        System.out.println("Longitud:" +  array.size());
                        Thread.sleep(300);
                    }
                Thread.sleep(5000);
            }
        }catch(Exception e){
            System.err.println("Error al conectar con el servidor:" + ptoS+", se cierra la conexión");
            isn.removeMasivoSN(String.valueOf(ptoS));
            this.interrupt();
        }   
    }   
}
