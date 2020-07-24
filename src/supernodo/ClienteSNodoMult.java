/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supernodo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author Sweet
 */
public class ClienteSNodoMult extends Thread{

    String persona;
    DatagramChannel cl;

    public ClienteSNodoMult(String persona, DatagramChannel cl) {
        this.persona = persona;
        this.cl = cl;
    }
    
    
    
    @Override
    public void run() {
    try {
        final Selector selector_read = Selector.open();
        cl.register(selector_read, SelectionKey.OP_READ);
        ByteBuffer bb;
        String people = persona;
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
                        System.out.println("Supernodo" + converted);
                    }else if(msj[1].equals("N")){
                        System.err.println("Nodo" + converted);
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
    
   
}
