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
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    boolean espera;
    
    public clientenodoRMI(String tipo, int pto, String hhost,IntNodo in) throws IOException {
        super(tipo,pto,hhost);
        this.host=hhost;
        this.ptoS=pto;
        this.in=in;
        ptoN=in.getPuerto();
    }

    public void MainCliente(){
        DataInputStream dis;
        DataOutputStream dos; 
	try {
            dos = new DataOutputStream(cs.getOutputStream());
            dos.writeInt(ptoN);
            String current = new java.io.File( "." ).getCanonicalPath();
            directorio = current + "\\src\\Folders\\" + ptoN + "\\";
	    Registry registry = LocateRegistry.getRegistry((55000-ptoS));
            File file = new File(directorio);
            if (!file.exists()) {
                file.mkdir();
            }
            Thread.sleep(2000);
            stub = (Archivo)registry.lookup(String.valueOf(ptoN));
            
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
                ArrayList<objArchivo> r = new ArrayList<>();
                String current = new java.io.File( "." ).getCanonicalPath();
                directorio = current + "\\src\\Folders\\" + ptoN + "\\";
                File file = new File(directorio);

                for (final File f : file.listFiles()){
                    objArchivo a = new objArchivo();
                    a.setName(f.getName());
                    a.setMd5(MD5Checksum.getMD5Checksum(f.getCanonicalPath()));
                    a.setNodo(String.valueOf(ptoN));
                    a.setSupernodo(String.valueOf(ptoS));
                    r.add(a);
                }
                if(!(r.isEmpty()))
                    if(stub.Actualizar(r))            
                        in.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> Se actualiza la info local en el supernodo");
                Thread.sleep(10000);
            }
        }catch(Exception e){
            in.setVisible(false);
            in.dispose();
            this.interrupt();
            JOptionPane.showMessageDialog(null, "Se perdió la conexión con el supernodo, la aplicación se cerrará", "POPUP: Error de nodo", JOptionPane.INFORMATION_MESSAGE);
            System.exit(1);
        }
        
        
    }
    
    
    

    public void buscarArchivo(String arch) throws IOException{
            objArchivo ap = new objArchivo();
            ArrayList<objArchivo> array, apoyo;
            array = new ArrayList<>();
            apoyo = new ArrayList<>();
        try {
            in.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> Se realiza la solicitud de busqueda del archivo " + arch);
            String current = new java.io.File( "." ).getCanonicalPath();
            directorio = current + "\\src\\Folders\\" + ptoN + "\\" + arch;
                File file = new File(directorio);
                if(file.exists()){
                    in.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> El archivo con nombre: " + arch + " ya se encuentra en nuestro repositorio");
                    JOptionPane.showMessageDialog(null, "El archivo con nombre: " + arch + " ya se encuentra en nuestro repositorio", "POPUP: Busqueda de archivo", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    array= new ArrayList<>(stub.buscarArchivo(arch));
                    if(array.isEmpty()){
                        //print que no se encontró el archivo
                        in.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> El archivo con nombre: " + arch + " no fue encontrado en ningún repositorio");
                        JOptionPane.showMessageDialog(null, "El archivo con nombre: " + arch + " no fue encontrado en ningún repositorio", "POPUP: Busqueda de archivo", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        //se verifica cuantos archivos existen
                        for(int i=0;i<array.size();i++){
                            ap=array.get(i);
                            if(apoyo.isEmpty())apoyo.add(ap);
                            for(int j=0;j<apoyo.size();j++){
                                objArchivo aux = apoyo.get(j);
                                if(!(aux.getMd5().equals(ap.getMd5()))){
                                    apoyo.add(aux);
                                }
                            }
                        }
                        
                        System.out.print("Tamaño: " + apoyo.size());

                        if(!(apoyo.size()==1)){
                            //si no es igual a 1 significa que hay varios archivos con el mismo nombre
                            //Le damos a elegir entre uno
                            in.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> Se encuentran varios archivos con el nombre: " + arch + " se da a elegir uno");
                            //ap=choseOne(apoyo);
                        }else{
                            ap=apoyo.get(0);
                        }

                        //generar conexión con el archivo que sea seleccionado
                        int iss = array.size();
                        iss-=1;
                        while(iss>=0){
                            objArchivo ux = array.get(iss);
                            if(!(ux.getMd5().equals(ap.getMd5()))){
                                array.remove(iss);
                                iss=(array.size()-1);
                            }else{
                                iss--;
                            }
                        }
                        int contador=array.size();
                        in.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> El archivo con nombre: " + arch + " fue encontrado " + contador + " veces");
                        for(int i = 0;i<contador;i++){
                            //iniciar conexión
                            objArchivo ux = array.get(i);
                            objPeticion o = new objPeticion();
                            o.setName(arch);
                            o.setPartes(contador);
                            o.setPosicion(i);
                            o.setDestino(ptoN);
                            o.setOrigen(Integer.parseInt(ux.getNodo()));
                            int ptoremoto = Integer.parseInt(ux.getNodo());
                            ptoremoto=ptoremoto+100;
                            clientFile cf = new clientFile(o, "localhost",ptoremoto);
                            Thread th=new Thread(cf);
                            th.start();
                            th.join();
                        }
                    }
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public objArchivo choseOne(ArrayList<objArchivo> ap) throws InterruptedException{
    objArchivo a,aux;
    setEspera(true);
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
                setEspera(false);
            }
    });
    frame.setVisible(true);
    while(getEspera()){
        Thread.sleep(1000);
    }
    
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

    public void setEspera(boolean espera) {
        this.espera = espera;
    }
    
    public boolean getEspera(){
        return espera;
    }
    
    
}
