/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author Sweet
 */
public class ClienteNodoMult extends Thread{

    String persona;
    DatagramChannel cl;
    ArrayList<String> supernodos;

    public ClienteNodoMult(String persona, DatagramChannel cl) {
        this.persona = persona;
        this.cl = cl;
        supernodos = new ArrayList<String>();
    }
    
    
    
    @Override
    public void run() {
        try {
            final Selector selector_read = Selector.open();
            ByteBuffer bb;
            String puertoRMI = persona;
            cl.register(selector_read, SelectionKey.OP_READ);
            while (true){
                selector_read.select();
                Iterator<SelectionKey> iterator = selector_read.selectedKeys().iterator();
                while (iterator.hasNext()){
                    bb = ByteBuffer.allocate(1024);
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        DatagramChannel dc = (DatagramChannel) key.channel();
                        SocketAddress emisor = dc.receive(bb);
                        InetSocketAddress d = (InetSocketAddress)emisor;
                        String converted = new String(bb.array(), "UTF-8");
                        converted = converted.trim();
                        String msj[] = converted.split("<>");
                        if(msj[1].equals("SN")){
                            if(msj[0].equals("Nuevo")){
                                supernodos.add(msj[2]);
                            }else if(msj[0].equals("Cierre")){
                                supernodos.remove(msj[2]);
                            }
                        }
                        bb.clear();
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //Logger.getLogger(ReceiverApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(ReceiverApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   public void selectNodo(ArrayList <String> a){
       for(int i=0;i<a.size();i++){
           //Intenta establecer conexión con el servidor RMI
       }
   }
}
