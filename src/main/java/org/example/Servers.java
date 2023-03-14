package org.example;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class

Servers implements  Runnable{

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private ArrayList<ClientHandler> clientConnector;
    public Servers() {
        this.serverSocket = serverSocket;
        this.clientConnector = new ArrayList<>();
    }

    @Override
    public void run() {


        // the server will keep listening for  incoming connections and accept requestand then we going a new open  thread handler

          try {
                 serverSocket = new ServerSocket(1500);
                 executorService = Executors.newCachedThreadPool();
               while(true) {
                   Socket socket = serverSocket.accept();
                   executorService = Executors.newCachedThreadPool();
                   ClientHandler clientHandler = new ClientHandler(socket);
                   executorService.execute(clientHandler);
                   clientConnector.add(clientHandler);
               }

             } catch (IOException e) {
                 shutdown();
             }

    }

    public void  broadcastMessage(String message){
        for(ClientHandler clientHandler : clientConnector){
            if(clientHandler!=null){
                clientHandler.sendMessage(message);
            }
        }

    }

    public void shutdown(){
        try {

            executorService.shutdown();
            if (!serverSocket.isClosed()) {
                serverSocket.close();


                for(ClientHandler clientHandler : clientConnector){
                     shutdown();
                }
            }
        }catch(IOException e){}

    }
    class ClientHandler implements  Runnable {
        private Socket client;
        private PrintWriter outPut;
        private BufferedReader inPut; // buffer reader will be used to get the stream fromthe socket so that when a client get send something we are going to getfrom the in
        private String nickName;

        public ClientHandler(Socket socket) {
            this.client = socket;
        }

        @Override
        public void run() {
            try {
                inPut = new BufferedReader(new InputStreamReader(client.getInputStream()));
                outPut = new PrintWriter(client.getOutputStream(), true);
                outPut.println("please enter a nick name nickname: "); // let us send something to the client and to get a message from th client we used input.readline
                nickName = inPut.readLine(); // wait for input from the client
                System.out.println(nickName + " is  connected! ");
                broadcastMessage(nickName + "  join the chat ");
                String message;
                while ((message = inPut.readLine()) != null) {
                    if (message.startsWith("/nick ")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcastMessage( nickName +" renamed themselves  to " + messageSplit[1]);
                            System.out.println( nickName + " renamed themselves  to " + messageSplit[1]);
                            nickName = messageSplit[1];
                            outPut.println("Successfully change nickname to " + nickName);
                        } else {
                            outPut.println("no nick name provided");
                        }
                    } else if (message.startsWith("/quit")) {

                        broadcastMessage(nickName + " has left the chat");
                         shutdown();

                    } else {
                        broadcastMessage(nickName + ": " + message);

                    }
                }

            } catch (IOException e) {
                shutdown();
            }

        }

        public void sendMessage(String message) {
            outPut.println(message);
        }

        public void shutdown() {
            try {
                inPut.close();
                outPut.close();
                if (client != null) {
                    client.close();
                }

            } catch (IOException exception) {
            }
        }
    }
        public static void main(String[] args) {
            Servers servers = new Servers();
            servers.run();
        }



}
