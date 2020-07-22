package headers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConexionRMI
{
    private final int PORT = 5000;   
    private final String HOST = "localhost";   
    protected ServerSocket ss;   
    protected Socket cs; 

    // Constructor
    public ConexionRMI(String tipo) throws IOException     
    { 
        if(tipo.equalsIgnoreCase("servidor"))
        {
            ss = new ServerSocket(PORT); 
            cs = new Socket(); 
        }
        else
        {
            cs = new Socket(HOST, PORT); 
        }
    }
    
    public ConexionRMI(String tipo, int pto) throws IOException     
    { 
        if(tipo.equalsIgnoreCase("servidor"))
        {
            ss = new ServerSocket(pto); 
            cs = new Socket(); 
        }
        else
        {
            cs = new Socket(HOST, pto); 
        }
    }
    public ConexionRMI(String tipo, int pto, String hhost) throws IOException     
    { 
        if(tipo.equalsIgnoreCase("servidor"))
        {
            ss = new ServerSocket(pto); 
            cs = new Socket(); 
        }
        else
        {
            cs = new Socket(hhost, pto); 
        }
    }
}
