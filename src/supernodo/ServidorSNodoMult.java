/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supernodo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 *
 * @author Sweet
 */
public class ServidorSNodoMult extends Thread{
    String persona;
    DatagramChannel dc;
    SocketAddress remote;
    int numconexiones;
    public ServidorSNodoMult(String persona,DatagramChannel dc, SocketAddress remote) {
        this.dc=dc;
        this.persona=persona;
        this.remote =remote;
        numconexiones=2;
    }

    @Override
    public void run() {
         try {
                final Selector selector_write = Selector.open();
                dc.register(selector_write, SelectionKey.OP_WRITE);
                    ByteBuffer bb;
                    String people = persona;
                    while (true) {
                        bb = ByteBuffer.allocate(1024);
                        selector_write.select();
                        Iterator<SelectionKey> iterator = selector_write.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            if (key.isWritable()) {
                                String texto;
                                DatagramChannel ch = (DatagramChannel)key.channel();
                                bb.clear();
                                texto="Vivo<>SN<>" + persona + "<>" + numconexiones;
                                bb = ByteBuffer.wrap(texto.getBytes("UTF-8"),0,texto.length());
                                ch.send(bb, remote);
                                try{Thread.sleep(5000);}catch(Exception e){e.printStackTrace();continue;}
                                continue;  
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
