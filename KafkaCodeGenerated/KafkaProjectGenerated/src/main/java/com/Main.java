package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.ArrayList;
import com.*;

public class Main {
    
    public static void main(String[] argv) throws Exception {



        ArrayList<String> listTopic = new ArrayList<String>();
        KafkaManager kafkaManager= new KafkaManager(listTopic);
        kafkaManager.openKafkaChannels();

        /* PRODUCERS and CONSUMERS*/
        listTopic = new ArrayList<String>();
        ProducerTwitter8 threadProducerTwitter8 = new ProducerTwitter8(listTopic);
        threadProducerTwitter8.start();

        //wait for initialization
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            //Handle exception
        }

        String serverAddress = "127.0.0.1";
        Socket s = null;
        try {

            s = new Socket(serverAddress, 443);
            ConsumerClass.sock=s;
            ConsumerClass.writer = new PrintWriter(ConsumerClass.sock.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //receive data
            int flagExit=0;
            while(flagExit==0) {

                while (input.ready() == false && s.isConnected() == true) {
                }

                String message= input.readLine();

                if(message.equals("exit")){
                    flagExit=1;
                }else if(message.equals("resume")){
                    System.out.println("He renaudado");
     
               /* RESUME THREADS */
            threadProducerTwitter8.resumeThread();
                }else if(message.equals("pause")){
                    System.out.println("He pausado");
     
               /* RESUME THREADS */
            threadProducerTwitter8.pauseThread();
                }

            }
     
           /* CLOSE THREADS */
         threadProducerTwitter8.stopThread();
         threadProducerTwitter8.join();

           // close socket
            s.close();
           // close kafkamanager
           // kafkaManager.closeKafkaChannels();

        System.out.print("Fin");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}