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
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author Sweet
 */
public class ClienteNodoMult extends Thread{

    String persona,supernodo;
    DatagramChannel cl;
    ArrayList<String> supernodos;

    public ClienteNodoMult(String persona, DatagramChannel cl, String supernodo) {
        this.persona = persona;
        this.cl = cl;
        supernodos = new ArrayList<String>();
        this.supernodo=supernodo;
    }
    
    
    
    @Override
    public void run() {
        try {
            final Selector selector_read = Selector.open();
                cl.register(selector_read, SelectionKey.OP_READ);
            ByteBuffer bb;
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
                            if(msj[0].equals("Vivo")){
                                int conexiones=Integer.parseInt(msj[3]);
                                if(conexiones==0){
                                    if(supernodos.contains(msj[2])) supernodos.remove(msj[2]);
                                }else{
                                    if(!(supernodos.contains(msj[2]))) supernodos.add(msj[2]);
                                }
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

    public String getSupernodo() {
        return supernodo;
    }

    public void setSupernodo(String supernodo) {
        this.supernodo = supernodo;
    }
    
   public boolean selectNodo(){
       try{
        Random r =  new Random();
        int result = r.nextInt(supernodos.size());
        if(result==supernodos.size())result--;
        supernodo=supernodos.get(result);
        System.err.println("Supernodo seleccionado:" + supernodo);
        return true;
       }catch(Exception e){
                JOptionPane.showMessageDialog(null, "No se encuentra supernodo disponible. Favor de intentarlo más tarde","POPUP: Error nodo", JOptionPane.INFORMATION_MESSAGE);
                return false;
           
       }
   }
}
