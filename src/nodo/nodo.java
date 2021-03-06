/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodo;
import interfaz.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Enumeration;

/**
 *
 * @author Sweet
 */
public class nodo {
    
    public static void main(String[] args) throws InterruptedException, SocketException {
        int pto=4000;
        String hhost = "228.1.1.10";
        InetSocketAddress dir = new InetSocketAddress(pto);
        final SocketAddress remote = new InetSocketAddress(hhost, pto);
        final String persona;
        Puerto p = new Puerto();
        p.setVisible(true);
        p.setTitle("Nodo");
        String id="no";
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface netint:Collections.list(nets)){
                //displayInterfaceInformation(netint);
            }
            NetworkInterface ni = NetworkInterface.getByName("eth2");
            DatagramChannel cl = DatagramChannel.open(StandardProtocolFamily.INET);
            cl.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            cl.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            cl.configureBlocking(false);
            cl.socket().bind(dir);
            InetAddress group = InetAddress.getByName(hhost);
            cl.join(group,ni);
            pto=0;
            
        while(pto==0){
            if(p.getButton()==true){
                try {
                    pto=Integer.parseInt(p.getJTextFiled());
                    //conectar al puerto indicado
                } catch (Exception e) {
                    //En caso de que el puerto ya esté usado
                    p.setJTextField("");
                    p.setButton(false);
                    pto=0;
                }finally{
                    Thread.sleep(500);
                }
            }
            Thread.sleep(500);
        }
        persona = String.valueOf(pto);
        p.setVisible(false);
        p.dispose();
        //Se inicializa la interfaz del nodo
        IntNodo in1 = new IntNodo();
        in1.setVisible(true);
        in1.setTitle("localhost:" + String.valueOf(pto));
        ByteBuffer b = ByteBuffer.allocate(1024);
        String nuevos = "Nuevo<>N<>" + persona;
        System.out.println("Texto: " + nuevos);
        b = ByteBuffer.wrap(nuevos.getBytes("UTF-8"),0,nuevos.length());
        cl.send(b, remote);
        b.clear();
        
        //Ejecuta el Thread del cliente multicast
        ClienteNodoMult cnm=new ClienteNodoMult(persona,cl,id);
        Thread cliente = new Thread(cnm);
        cliente.setName("nodo");
        cliente.start();
        
        Thread.sleep(4000);
        //se intenta escoger un supernodo
        boolean bool = cnm.selectNodo();
        if(bool){
            id=cnm.getSupernodo();
            in1.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> Se realiza la conexión de forma correcta al supernodo " + id);
        }else{
            in1.LOGS(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "-> Fallo en la conexión de un supernodo. Se finaliza aplicación");
            System.exit(-1);
        }
        
        System.err.println("ID ID ID:" + id);
        in1.setSupernodo(id);
        clientenodoRMI rmi = new clientenodoRMI("cliente", Integer.parseInt(id), null, in1);
        rmi.MainCliente();
        Thread rmiclient = new Thread(rmi);
        rmiclient.setName("ClienteRMI");
        rmiclient.start();
        
        serverFile sf=new serverFile(pto+100);
        Thread fileserver = new Thread(sf);
        fileserver.setName("ServidorF");
        fileserver.start();
        
        //notificamos de la nueva conexión de Nodo a Supernodo
        String conex = "Conexion<>N<>" + id + "<>" + persona;
        System.out.println("Texto: " + conex);
        b = ByteBuffer.wrap(conex.getBytes("UTF-8"),0,conex.length());
        cl.send(b, remote);
        b.clear();
        
        boolean f=true;
            do{
                try {
                if(in1.getBandera()==true){
                    System.out.println("Se activa la busqueda");
                    rmi.buscarArchivo(in1.getText());
                    in1.setBandera(false);
                }
                if(in1.getClose()==true){
                    ByteBuffer be = ByteBuffer.allocate(1024);
                    String fin = "Cierre<>N<>"+ id + "<>" + persona;
                    System.out.println("Texto: " + fin);
                    be = ByteBuffer.wrap(fin.getBytes("UTF-8"),0,fin.length());
                    cl.send(be, remote);
                    Thread.sleep(3000);
                    be.clear();
                    f=false;
                }
                Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }while(f);
        
            in1.setVisible(false);
            in1.dispose();
            
            cliente.interrupt();
            rmiclient.interrupt();
            System.exit(0);
        }catch (Exception e) {
            e.printStackTrace();
            //TODO: handle exception
        }
    }
        
         static void displayInterfaceInformation(NetworkInterface netint) throws SocketException{
            System.out.printf("Interfaz:%s\n",netint.getDisplayName());
            System.out.printf("Nombre:%s\n",netint.getName());
            Enumeration<InetAddress> inetAdresses = netint.getInetAddresses();
            for(InetAddress inetAddress:Collections.list(inetAdresses)){
                System.out.printf("InetAddress:%s\n",inetAddress);
            }
            System.out.println("\n");    
    }
}
