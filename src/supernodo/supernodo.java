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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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
        ArrayList<String> nodos=new ArrayList<>();
        ArrayList<String> nodosh=new ArrayList<>();
        ArrayList<clientesupernodoRMI> arrayofthreads = new ArrayList<>();
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
        supernodoRMI serverRMI = new supernodoRMI("servidor", Integer.parseInt(persona), isn);
        Thread servrmi = new Thread(serverRMI);
        serverRMI.setSupernodoRMI(serverRMI);
        servrmi.setName("servidorRMI");
        servrmi.start();
        
        boolean f=true;
            do{
                try {
                    
                    if(csm.getNconexion().length()>0){
                        String nuevo=csm.getNconexion();
                        nodosh.add(nuevo);
                        ssm.setNumconexiones((2-nodosh.size()));
                    }
                    
                    if(csm.getNmuerto().length()>0){
                        String nuevo=csm.getNmuerto();
                        nodosh.remove(nuevo);
                        ssm.setNumconexiones((2-nodosh.size()));
                        isn.removeMasivoN(nuevo);
                    }
                    
                    
                    if(csm.getSNvivo().length()>0){
                        String nuevo=csm.getSNvivo();
                        csm.setSNvivo("");
                        if(!nodos.contains(nuevo)){
                            clientesupernodoRMI cRMI = new clientesupernodoRMI("cliente",Integer.parseInt(nuevo),null,isn);
                            cRMI.MainCliente();
                            cRMI.start();
                            nodos.add(nuevo);
                            arrayofthreads.add(cRMI);
                            isn.addSupernodos(nuevo);
                        }
                    }
                    
                    if(csm.getSNmuerto().length()>0){
                        String nuevo=csm.getSNmuerto();
                        csm.setSNmuerto("");
                        if(nodos.contains(nuevo)){
                            int hilo=nodos.indexOf(nuevo);
                            clientesupernodoRMI cRMI = arrayofthreads.get(hilo);
                            cRMI.interrupt();
                            arrayofthreads.remove(hilo);
                            nodos.remove(hilo);
                            isn.removeSuperNodo(nuevo);
                            isn.removeMasivoSN(nuevo);
                        }
                    }
                    
                    if(isn.getClose()==true){
                        ByteBuffer be = ByteBuffer.allocate(1024);
                        String fin = "Cierre<>SN<>" + persona;
                        System.out.println("Texto: " + fin);
                        be = ByteBuffer.wrap(fin.getBytes("UTF-8"),0,fin.length());
                        cl.send(be, remote);
                        Thread.sleep(2000);
                        be.clear();
                        f=false;
                    }
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.toString();
                }
            }while(f);
        
            isn.setVisible(false);
            isn.dispose();
            
            cliente.interrupt();
            server.interrupt();
            servrmi.interrupt();
            
        }catch (Exception e) {
            e.printStackTrace();
            //TODO: handle exception
        }
        
    }
    
}
