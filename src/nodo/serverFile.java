/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodo;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import headers.objPeticion;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Sweet
 */
public class serverFile extends Thread{

    int puerto;
    public serverFile(int puerto) {
        this.puerto=puerto;
    }

    @Override
    public void run() {
        while(true){
            try
            {
                ServerSocket socketServidor = new ServerSocket(puerto);
                Socket cliente = socketServidor.accept();
                System.out.println("Aceptado cliente");
                ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
                objPeticion mensaje =(objPeticion) ois.readObject();
                System.out.println("Se realiza la solicitud del archivo:" + mensaje.getName());
                enviaFichero(mensaje,new ObjectOutputStream(cliente.getOutputStream()));
                cliente.close();
                socketServidor.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private void enviaFichero(objPeticion mensaje, ObjectOutputStream oos){
        try{
            String directorio;
            String current = new java.io.File( "." ).getCanonicalPath();
            directorio = current + "\\src\\Folders\\" + mensaje.getOrigen() + "\\";
            String fichero=directorio + mensaje.getName();
            FileInputStream fis = new FileInputStream(fichero);
            File file = new File(fichero);
            double longitud = file.length();
            //seleccionamos que parte vamos a enviar
            double cuantoenviar = longitud/(mensaje.getPartes());
            double dondeiniciar = cuantoenviar*(mensaje.getPosicion());
            if(mensaje.getPosicion()==(mensaje.getPartes()-1)){
                cuantoenviar=longitud-dondeiniciar;
            }
            byte[] bb = new byte[(int)cuantoenviar];
            int leidos = fis.read(bb,(int)dondeiniciar,(int)cuantoenviar);
            oos.writeInt((int)cuantoenviar);
            oos.write(bb,0,bb.length);   
            oos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
