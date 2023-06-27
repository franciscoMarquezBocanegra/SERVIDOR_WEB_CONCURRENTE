
package com.mycompany.sevidorconcurrente;

import com.mycompany.sevidorconcurrente.HttpServerController;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static HttpServer _instance = new HttpServer();
    private ExecutorService executorService;
    
    public static HttpServer getInstance() {
        return _instance;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 36000.");
            System.exit(1);
        }
        boolean flags = true;

        
        int numThreads = 5; 
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        while (flags) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

           
            HttpServerController httpServerController = new HttpServerController(clientSocket, "");
            executorService.execute((Runnable) httpServerController);
        }

        serverSocket.close();
        
      
        executorService.shutdown();
    }
    
    private static int getPort(){
        if(System.getenv("PORT") !=null) return Integer.parseInt(System.getenv("PORT")) ;
        return 36000;
    }
}