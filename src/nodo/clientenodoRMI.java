/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodo;
import headers.*;
import interfaz.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import supernodo.Archivo;

/**
 *
 * @author Sweet
 */
public class clientenodoRMI extends ConexionRMI {

    private String host;
    private IntNodo in;
    int id;
    String directorio,a,b;
    Archivo stub;
    ArrayList<objArchivo> array, apoyo;
    objArchivo ap;
    
    public clientenodoRMI(String tipo, int pto, String hhost,IntNodo in) throws IOException {
        super(tipo,pto,hhost);
        this.host=hhost;
        this.in=in;
        DataInputStream dis;
        array=new ArrayList<>();
        apoyo=new ArrayList<>();
	try {
        dis = new DataInputStream(cs.getInputStream());
        id = dis.readInt();
        System.out.println("El id es: "+ id);
        String current = new java.io.File( "." ).getCanonicalPath();
        directorio = current + "\\src\\Folders\\" + id + "\\";
	    Registry registry = LocateRegistry.getRegistry(host);	
            //también puedes usar getRegistry(String host, int port)
            this.stub = (Archivo) registry.lookup("Archivo" + id);
            boolean result=false;
            File file = new File(directorio);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            System.err.println("Excepción del cliente: " +
                             e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        
    }
    
    
    

    public void buscarArchivo(String arch) throws IOException{
        try {
            array=stub.buscarArchivo(arch);
            if(array.isEmpty()){
                //print que no se encontró el archivo
            }else{
                //se verifica cuantos archivos existen
                for(int i=0;i<array.size();i++){
                    ap=array.get(i);
                    if(apoyo.size()==0)apoyo.add(ap);
                    for(int j=0;j<apoyo.size();j++){
                        if(!apoyo.get(j).equals(ap)){
                            apoyo.add(ap);
                        }
                    }
                }
                
                if(!(apoyo.size()==1)){
                    //si no es igual a 1 significa que hay varios archivos con el mismo nombre
                    //Le damos a elegir entre uno
                }
                
                ap=apoyo.get(0);
                //generar conexión con el archivo que sea seleccionado
                int contador=0;
                for(int i=0;i<array.size();i++){
                    if(array.get(i).equals(ap)){
                        contador++;
                    }
                }
                //el contador sirve para saber cuantas conexiones se van a necesitar para pedir la conexión
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
