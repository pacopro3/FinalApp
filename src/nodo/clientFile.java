/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodo;

import headers.objPeticion;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Sweet
 */
public class clientFile extends Thread{

    objPeticion obj;
    String servidor;
    int puerto;
    
    public clientFile() {
    }

    public clientFile(objPeticion obj, String servidor, int puerto) {
        this.obj = obj;
        this.servidor = servidor;
        this.puerto = puerto;
    }
    
    @Override
    public void run() {
        try
        {
            // Se abre el socket.
            Socket socket = new Socket(servidor, puerto);

            // Se env�a un mensaje de petici�n de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(obj);
            String directorio;
            String current = new java.io.File( "." ).getCanonicalPath();
            directorio = current + "\\src\\Folders\\" + obj.getDestino() + "\\";
            String fichero=directorio + obj.getName();
            FileOutputStream fos = new FileOutputStream(fichero,true);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            byte[] bb = null;
            int longitud = ois.readInt();
            bb = new byte[longitud];
            ois.read(bb, 0, longitud);
            fos.write(bb,0,bb.length);
            fos.close();
            ois.close();
            socket.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
}
