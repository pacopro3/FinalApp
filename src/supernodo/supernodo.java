/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supernodo;
import interfaz.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.Enumeration;
import nodo.ClienteNodoMult;

/**
 *
 * @author Sweet
 */
public class supernodo {
    public static void main(String[] args) throws InterruptedException {
        int pto=4000;
        String hhost = "228.1.1.10";
        InetSocketAddress dir = new InetSocketAddress(pto);
        final SocketAddress remote = new InetSocketAddress(hhost, pto);
        final String persona;
        Puerto p = new Puerto();
        p.setVisible(true);
        p.setTitle("Supernodo");
        String id;
        
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
        //Se inicializa la interfaz del supernodo
        IntSupernodo isn = new IntSupernodo();
        isn.setVisible(true);
        isn.setTitle("localhost:" + String.valueOf(pto));
        ByteBuffer b = ByteBuffer.allocate(1024);
        String nuevos = "Nuevo<>SN<>" + persona;
        System.out.println("Texto: " + nuevos);
        b = ByteBuffer.wrap(nuevos.getBytes("UTF-8"),0,nuevos.length());
        cl.send(b, remote);
        b.clear();
        ClienteSNodoMult csm = new ClienteSNodoMult(persona,cl);
        Thread cliente = new Thread(csm);
        cliente.setName("supernodocliente");
        cliente.start();
        ServidorSNodoMult ssm= new ServidorSNodoMult(persona,cl,remote);
        Thread server = new Thread(ssm);
        server.setName("supernodoservidor");
        server.start();
        supernodoRMI serverRMI = new supernodoRMI("server", Integer.parseInt(persona), isn);
        Thread servrmi = new Thread(serverRMI);
        //servrmi.start();
        
        boolean f=true;
        int referencia = ssm.getNumconexiones();
            do{
                try {
                    if(serverRMI.getConexiones()!=referencia){
                        ssm.setNumconexiones(serverRMI.getConexiones());
                    }
                    if(isn.getClose()==true){
                        ByteBuffer be = ByteBuffer.allocate(1024);
                        String fin = "Cierre<>SN<>" + persona;
                        System.out.println("Texto: " + fin);
                        be = ByteBuffer.wrap(fin.getBytes("UTF-8"),0,fin.length());
                        cl.send(be, remote);
                        be.clear();
                        f=false;
                    }
                    Thread.sleep(500);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }while(f);
        
            isn.setVisible(false);
            isn.dispose();
            
            cliente.interrupt();
            server.interrupt();
            
        }catch (Exception e) {
            e.printStackTrace();
            //TODO: handle exception
        }
        
    }
    
}
