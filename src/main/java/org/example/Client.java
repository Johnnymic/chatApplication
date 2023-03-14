package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements  Runnable{
   private  Socket socket;

   private BufferedReader input;
   private PrintWriter output;

   private String userName;

    @Override
    public void run() {
        try{
       socket = new Socket("localhost", 1500);
       input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       output = new PrintWriter( socket.getOutputStream(),true);
            InputHandler  inputHandler = new InputHandler();
            new Thread(inputHandler).start();

            String message;
            while((message= input.readLine())!=null){
                System.out.println(message);
            }

    }catch(IOException e){ shutdown();}

    }

    public void shutdown() {
        try {

        input.close();
        output.close();
        if (!socket.isClosed()) {
            socket.close();
         }
        }catch(IOException e){}
    }


    class InputHandler implements  Runnable{
        @Override
        public void run() {
            try{
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                while(true){
                        String message =inputReader.readLine();
                        if(message.equals("/quit")){
                            output.println(message);
                            inputReader.close();
                            shutdown();
                        }else{
                            output.println(message);
                        }
                }

            } catch (IOException e) {
                 shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client= new Client();
        client.run();
    }
}
