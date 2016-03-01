package com.noklin.chatserver;    
import com.noklin.chatserver.network.Server;
import java.io.IOException; 

/**
 *
 * @author noklin
 */

public class Launcher {
    public static void main(String[] args) {    
         
        try{ 
            Server server = new Server();
            server.start();
            System.out.println("Server started!\n" + server);
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }  
    }
}