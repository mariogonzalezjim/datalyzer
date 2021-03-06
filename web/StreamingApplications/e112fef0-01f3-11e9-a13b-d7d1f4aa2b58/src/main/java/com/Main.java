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
        listTopic.add("e112fef0-01f3-11e9-a13b-d7d1f4aa2b58.2");
        KafkaManager kafkaManager= new KafkaManager(listTopic);
        kafkaManager.openKafkaChannels();

        /* PRODUCERS and CONSUMERS*/
        listTopic = new ArrayList<String>();
        listTopic.add("e112fef0-01f3-11e9-a13b-d7d1f4aa2b58.2");
        ProducerTwitter1 threadProducerTwitter1 = new ProducerTwitter1(listTopic);
        threadProducerTwitter1.start();

        listTopic = new ArrayList<String>();
        listTopic.add("e112fef0-01f3-11e9-a13b-d7d1f4aa2b58.2");
        ConsumerSimple2 threadConsumerSimple2 = new ConsumerSimple2(listTopic);
        threadConsumerSimple2.start();

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
            //send appid
            ConsumerClass.writer.write("e112fef0-01f3-11e9-a13b-d7d1f4aa2b58"+"$#$\n");
            ConsumerClass.writer.flush();

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
            threadProducerTwitter1.resumeThread();
            threadConsumerSimple2.resumeThread();
                }else if(message.equals("pause")){
                    System.out.println("He pausado");
     
               /* RESUME THREADS */
            threadProducerTwitter1.pauseThread();
            threadConsumerSimple2.pauseThread();
                }

            }
     
           /* CLOSE THREADS */
         threadProducerTwitter1.stopThread();
         threadProducerTwitter1.join();

         threadConsumerSimple2.stopThread();
         threadConsumerSimple2.join();

           // close socket
            s.close();
           // close kafkamanager
           // kafkaManager.closeKafkaChannels();
           System.exit(0);

        System.out.print("Fin");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}