/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodo;
import headers.*;
import interfaz.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
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
public class clientenodoRMI extends ConexionRMI {

    private String host;
    private IntNodo in;
    private String MD5;
    int id,ptoN,ptoS;
    String directorio,a,b;
    Archivo stub;
    ArrayList<objArchivo> array, apoyo;
    objArchivo ap;
    
    public clientenodoRMI(String tipo, int pto, String hhost,IntNodo in) throws IOException {
        super(tipo,pto,hhost);
        this.host=hhost;
        this.ptoS=pto;
        this.in=in;
        ptoN=in.getPuerto();
        DataInputStream dis;
        array=new ArrayList<>();
        apoyo=new ArrayList<>();
	try {
        dis = new DataInputStream(cs.getInputStream());
        id = dis.readInt();
        System.err.println("PtoN:" + ptoN + "\nID:" + id);
        String current = new java.io.File( "." ).getCanonicalPath();
        directorio = current + "\\src\\Folders\\" + id + "\\";
	    Registry registry = LocateRegistry.getRegistry(host);	
            //también puedes usar getRegistry(String host, int port)
            this.stub = (Archivo) registry.lookup("Archivo" + id);
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
        try{
            ArrayList<objArchivo> r = new ArrayList<>();
            String current = new java.io.File( "." ).getCanonicalPath();
            directorio = current + "\\src\\Folders\\" + id + "\\";
            File file = new File(directorio);
            if (!file.exists()) {
                file.mkdir();
            }
            
            for (final File f : file.listFiles()) {
                objArchivo a = new objArchivo();
                a.setName(f.getName());
                a.setMd5(MD5Checksum.getMD5Checksum(f.getCanonicalPath()));
                a.setNodo("Localhost:" + String.valueOf(id));
                a.setSupernodo(host + String.valueOf(ptoS));
                r.add(a);
            }
            if(stub.Actualizar(r))            
                in.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> Se actualiza la info local en el supernodo");
            Thread.sleep(5000);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
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
                    ap=choseOne(apoyo);
                }else{
                    ap=apoyo.get(0);
                }
                
                //generar conexión con el archivo que sea seleccionado
                int contador=0;
                for(int i=0;i<array.size();i++){
                    if(array.get(i).equals(ap)){
                        contador++;
                    }
                }
                //el contador sirve para saber cuantas conexiones se van a necesitar para pedir la conexión
                //ejecuta conexiones simultaneas
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public objArchivo choseOne(ArrayList<objArchivo> ap){
    objArchivo a,aux;
    a=new objArchivo();
    JFrame frame = new JFrame("Seleccion");
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 500);
    frame.setLocation(430, 100);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // added code

    frame.add(panel);

    JLabel lbl = new JLabel("Selecciona el archivo que quieres descargar:");
    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
    //lbl.setVisible(true); // Not needed

    panel.add(lbl);

    String[] choices = new String[ap.size()];
    for(int i=0;i<ap.size();i++){
        aux=ap.get(i);
        choices[i]="Nombre:" + aux.getName() + "//MD5:" + aux.getMd5();
    }

    final JComboBox<String> cb = new JComboBox<String>(choices);

    cb.setMaximumSize(cb.getPreferredSize());
    cb.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(cb);
    JButton btn = new JButton("OK");
    btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(btn);
    btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String selected =(String) cb.getSelectedItem();
                String abc[]=selected.split(":");
                setMD5(abc[2]);
                frame.setVisible(false);
                frame.dispose();
            }
    });
    
    frame.setVisible(true);

        for(int i=0;i<ap.size();i++){
            aux=ap.get(i);
            if(getMD5().equals(aux.getMd5())){
                a=aux;
            }
        }
        return a;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }
    
    
}
