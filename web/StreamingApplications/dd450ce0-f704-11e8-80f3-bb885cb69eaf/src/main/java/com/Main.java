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
        listTopic.add("dd450ce0-f704-11e8-80f3-bb885cb69eaf.3");
        listTopic.add("dd450ce0-f704-11e8-80f3-bb885cb69eaf.4");
        KafkaManager kafkaManager= new KafkaManager(listTopic);
        kafkaManager.openKafkaChannels();

        /* PRODUCERS and CONSUMERS*/
        listTopic = new ArrayList<String>();
        listTopic.add("dd450ce0-f704-11e8-80f3-bb885cb69eaf.3");
        ProducerSocket1 threadProducerSocket1 = new ProducerSocket1(listTopic);
        threadProducerSocket1.start();

        listTopic = new ArrayList<String>();
        listTopic.add("dd450ce0-f704-11e8-80f3-bb885cb69eaf.4");
        ProducerSocket2 threadProducerSocket2 = new ProducerSocket2(listTopic);
        threadProducerSocket2.start();

        listTopic = new ArrayList<String>();
        listTopic.add("dd450ce0-f704-11e8-80f3-bb885cb69eaf.3");
        ConsumerSimple3 threadConsumerSimple3 = new ConsumerSimple3(listTopic);
        threadConsumerSimple3.start();

        listTopic = new ArrayList<String>();
        listTopic.add("dd450ce0-f704-11e8-80f3-bb885cb69eaf.4");
        ConsumerSimple4 threadConsumerSimple4 = new ConsumerSimple4(listTopic);
        threadConsumerSimple4.start();

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
            ConsumerClass.writer.write("dd450ce0-f704-11e8-80f3-bb885cb69eaf"+"$#$\n");
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
            threadProducerSocket1.resumeThread();
            threadProducerSocket2.resumeThread();
            threadConsumerSimple3.resumeThread();
            threadConsumerSimple4.resumeThread();
                }else if(message.equals("pause")){
                    System.out.println("He pausado");
     
               /* RESUME THREADS */
            threadProducerSocket1.pauseThread();
            threadProducerSocket2.pauseThread();
            threadConsumerSimple3.pauseThread();
            threadConsumerSimple4.pauseThread();
                }

            }
     
           /* CLOSE THREADS */
         threadProducerSocket1.stopThread();
         threadProducerSocket1.join();

         threadProducerSocket2.stopThread();
         threadProducerSocket2.join();

         threadConsumerSimple3.stopThread();
         threadConsumerSimple3.join();

         threadConsumerSimple4.stopThread();
         threadConsumerSimple4.join();

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