/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supernodo;
import headers.*;
import interfaz.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import supernodo.Archivo;

/**
 *
 * @author Sweet
 */
public class clientesupernodoRMI extends ConexionRMI {

    private String host;
    private IntSupernodo isn;
    private String MD5;
    int id,ptoL,ptoS;
    String directorio,a,b;
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
        array=new ArrayList<>();
	try {
            dos = new DataOutputStream(cs.getOutputStream());
            dos.writeInt(ptoL);
            System.err.println("PtoN:" + ptoL + "\nID:" + id + "\nhost:" + host);
            Registry registry = LocateRegistry.getRegistry();
            Thread.sleep(1000);
            stub = (Archivo)registry.lookup(String.valueOf(ptoL));
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
                array = stub.getLocalArchivo();
                isn.removeMasivo(a);
                objArchivo aux = new objArchivo();
                if(!array.isEmpty())
                    for(int i=0;i<array.size();i++){
                        aux=array.get(i);
                        isn.addTable(aux.getName(), aux.getMd5(), aux.getSupernodo(), aux.getNodo());
                    }
                Thread.sleep(5000);
            }
        }catch(Exception e){
            e.printStackTrace();
        }   
    }   
}
