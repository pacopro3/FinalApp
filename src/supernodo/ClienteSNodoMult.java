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
import java.util.Iterator;

/**
 *
 * @author Sweet
 */
public class ClienteSNodoMult extends Thread{

    String persona;
    DatagramChannel cl;
    String SNvivo,SNmuerto,Nconexion,Nmuerto;

    public ClienteSNodoMult(String persona, DatagramChannel cl){
        this.persona = persona;
        this.cl = cl;
        SNvivo="";
        SNmuerto="";
        Nconexion="";
        Nmuerto="";
    }
    
    
    
    @Override
    public void run() {
        try {
            final Selector selector_read = Selector.open();
            cl.register(selector_read, SelectionKey.OP_READ);
            ByteBuffer bb;
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
                            if(msj[0].equals("Vivo")){
                                if(!(msj[2].equals(persona))){
                                    setSNvivo(msj[2]);
                                    Thread.sleep(2000);
                                }
                            }else if(msj[0].equals("Cierre")){
                                if(!(msj[2].equals(persona))){
                                    setSNmuerto(msj[2]);
                                    Thread.sleep(2000);
                                }
                            }
                        }else if(msj[1].equals("N")){
                            if(msj[0].equals("Conexion")){
                                if(msj[2].equals(persona)){
                                        setNconexion(msj[3]);
                                        Thread.sleep(2000);
                                    }
                            }else if(msj[0].equals("Cierre")){
                                if(msj[2].equals(persona)){
                                        setNmuerto(msj[3]);
                                        Thread.sleep(2000);
                                    }
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
        } catch (InterruptedException ex) {
            //Logger.getLogger(ClienteSNodoMult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getSNmuerto() {
        return SNmuerto;
    }

    public String getSNvivo() {
        return SNvivo;
    }

    public void setSNmuerto(String SNmuerto) {
        this.SNmuerto = SNmuerto;
    }

    public void setSNvivo(String SNvivo) {
        this.SNvivo = SNvivo;
    }

    public void setNconexion(String Nconexion) {
        this.Nconexion = Nconexion;
    }

    public String getNconexion() {
        return Nconexion;
    }

    public String getNmuerto() {
        return Nmuerto;
    }

    public void setNmuerto(String Nmuerto) {
        this.Nmuerto = Nmuerto;
    }
    
}
