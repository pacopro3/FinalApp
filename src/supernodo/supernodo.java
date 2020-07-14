/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supernodo;
import interfaz.*;

/**
 *
 * @author Sweet
 */
public class supernodo {
    public static void main(String[] args) throws InterruptedException {
        Puerto p = new Puerto();
        p.setVisible(true);
        p.setTitle("Supernodo");
        int pto=0;
        String id;
        
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
        p.setVisible(false);
        p.dispose();
        //Se inicializa la interfaz del supernodo
    }
    
}
