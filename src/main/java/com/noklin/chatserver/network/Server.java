package com.noklin.chatserver.network;
 
import com.noklin.chatserver.Settings;  
import com.noklin.network.simplechat.ChatConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket; 
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author noklin
 */
public class Server {  
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<String , ChatConnection> connectionHub 
            = new ConcurrentHashMap();
    private final ServerSocket serverSocket;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(Settings.PORT
                , Settings.BACKLOG, InetAddress.getByName(Settings.IP));
    }
    
    public void start(){  
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!Thread.currentThread().isInterrupted()){  
                        executor.execute(new ConnectionHandler(serverSocket.accept(), connectionHub));
                    } 
                }catch(IOException ex){

                }  
            }
        });  
    }

    @Override
    public String toString() {
        return "Listen on: " + Settings.IP + ": " + Settings.PORT;
    } 
}

